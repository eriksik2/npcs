package com.example.examplemod.networking;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.example.examplemod.ExampleMod;
import com.mojang.realmsclient.client.Request.Get;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Messages {

    private static SimpleChannel INSTANCE;

    // Every packet needs a unique ID (unique for this channel)
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        // Make the channel. If needed you can do version checking here
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ExampleMod.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Register all our packets. We only have one right now. The new message has a unique ID, an indication
        // of how it is going to be used (from client to server) and ways to encode and decode it. Finally 'handle'
        // will actually execute when the packet is received
        net.messageBuilder(AddNpcToPlayerTeam.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AddNpcToPlayerTeam::new)
                .encoder(AddNpcToPlayerTeam::toBytes)
                .consumerMainThread(AddNpcToPlayerTeam::handle)
                .add();

        net.messageBuilder(ToggleTrackingEntity.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleTrackingEntity::new)
                .encoder(ToggleTrackingEntity::toBytes)
                .consumerMainThread(ToggleTrackingEntity::handle)
                .add();

        net.messageBuilder(OpenEncyclopedia.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenEncyclopedia::new)
                .encoder(OpenEncyclopedia::toBytes)
                .consumerMainThread(OpenEncyclopedia::handle)
                .add();

        net.messageBuilder(GetNpcData.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(GetNpcData::new)
                .encoder(GetNpcData::toBytes)
                .consumerMainThread(GetNpcData::handle)
                .add();

        net.messageBuilder(SyncTrackingToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncTrackingToClient::new)
                .encoder(SyncTrackingToClient::toBytes)
                .consumerMainThread(SyncTrackingToClient::handle)
                .add();

        net.messageBuilder(SyncNpcDataToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncNpcDataToClient::new)
                .encoder(SyncNpcDataToClient::toBytes)
                .consumerMainThread(SyncNpcDataToClient::handle)
                .add();
    }

    private static <MSG extends Message> void registerMessage(Class<MSG> cls, NetworkDirection dir) {
        INSTANCE.messageBuilder(cls, id(), dir)
                .decoder((buf) -> {
                    try {
                        return cls.getConstructor(FriendlyByteBuf.class).newInstance(buf);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .encoder(MSG::toBytes)
                .consumerMainThread(MSG::handle)
                .add();
    }

    public static <MSG extends Message> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG extends Message> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}