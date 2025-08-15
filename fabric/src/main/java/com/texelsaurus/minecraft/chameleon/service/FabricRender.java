package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.render.ChameleonBlockModelPart;
import com.texelsaurus.minecraft.chameleon.render.FabricReplacementBlockPart;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class FabricRender implements ChameleonRender
{
    @Override
    public ChameleonBlockModelPart createReplacementPart (BlockModelPart part, TextureAtlasSprite sprite) {
        return new FabricReplacementBlockPart(part, sprite);
    }
}
