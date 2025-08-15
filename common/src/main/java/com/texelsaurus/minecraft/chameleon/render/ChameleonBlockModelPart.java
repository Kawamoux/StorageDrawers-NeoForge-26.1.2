package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public interface ChameleonBlockModelPart extends BlockModelPart
{
    default void setRenderType (ChunkSectionLayer layer) { }

    ChunkSectionLayer getRenderType();
}
