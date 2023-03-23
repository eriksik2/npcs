package com.example.examplemod.npc;

import com.example.examplemod.networking.subscribe.SubscriptionBroker;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class NpcDataSubscriptionBroker extends SubscriptionBroker<NpcData> {

    public static final NpcDataSubscriptionBroker INSTANCE = new NpcDataSubscriptionBroker();

    public NpcDataSubscriptionBroker() {
        super(Registration.NPC_DATA_SUBSCRIPTION_BROKER.getId());
    }

    @Override
    public NpcData getData(ServerPlayer player, Integer dataId) {
        NpcManager manager = NpcManager.get(player.level);
        return manager.getNpcData(dataId);
    }

    @Override
    public void toBytes(NpcData data, FriendlyByteBuf buf) {
        data.toBytes(buf);
    }

    @Override
    public NpcData fromBytes(FriendlyByteBuf buf) {
        return new NpcData(buf, null);
    }
    
}
