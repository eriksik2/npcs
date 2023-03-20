package com.example.examplemod.npc.team;

import java.util.List;

import com.example.examplemod.networking.subscribe.SubscriptionBroker;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class PlayerTeamsSubscriptionBroker extends SubscriptionBroker<List<Integer>> {
    public static final PlayerTeamsSubscriptionBroker INSTANCE = new PlayerTeamsSubscriptionBroker();

    public PlayerTeamsSubscriptionBroker() {
        super(Registration.PLAYER_TEAMS_SUBSCRIPTION_BROKER.getId());
    }

    @Override
    public List<Integer> getData(ServerPlayer player, Integer dataId) {
        NpcManager manager = NpcManager.get(player.level);
        return manager.getPlayerTeamIds(player);
    }

    @Override
    public void toBytes(List<Integer> data, FriendlyByteBuf buf) {
        buf.writeInt(data.size());
        for(Integer teamId : data) {
            buf.writeInt(teamId);
        }
    }

    @Override
    public List<Integer> fromBytes(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<Integer> data = new java.util.ArrayList<Integer>(size);
        for(int i = 0; i < size; i++) {
            data.add(buf.readInt());
        }
        return data;
    }
}
