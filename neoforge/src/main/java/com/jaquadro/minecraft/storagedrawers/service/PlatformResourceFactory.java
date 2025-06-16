package com.jaquadro.minecraft.storagedrawers.service;

import com.jaquadro.minecraft.storagedrawers.block.tile.*;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PlatformResourceFactory implements ResourceFactory
{
    @Override
    public BlockEntityType.BlockEntitySupplier<BlockEntityDrawersStandard> createBlockEntityDrawersStandard (int slotCount) {
        return switch (slotCount) {
            case 1 -> BlockEntityDrawersStandard.Slot1::new;
            case 2 -> BlockEntityDrawersStandard.Slot2::new;
            case 4 -> BlockEntityDrawersStandard.Slot4::new;
            default -> null;
        };
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<BlockEntityDrawersComp> createBlockEntityDrawersComp (int slotCount) {
        return switch (slotCount) {
            case 2 -> BlockEntityDrawersComp.Slot2::new;
            case 3 -> BlockEntityDrawersComp.Slot3::new;
            default -> null;
        };
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<BlockEntityController> createBlockEntityController () {
        return BlockEntityController::new;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<BlockEntityControllerIO> createBlockEntityControllerIO () {
        return BlockEntityControllerIO::new;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<BlockEntityFramingTable> createBlockEntityFramingTable () {
        return BlockEntityFramingTable::new;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<BlockEntityTrim> createBlockEntityTrim () {
        return BlockEntityTrim::new;
    }
}
