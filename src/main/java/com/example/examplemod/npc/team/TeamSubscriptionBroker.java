package com.example.examplemod.npc.team;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.networking.subscribe.SubscriptionBroker;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TeamSubscriptionBroker extends SubscriptionBroker<NpcTeam> {

    public static final TeamSubscriptionBroker INSTANCE = new TeamSubscriptionBroker();

    public TeamSubscriptionBroker() {
        super(Registration.TEAM_SUBSCRIPTION_BROKER.getId());
    }

    @Override
    public NpcTeam getData(ServerPlayer player, Integer dataId) {
        NpcManager manager = NpcManager.get(player.level);
        return manager.getPlayerTeam(player);
    }

    @Override
    public void toBytes(NpcTeam data, FriendlyByteBuf buf) {
        data.toBytes(buf);
    }

    @Override
    public NpcTeam fromBytes(FriendlyByteBuf buf) {
        return new NpcTeam(buf, null);
    }
    
}
