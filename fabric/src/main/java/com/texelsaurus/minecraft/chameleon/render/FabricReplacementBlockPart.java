package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class FabricReplacementBlockPart extends ReplacementBlockPart implements BlockModelPart
{
    private ChunkSectionLayer renderType;

    public FabricReplacementBlockPart (BlockModelPart part, TextureAtlasSprite sprite) {
        super(part, sprite);

        //if (part instanceof SimpleModelWrapper wrapper)
        //    renderType = wrapper.
    }

    @Override
    public void setRenderType (ChunkSectionLayer renderType) {
        this.renderType = renderType;
    }

    @Override
    public ChunkSectionLayer getRenderType () {
        return renderType;
    }
}
