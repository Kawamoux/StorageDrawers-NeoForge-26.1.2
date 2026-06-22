package com.texelsaurus.minecraft.chameleon.render;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ReplacementBlockPart implements ChameleonBlockModelPart
{
    protected BlockStateModelPart parent;
    private TextureAtlasSprite sprite;
    private List<BakedQuad> quads = new ArrayList<>();

    public ReplacementBlockPart(BlockStateModelPart part, TextureAtlasSprite sprite) {
        parent = part;
        this.sprite = sprite;

        part.getQuads(null).forEach(quad -> quads.add(remapQuad(quad, sprite)));
        for (Direction dir : Direction.values()) {
            part.getQuads(dir).forEach(quad -> quads.add(remapQuad(quad, sprite)));
        }
    }

    public ReplacementBlockPart (BlockStateModelPart parent, BlockStateModelPart replacement) {
        this(parent, replacement.particleMaterial().sprite());
    }

    @Override
    public List<BakedQuad> getQuads (@Nullable Direction direction) {
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion () {
        return parent.useAmbientOcclusion();
    }

    public TextureAtlasSprite particleIcon () {
        if (sprite == null)
            return parent.particleMaterial().sprite();

        return sprite;
    }

    @Override
    public Material.Baked particleMaterial () {
        return parent.particleMaterial();
    }

    @Override
    public int materialFlags () {
        return parent.materialFlags();
    }

    BakedQuad remapQuad (BakedQuad quad, TextureAtlasSprite sprite) {
        BakedQuad.MaterialInfo source = quad.materialInfo();
        BakedQuad.MaterialInfo materialInfo = new BakedQuad.MaterialInfo(
            sprite,
            source.layer(),
            source.itemRenderType(),
            source.tintIndex(),
            source.shade(),
            source.lightEmission(),
            source.ambientOcclusion()
        );

        return new BakedQuad(
            quad.position0(),
            quad.position1(),
            quad.position2(),
            quad.position3(),
            quad.packedUV0(),
            quad.packedUV1(),
            quad.packedUV2(),
            quad.packedUV3(),
            quad.direction(),
            materialInfo,
            quad.bakedNormals(),
            quad.bakedColors()
        );
    }
}
