package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ForgeReplacementBlockPart extends ReplacementBlockPart implements BlockModelPart
{
    @Nullable
    private ChunkSectionLayer renderType;

    public ForgeReplacementBlockPart (BlockModelPart part, TextureAtlasSprite sprite) {
        super(part, sprite);

        if (part instanceof SimpleModelWrapper wrapper)
            renderType = wrapper.layer();
    }

    @Override
    public void setRenderType (@Nullable ChunkSectionLayer renderType) {
        this.renderType = renderType;
    }

    @Override
    @Nullable
    public ChunkSectionLayer getRenderType () {
        return renderType;
    }

    @Override
    public ChunkSectionLayer layer () {
        return renderType != null ? renderType : super.layer();
    }

    @Override
    public ChunkSectionLayer layerFast () {
        return renderType != null ? renderType : super.layerFast();
    }
}
