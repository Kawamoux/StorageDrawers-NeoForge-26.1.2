package com.jaquadro.minecraft.storagedrawers.client;

import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class NeoForgeClient
{
    public static <P extends ChameleonPacket> void sendToServer(P packet) {
        ClientPacketDistributor.sendToServer(packet);
    }
}
