package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.render.ChameleonBlockModelPart;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface ChameleonRender
{
    ChameleonBlockModelPart createReplacementPart (BlockModelPart part, TextureAtlasSprite sprite);
}
