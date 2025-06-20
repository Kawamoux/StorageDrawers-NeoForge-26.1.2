package com.texelsaurus.minecraft.chameleon.registry;

import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ForgeRegistryContext extends ChameleonInit.InitContext
{
    private final BusGroup eventBusGroup;


    public ForgeRegistryContext (BusGroup busGroup) {
        this.eventBusGroup = busGroup;
    }

    public BusGroup getBusGroup () {
        return eventBusGroup;
    }
}
