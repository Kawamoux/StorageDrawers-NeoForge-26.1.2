package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.base.Suppliers;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.modelprops.RenderDataProvider;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.ModelDecorator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.extensions.IForgeBlockStateModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlatformDecoratedModel<C extends ModelContext> extends ParentModel implements IForgeBlockStateModel
{
    public static final ModelProperty<BlockState> BLOCKSTATE = new ModelProperty<>();

    private final ModelDecorator<C> decorator;
    private final ModelContextSupplier<C> contextSupplier;

    public PlatformDecoratedModel (BlockStateModel parent, ModelDecorator<C> decorator, ModelContextSupplier<C> contextSupplier) {
        super(parent);
        this.decorator = decorator;
        this.contextSupplier = contextSupplier;
    }

    @Override
    public @NotNull ModelData getModelData (@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return modelData.derive().with(BLOCKSTATE, state).build();
    }

    @Override
    public void collectParts (RandomSource random, List<BlockModelPart> dest, ModelData data, @Nullable RenderType renderType) {
        BlockState state = data.get(BLOCKSTATE);
        if (state == null) {
            parent.collectParts(random, dest, data, renderType);
            return;
        }

        Supplier<C> supplier = () -> contextSupplier.makeContext(state, random, data);

        if (decorator.shouldRenderBase(supplier))
            parent.collectParts(random, dest, data, renderType);

        Consumer<BlockStateModel> emitModel = (model) -> {
            if (model != null) {
                model.collectParts(random, dest, data, renderType);
            }
        };

        try {
            decorator.emitQuads(supplier, emitModel);
        } catch (Exception e) { }
    }

    public static class PlatformDecoratedItemModel implements ItemModel
    {
        ResourceLocation location;
        String variant;
        PlatformDecoratedModel<?> parent;
        BlockStateModel model;
        ItemStack stack;
        BlockState state;
        ModelRenderProperties properties;

        private final Supplier<Vector3f[]> extents;

        public PlatformDecoratedItemModel (ResourceLocation location, String variant, ModelRenderProperties properties) {
            this.location = location;
            this.variant = variant;
            this.properties = properties;

            this.extents = Suppliers.memoize(() -> {
                Vector3f[] ext = new Vector3f[2];
                ext[0] = new Vector3f(0.0f, 0.0f, 0.0f);
                ext[1] = new Vector3f(1.0f, 1.0f, 1.0f);
                return ext;
            });
        }

        @Override
        public void update (ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
            if (state == null) {
                var blockOption = BuiltInRegistries.BLOCK.get(location);
                if (blockOption.isEmpty())
                    return;

                Block block = blockOption.get().value();
                state = block.defaultBlockState();

                String[] props = variant.split(",");
                for (String prop : props) {
                    String[] keyVal = prop.split("=");
                    if (keyVal.length != 2) continue;

                    String key = keyVal[0].trim();
                    String value = keyVal[1].trim();

                    Property<?> property = state.getBlock().getStateDefinition().getProperty(key);
                    if (property != null)
                        state = setProperty(state, property, value);
                }
            }

            if (parent == null) {
                BlockStateModel stored = ItemModelStore.models.get(state);
                if (stored instanceof PlatformDecoratedModel<?> p)
                    parent = p;
            }

            if ((stack == null || !ItemStack.isSameItemSameComponents(stack, itemStack)) && parent != null) {
                stack = itemStack;
                model = new ItemRender<>(parent, itemStack);
            }

            if (model != null) {
                List<BlockModelPart> parts = new ArrayList<>();
                model.collectParts(null, parts);
                Map<RenderType, ItemStackRenderState.LayerRenderState> layers = new HashMap<>();
                for (BlockModelPart part : parts) {
                    RenderType type = part.getRenderType(state);
                    if (!layers.containsKey(type)) {
                        ItemStackRenderState.LayerRenderState renderState = itemStackRenderState.newLayer();
                        layers.put(type, renderState);

                        RenderType itemRenderType = null;
                        if (type == RenderType.solid())
                            itemRenderType = Sheets.solidBlockSheet();
                        if (type == RenderType.cutoutMipped() || type == RenderType.cutout())
                            itemRenderType = Sheets.cutoutBlockSheet();
                        else if (type == RenderType.translucent())
                            itemRenderType = Sheets.translucentItemSheet();

                        renderState.setRenderType(itemRenderType);
                        renderState.setExtents(extents);
                    }

                    ItemStackRenderState.LayerRenderState layer = layers.get(type);
                    properties.applyToLayer(layer, itemDisplayContext);

                    layer.prepareQuadList().addAll(part.getQuads(null));
                    for (Direction direction : Direction.values())
                        layer.prepareQuadList().addAll(part.getQuads(direction));
                }
                int x = 5;
            }
        }

        private static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, String valueName) {
            Optional<T> parsed = property.getValue(valueName);
            return parsed.map(v -> state.setValue(property, v)).orElse(state);
        }

        public record Unbaked (ResourceLocation model, String variant) implements ItemModel.Unbaked {
            public static final MapCodec<PlatformDecoratedItemModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((builder) ->
                builder.group(
                    ResourceLocation.CODEC.fieldOf("model").forGetter(PlatformDecoratedItemModel.Unbaked::model),
                    Codec.STRING.fieldOf("variant").forGetter(PlatformDecoratedItemModel.Unbaked::variant)
                ).apply(builder, PlatformDecoratedItemModel.Unbaked::new)
            );

            @Override
            public MapCodec<? extends ItemModel.Unbaked> type () {
                return MAP_CODEC;
            }

            @Override
            public ItemModel bake (BakingContext bakingContext) {
                ModelBaker modelbaker = bakingContext.blockModelBaker();
                ResolvedModel resolvedmodel = modelbaker.getModel(ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID, "block/oak_full_drawers_2"));
                TextureSlots textureslots = resolvedmodel.getTopTextureSlots();

                ModelRenderProperties modelrenderproperties = ModelRenderProperties.fromResolvedModel(modelbaker, resolvedmodel, textureslots);
                return new PlatformDecoratedItemModel(model, variant, modelrenderproperties);
            }

            @Override
            public void resolveDependencies (Resolver resolver) {
                resolver.markDependency(model);
                resolver.markDependency(ResourceLocation.fromNamespaceAndPath(StorageDrawers.MOD_ID, "block/oak_full_drawers_2"));
            }
        }
    }

    public static class ItemRender<C extends ModelContext> extends ParentModel
    {
        PlatformDecoratedModel<C> parent;
        private ItemStack stack;

        public ItemRender (PlatformDecoratedModel<C> parent, ItemStack stack) {
            super(parent);
            this.parent = parent;
            this.stack = stack;
        }

        @Override
        public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            List<BakedQuad> quads = new ArrayList<>();

            Supplier<C> supplier = () -> parent.contextSupplier.makeContext(stack);
            ModelDecorator<C> decorator = parent.decorator;
            if (decorator.shouldRenderBase(supplier, stack))
                quads.addAll(parent.getQuads(state, side, rand));

            BiConsumer<BakedModel, RenderType> emitModel = (model, renderType) -> {
                if (model != null)
                    quads.addAll(model.getQuads(state, side, rand));
            };

            try {
                decorator.emitItemQuads(supplier, emitModel, stack);
            } catch (Exception e) {
                return quads;
            }

            return quads;
        }

        @Override
        public TextureAtlasSprite getParticleIcon (ModelData data) {
            return parent.getParticleIcon(data);
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes (BlockState state, RandomSource rand, ModelData data) {
            return parent.getRenderTypes(state, rand, data);
        }
    }
}