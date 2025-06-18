package com.jaquadro.minecraft.storagedrawers.block.tile.modelprops;

import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedBlockEntity;
import com.jaquadro.minecraft.storagedrawers.api.framing.IFramedMaterials;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.MaterialData;
import com.jaquadro.minecraft.storagedrawers.client.model.ModelContextSupplier;
import com.jaquadro.minecraft.storagedrawers.client.model.context.DrawerModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.context.FramedModelContext;
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

public class ForgeFramedModelProperties extends FramedModelProperties
{
    public static final ForgeFramedModelProperties INSTANCE = new ForgeFramedModelProperties();

    public static final ModelProperty<BlockState> BLOCKSTATE = new ModelProperty<>();
    public static final ModelProperty<IFramedMaterials> MATERIAL = new ModelProperty<>();

    public final ModelData modelData;

    protected ForgeFramedModelProperties () {
        super();

        modelData = ModelData.builder().build();
    }

    protected ForgeFramedModelProperties (IFramedBlockEntity blockEntity) {
        super(blockEntity);

        modelData = ModelData.builder()
            .with(MATERIAL, blockEntity.material()).build();
    }

    public static ForgeFramedModelProperties getForgeModelData (IFramedBlockEntity blockEntity) {
        return new ForgeFramedModelProperties(blockEntity);
    }

    @Override
    public FramedModelContext makeContext (@Nullable BlockState state, RandomSource rand, Object renderData) {
        if (renderData instanceof ModelData m) {
            return new FramedModelContext(state, rand)
                .materialData(new MaterialData(m.get(MATERIAL)));
        }

        return super.makeContext(state, rand, renderData);
    }

    @Override
    public FramedModelContext makeContext (ItemStack stack) {
        MaterialData data = stack.getOrDefault(ModDataComponents.FRAME_DATA.get(), FrameData.EMPTY).asMaterialData();

        Block block = Blocks.AIR;
        if (stack.getItem() instanceof BlockItem blockItem)
            block = blockItem.getBlock();

        return new FramedModelContext(block.defaultBlockState())
            .materialData(data);
    }
}