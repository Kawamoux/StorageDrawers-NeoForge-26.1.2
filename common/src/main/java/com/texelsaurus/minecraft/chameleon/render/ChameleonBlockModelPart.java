package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.state.BlockState;

public interface ChameleonBlockModelPart extends BlockStateModelPart
{
    default void setRenderType (ChunkSectionLayer layer) { }

    ChunkSectionLayer getRenderType();

    default ChunkSectionLayer getRenderType (BlockState state) {
        return getRenderType();
    }
}
