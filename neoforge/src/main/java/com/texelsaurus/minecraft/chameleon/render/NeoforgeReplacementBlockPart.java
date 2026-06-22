package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class NeoforgeReplacementBlockPart extends ReplacementBlockPart implements BlockStateModelPart
{
    @Nullable
    private ChunkSectionLayer renderType;

    public NeoforgeReplacementBlockPart (BlockStateModelPart part, TextureAtlasSprite sprite) {
        super(part, sprite);
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
    public ChunkSectionLayer getRenderType (BlockState state) {
        return renderType != null ? renderType : ChunkSectionLayer.SOLID;
    }
}
