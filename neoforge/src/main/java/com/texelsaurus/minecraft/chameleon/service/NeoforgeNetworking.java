package com.texelsaurus.minecraft.chameleon.service;

import com.jaquadro.minecraft.storagedrawers.client.NeoForgeClient;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.network.ChameleonPacket;
import com.texelsaurus.minecraft.chameleon.registry.NeoforgeRegistryContext;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Consumer;

public class NeoforgeNetworking implements ChameleonNetworking
{
    private static PayloadRegistrar registrar = null;

    public static void init (String modId, ChameleonInit init, NeoforgeRegistryContext context) {
        context.getEventBus().addListener((Consumer<RegisterPayloadHandlersEvent>) event -> {
            registrar = event.registrar(modId);
            init.init(context);
            registrar = null;
        });
    }

    @Override
    public <B extends FriendlyByteBuf, P extends ChameleonPacket> void registerPacketInternal (CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound) {
        IPayloadHandler<P> handler = (packet, context) -> {
            packet.handleMessage(context.player(), context::enqueueWork);
        };

        if (clientBound)
            registrar.playToClient(payloadType, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, handler);
        else
            registrar.playToServer(payloadType, (StreamCodec<RegistryFriendlyByteBuf, P>) codec, handler);
    }

    @Override
    public void sendToPlayer (ChameleonPacket packet, ServerPlayer player) {
        if (!canSendToPlayer(packet, player))
            return;

        PacketDistributor.sendToPlayer(player, packet);
    }

    @Override
    public void sendToPlayersNear(ChameleonPacket packet, ServerLevel level, double x, double y, double z, double radius) {
        double radiusSq = radius * radius;

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.level() != level)
                continue;

            double dx = x - player.getX();
            double dy = y - player.getY();
            double dz = z - player.getZ();
            if (dx * dx + dy * dy + dz * dz > radiusSq)
                continue;

            sendToPlayer(packet, player);
        }
    }

    private boolean canSendToPlayer(ChameleonPacket packet, ServerPlayer player) {
        return NetworkRegistry.hasChannel(player.connection.getConnection(), ConnectionProtocol.PLAY, packet.type().id());
    }

    @Override
    public void sendToServer (ChameleonPacket packet) {
        NeoForgeClient.sendToServer(packet);
    }
}
