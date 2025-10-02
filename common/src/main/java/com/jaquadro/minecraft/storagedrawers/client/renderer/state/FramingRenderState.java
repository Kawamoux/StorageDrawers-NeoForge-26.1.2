package com.jaquadro.minecraft.storagedrawers.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class FramingRenderState extends BlockEntityRenderState
{
    public ItemStackRenderState mainSlotItem;
    public ItemStackRenderState sideSlotItem;
    public ItemStackRenderState frontSlotItem;
    public ItemStackRenderState trimSlotItem;

    public FramingRenderState () { }
}
