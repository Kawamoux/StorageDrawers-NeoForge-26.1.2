package com.texelsaurus.minecraft.chameleon.capabilities;

import net.minecraft.core.BlockPos;
import com.texelsaurus.minecraft.chameleon.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface ChameleonCapability<T>
{
    ResourceLocation id ();

    T getCapability(Level level, BlockPos pos);
}
