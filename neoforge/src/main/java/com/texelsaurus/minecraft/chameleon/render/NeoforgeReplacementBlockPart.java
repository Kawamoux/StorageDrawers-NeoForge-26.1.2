package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.BlockModelPartExtension;

import javax.annotation.Nullable;

public class NeoforgeReplacementBlockPart extends ReplacementBlockPart implements BlockModelPart
{
    @Nullable
    private ChunkSectionLayer renderType;

    public NeoforgeReplacementBlockPart (BlockModelPart part, TextureAtlasSprite sprite) {
        super(part, sprite);

        if (part instanceof SimpleModelWrapper wrapper)
            renderType = wrapper.renderType();
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
        return renderType != null ? renderType : super.getRenderType(state);
    }
}
