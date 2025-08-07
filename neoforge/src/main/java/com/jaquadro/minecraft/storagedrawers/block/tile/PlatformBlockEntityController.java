package com.jaquadro.minecraft.storagedrawers.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PlatformBlockEntityController extends BlockEntityController
{
    public PlatformBlockEntityController (BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void onLoad () {
        super.onLoad();
        onEntityLoad();
    }
}
