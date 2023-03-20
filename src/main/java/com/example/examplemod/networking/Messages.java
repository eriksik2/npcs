package com.example.examplemod.networking;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.npc.area.AddNpcAreaMsg;
import com.example.examplemod.npc.area.SyncEditingAreaToClient;
import com.example.examplemod.npc.area.ToggleRoleHasAreaMsg;
import com.example.examplemod.npc.area.UpdateAreaPositionMsg;
import com.example.examplemod.npc.role.RemoveTeamRoleMsg;
import com.example.examplemod.npc.role.ToggleNpcHasRoleMsg;
import com.mojang.realmsclient.client.Request.Get;

import ca.weblite.objc.Client;
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

        net.messageBuilder(ServerToClientBrokerRequest.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerToClientBrokerRequest::new)
                .encoder(ServerToClientBrokerRequest::toBytes)
                .consumerMainThread(ServerToClientBrokerRequest::handle)
                .add();

        net.messageBuilder(ServerToClientBrokerResponse.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ServerToClientBrokerResponse::new)
                .encoder(ServerToClientBrokerResponse::toBytes)
                .consumerMainThread(ServerToClientBrokerResponse::handle)
                .add();

        // Register all our packets. We only have one right now. The new message has a unique ID, an indication
        // of how it is going to be used (from client to server) and ways to encode and decode it. Finally 'handle'
        // will actually execute when the packet is received
        net.messageBuilder(AddNpcToPlayerTeam.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AddNpcToPlayerTeam::new)
                .encoder(AddNpcToPlayerTeam::toBytes)
                .consumerMainThread(AddNpcToPlayerTeam::handle)
                .add();

        net.messageBuilder(SetNpcTeamData.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetNpcTeamData::new)
                .encoder(SetNpcTeamData::toBytes)
                .consumerMainThread(SetNpcTeamData::handle)
                .add();

        net.messageBuilder(AddRoleToTeam.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AddRoleToTeam::new)
                .encoder(AddRoleToTeam::toBytes)
                .consumerMainThread(AddRoleToTeam::handle)
                .add();

        net.messageBuilder(ToggleTrackingNpc.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleTrackingNpc::new)
                .encoder(ToggleTrackingNpc::toBytes)
                .consumerMainThread(ToggleTrackingNpc::handle)
                .add();

        net.messageBuilder(OpenEncyclopedia.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenEncyclopedia::new)
                .encoder(OpenEncyclopedia::toBytes)
                .consumerMainThread(OpenEncyclopedia::handle)
                .add();

        net.messageBuilder(OpenEditTeam.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenEditTeam::new)
                .encoder(OpenEditTeam::toBytes)
                .consumerMainThread(OpenEditTeam::handle)
                .add();

        net.messageBuilder(RemoveTeamRoleMsg.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RemoveTeamRoleMsg::new)
                .encoder(RemoveTeamRoleMsg::toBytes)
                .consumerMainThread(RemoveTeamRoleMsg::handle)
                .add();

        net.messageBuilder(ToggleNpcHasRoleMsg.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleNpcHasRoleMsg::new)
                .encoder(ToggleNpcHasRoleMsg::toBytes)
                .consumerMainThread(ToggleNpcHasRoleMsg::handle)
                .add();

        net.messageBuilder(AddNpcAreaMsg.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AddNpcAreaMsg::new)
                .encoder(AddNpcAreaMsg::toBytes)
                .consumerMainThread(AddNpcAreaMsg::handle)
                .add();

        net.messageBuilder(UpdateAreaPositionMsg.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateAreaPositionMsg::new)
                .encoder(UpdateAreaPositionMsg::toBytes)
                .consumerMainThread(UpdateAreaPositionMsg::handle)
                .add();

        net.messageBuilder(ToggleRoleHasAreaMsg.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleRoleHasAreaMsg::new)
                .encoder(ToggleRoleHasAreaMsg::toBytes)
                .consumerMainThread(ToggleRoleHasAreaMsg::handle)
                .add();
        

        net.messageBuilder(SyncTrackingToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncTrackingToClient::new)
                .encoder(SyncTrackingToClient::toBytes)
                .consumerMainThread(SyncTrackingToClient::handle)
                .add();

        net.messageBuilder(SyncEditingAreaToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncEditingAreaToClient::new)
                .encoder(SyncEditingAreaToClient::toBytes)
                .consumerMainThread(SyncEditingAreaToClient::handle)
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