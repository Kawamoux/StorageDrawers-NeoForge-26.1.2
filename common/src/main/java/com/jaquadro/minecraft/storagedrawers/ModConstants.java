package com.jaquadro.minecraft.storagedrawers;

import com.texelsaurus.minecraft.chameleon.resources.ResourceLocation;

public final class ModConstants
{
    public static final String MOD_ID = "storagedrawers";

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
