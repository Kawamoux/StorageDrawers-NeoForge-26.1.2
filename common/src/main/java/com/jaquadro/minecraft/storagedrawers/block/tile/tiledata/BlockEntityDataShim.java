package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class BlockEntityDataShim
{
    public abstract void read (ValueInput input);

    public abstract void write (ValueOutput output);

    /*public CompoundTag serializeNBT (HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        return write(provider, tag);
    }

    public void deserializeNBT (HolderLookup.Provider provider, CompoundTag nbt) {
        read(provider, nbt);
    }*/
}
