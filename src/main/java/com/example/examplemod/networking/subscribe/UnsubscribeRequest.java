package com.example.examplemod.networking.subscribe;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class UnsubscribeRequest implements Message {

    public Integer dataId;
    public ResourceLocation subscriptionBroker;

    public UnsubscribeRequest(Integer dataId, ResourceLocation subscriptionBroker) {
        this.dataId = dataId;
        this.subscriptionBroker = subscriptionBroker;
    }

    public UnsubscribeRequest(FriendlyByteBuf buf) {
        dataId = buf.readInt();
        subscriptionBroker = buf.readResourceLocation();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(dataId);
        buf.writeResourceLocation(subscriptionBroker);
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            Registration.SUBSCRIPTION_BROKERS.getEntries().stream().filter(e -> e.getId().equals(subscriptionBroker)).findFirst().ifPresent(e -> {
                SubscriptionBroker<?> broker = e.get();
                Player player = ctx.getSender();
                broker.serverUnregisterSubscriber(player.getUUID(), dataId);
            });
        });
        return true;
    }
    
}
