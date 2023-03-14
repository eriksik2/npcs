package com.example.examplemod.networking.subscribe;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.networking.Message;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class SubscriptionMessages {
    
    private static SimpleChannel INSTANCE;

    // Every packet needs a unique ID (unique for this channel)
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        // Make the channel. If needed you can do version checking here
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ExampleMod.MODID, "subscription_messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;
        
        net.messageBuilder(SubscribeRequest.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SubscribeRequest::new)
                .encoder(SubscribeRequest::toBytes)
                .consumerMainThread(SubscribeRequest::handle)
                .add();

        net.messageBuilder(UnsubscribeRequest.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UnsubscribeRequest::new)
                .encoder(UnsubscribeRequest::toBytes)
                .consumerMainThread(UnsubscribeRequest::handle)
                .add();
        

        net.messageBuilder(SubscriptionPayload.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SubscriptionPayload::new)
                .encoder(SubscriptionPayload::toBytes)
                .consumerMainThread(SubscriptionPayload::handle)
                .add();
    }

    public static <MSG extends Message> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG extends Message> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
