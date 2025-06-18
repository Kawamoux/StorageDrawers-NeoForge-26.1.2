package com.jaquadro.minecraft.storagedrawers.block.tile.modelprops;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.model.ModelContextSupplier;
import com.jaquadro.minecraft.storagedrawers.client.model.context.DrawerModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.context.ModelContext;
import com.jaquadro.minecraft.storagedrawers.components.item.FrameData;
import com.jaquadro.minecraft.storagedrawers.core.ModDataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public class ForgeDrawerModelProperties extends DrawerModelProperties
{
    public static final ForgeDrawerModelProperties INSTANCE = new ForgeDrawerModelProperties();

    public static final ModelProperty<BlockState> BLOCKSTATE = new ModelProperty<>();
    public static final ModelProperty<IDrawerAttributes> ATTRIBUTES = new ModelProperty<>();
    public static final ModelProperty<IDrawerGroup> DRAWER_GROUP = new ModelProperty<>();
    public static final ModelProperty<IProtectable> PROTECTABLE = new ModelProperty<>();
    public static final ModelProperty<MaterialData> MATERIAL = new ModelProperty<>();

    public final ModelData modelData;

    protected  ForgeDrawerModelProperties () {
        super();

        modelData = ModelData.builder().build();
    }

    protected ForgeDrawerModelProperties (BlockEntityDrawers blockEntity) {
        super(blockEntity);

        modelData = ModelData.builder()
            .with(ATTRIBUTES, attributes)
            .with(DRAWER_GROUP, group)
            .with(PROTECTABLE, protectable)
            .with(MATERIAL, material).build();
    }

    public static ForgeDrawerModelProperties getForgeModelData (BlockEntityDrawers blockEntity) {
        return new ForgeDrawerModelProperties(blockEntity);
    }

    @Override
    public DrawerModelContext makeContext (@Nullable BlockState state, RandomSource rand, Object renderData) {
        if (renderData instanceof ModelData m) {
            return new DrawerModelContext(state, rand)
                .attr(m.get(ATTRIBUTES))
                .group(m.get(DRAWER_GROUP))
                .protectable(m.get(PROTECTABLE))
                .materialData(m.get(MATERIAL));
        }

        return super.makeContext(state, rand, renderData);
    }

    @Override
    public DrawerModelContext makeContext (ItemStack stack) {
        MaterialData data = stack.getOrDefault(ModDataComponents.FRAME_DATA.get(), FrameData.EMPTY).asMaterialData();

        Block block = Blocks.AIR;
        if (stack.getItem() instanceof BlockItem blockItem)
            block = blockItem.getBlock();

        return new DrawerModelContext(block.defaultBlockState())
            .materialData(data);
    }
}