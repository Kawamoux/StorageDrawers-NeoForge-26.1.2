package com.jaquadro.minecraft.storagedrawers.block.meta;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockMetaTrans extends BlockMeta
{
    public static final BooleanProperty TRANS = BooleanProperty.create("trans");

    public BlockMetaTrans (Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRANS);
    }
}
