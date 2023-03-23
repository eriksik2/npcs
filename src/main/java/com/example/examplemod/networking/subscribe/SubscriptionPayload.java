package com.example.examplemod.networking.subscribe;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class SubscriptionPayload implements Message {

    public ResourceLocation subscriptionBroker;
    public Integer dataId;
    public Object data;

    public SubscriptionPayload(ResourceLocation subscriptionBroker, Integer dataId, Object data) {
        this.subscriptionBroker = subscriptionBroker;
        this.dataId = dataId;
        this.data = data;
    }

    public SubscriptionPayload(FriendlyByteBuf buf) {
        subscriptionBroker = buf.readResourceLocation();
        dataId = buf.readInt();
        SubscriptionBroker<?> broker = Registration.SUBSCRIPTION_BROKERS.getEntries().stream().filter(e -> e.getId().equals(subscriptionBroker)).findFirst().get().get();
        data = broker.fromBytes(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(subscriptionBroker);
        buf.writeInt(dataId);
        SubscriptionBroker<?> broker = Registration.SUBSCRIPTION_BROKERS.getEntries().stream().filter(e -> e.getId().equals(subscriptionBroker)).findFirst().get().get();
        broker.unsafeToBytes(data, buf);
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            SubscriptionBroker<?> manager = Registration.SUBSCRIPTION_BROKERS.getEntries().stream().filter(e -> e.getId().equals(subscriptionBroker)).findFirst().get().get();
            manager.unsafeClientReceivePayload(dataId, data);
        });
        return true;
    }
    
}
