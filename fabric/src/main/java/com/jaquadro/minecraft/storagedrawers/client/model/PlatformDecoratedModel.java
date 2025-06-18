package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.base.Suppliers;
import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.decorator.ModelDecorator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PlatformDecoratedModel<C extends ModelContext> extends ParentModel implements FabricBlockStateModel
{
    private final ModelDecorator<C> decorator;
    private final ModelContextSupplier<C> contextSupplier;
    private final ItemStack stack;

    private static Map<BlockStateModel, Mesh> meshCache = new HashMap<>();
    private static RenderMaterial cutoutMat;
    private static RenderMaterial transMat;

    public PlatformDecoratedModel (BlockStateModel parent, ModelDecorator<C> decorator, ModelContextSupplier<C> contextSupplier) {
        super(parent);
        this.decorator = decorator;
        this.contextSupplier = contextSupplier;
        this.stack = null;
    }

    public PlatformDecoratedModel (PlatformDecoratedModel<C> p, ItemStack stack) {
        super(p.parent);
        this.decorator = p.decorator;
        this.contextSupplier = p.contextSupplier;
        this.stack = stack;
    }

    @Override
    public void emitQuads (QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
        if (state == null) {
            parent.emitQuads(emitter, blockView, pos, null, random, cullTest);
            return;
        }

        FabricBlockView fabricView = blockView;
        if (fabricView == null)
            return;

        Object renderData = fabricView.getBlockEntityRenderData(pos);
        Supplier<C> supplier = () -> contextSupplier.makeContext(state, random, renderData);

        if (decorator.shouldRenderBase(supplier))
            parent.emitQuads(emitter, blockView, pos, state, random, cullTest);

        Consumer<BlockStateModel> emitModelOpaque = (model) -> {
            if (model != null)
                model.emitQuads(emitter, blockView, pos, state, random, cullTest);
        };

        Consumer<BlockStateModel> emitModelTransparent = (model) -> {
            if (model != null) {
                Mesh mesh = getMesh(model, state, random, RenderType.translucent());
                mesh.outputTo(emitter);
            }
        };

        try {
            decorator.emitQuads(supplier, emitModelOpaque, RenderType.solid());
            decorator.emitQuads(supplier, emitModelTransparent, RenderType.translucent());
        } catch (Exception e) { }
    }

    private Mesh getMesh (BlockStateModel model, BlockState state, RandomSource randomSource, RenderType renderType) {
        if (meshCache.containsKey(model))
            return meshCache.get(model);

        Mesh mesh = buildMesh(model, state, randomSource, renderType);
        meshCache.put(model, mesh);
        return mesh;
    }

    private Mesh buildMesh (BlockStateModel model, BlockState state, RandomSource randomSource, RenderType renderType) {
        Renderer render = Renderer.get();
        RenderMaterial mat = null;

        if (renderType == RenderType.cutoutMipped()) {
            if (cutoutMat == null)
                cutoutMat = render.materialFinder().blendMode(BlendMode.CUTOUT_MIPPED).find();
            mat = cutoutMat;
        }
        else if (renderType == RenderType.translucent()) {
            if (transMat == null)
                transMat = render.materialFinder().blendMode(BlendMode.TRANSLUCENT).find();
            mat = transMat;
        }

        if (mat == null)
            return null;

        MutableMesh builder = render.mutableMesh();
        QuadEmitter quadEmit = builder.emitter();

        List<BlockModelPart> parts = new ArrayList<>();
        model.collectParts(randomSource, parts);

        for (BlockModelPart part : parts) {
            for (var d : Direction.values()) {
                for (var quad : part.getQuads(d)) {
                    quadEmit.fromVanilla(quad, mat, d).emit();
                }
            }
            for (var quad : part.getQuads(null)) {
                quadEmit.fromVanilla(quad, mat, null).emit();
            }
        }

        return builder.immutableCopy();
    }

    public static class PlatformDecoratedItemModel implements ItemModel
    {
        private final ResourceLocation location;
        private final String variant;
        private final ModelRenderProperties properties;
        private final Supplier<Vector3f[]> extents;

        PlatformDecoratedModel<? extends ModelContext> parent;
        BlockStateModel model;
        ItemStack stack;
        BlockState state;

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

            if ((stack == null || !ItemStack.isSameItemSameComponents(stack, itemStack)) && parent != null) {
                stack = itemStack;
                model = new PlatformDecoratedModel<>(parent, itemStack);
            }

            if (parent == null) {
                BlockStateModel stored = ItemModelStore.models.get(state);
                if (stored instanceof PlatformDecoratedModel<?> p)
                    parent = p;
            }

            if (model != null) {
                Map<RenderType, ItemStackRenderState.LayerRenderState> layers = new HashMap<>();
                for (var renderType : List.of(RenderType.solid(), RenderType.cutoutMipped(), RenderType.translucent())) {
                    List<BlockModelPart> parts = new ArrayList<>();
                    Consumer<BlockStateModel> emitModel = (model) -> {
                        if (model != null)
                            model.collectParts(null, parts);
                    };

                    PlatformDecoratedModel<ModelContext> pd = (PlatformDecoratedModel<ModelContext>) parent;
                    Supplier<ModelContext> supplier = () -> pd.contextSupplier.makeContext(stack);
                    pd.decorator.emitItemQuads(supplier, emitModel, stack, renderType);

                    if (parts.isEmpty())
                        continue;

                    if (!layers.containsKey(renderType)) {
                        ItemStackRenderState.LayerRenderState renderState = itemStackRenderState.newLayer();
                        layers.put(renderType, renderState);

                        RenderType itemRenderType = null;
                        if (renderType == RenderType.solid())
                            itemRenderType = Sheets.solidBlockSheet();
                        if (renderType == RenderType.cutoutMipped() || renderType == RenderType.cutout())
                            itemRenderType = Sheets.cutoutBlockSheet();
                        else if (renderType == RenderType.translucent())
                            itemRenderType = Sheets.translucentItemSheet();

                        renderState.setRenderType(itemRenderType);
                        renderState.setExtents(extents);
                    }

                    for (BlockModelPart part : parts) {
                        ItemStackRenderState.LayerRenderState layer = layers.get(renderType);
                        properties.applyToLayer(layer, itemDisplayContext);

                        layer.prepareQuadList().addAll(part.getQuads(null));
                        for (Direction direction : Direction.values())
                            layer.prepareQuadList().addAll(part.getQuads(direction));
                    }
                }
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
                ResolvedModel resolvedmodel = modelbaker.getModel(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "block/oak_full_drawers_2"));
                TextureSlots textureslots = resolvedmodel.getTopTextureSlots();

                ModelRenderProperties modelrenderproperties = ModelRenderProperties.fromResolvedModel(modelbaker, resolvedmodel, textureslots);
                return new PlatformDecoratedItemModel(model, variant, modelrenderproperties);
            }

            @Override
            public void resolveDependencies (Resolver resolver) {
                resolver.markDependency(model);
                resolver.markDependency(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "block/oak_full_drawers_2"));
            }
        }
    }
}
