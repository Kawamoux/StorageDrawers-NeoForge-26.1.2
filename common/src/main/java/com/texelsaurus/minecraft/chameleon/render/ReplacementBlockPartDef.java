package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ReplacementBlockPartDef
{
    private BlockModelPart part;
    private TextureAtlasSprite sprite;

    public ReplacementBlockPartDef (BlockModelPart part, TextureAtlasSprite sprite) {
        this.part = part;
        this.sprite = sprite;
    }

    public BlockModelPart getPart () {
        return part;
    }

    public TextureAtlasSprite getSprite () {
        return sprite;
    }
}
