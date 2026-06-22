package com.texelsaurus.minecraft.chameleon.render;

public final class RenderType {
    private static final RenderType SOLID = new RenderType();
    private static final RenderType CUTOUT = new RenderType();
    private static final RenderType CUTOUT_MIPPED = new RenderType();
    private static final RenderType TRANSLUCENT = new RenderType();

    private RenderType() {
    }

    public static RenderType solid() {
        return SOLID;
    }

    public static RenderType cutout() {
        return CUTOUT;
    }

    public static RenderType cutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderType translucent() {
        return TRANSLUCENT;
    }
}
