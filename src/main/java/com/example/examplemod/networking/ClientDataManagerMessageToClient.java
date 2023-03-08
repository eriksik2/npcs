package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.setup.Registration;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientDataManagerMessageToClient implements Message {
    public final static String MESSAGE = "message.ClientDataManagerMessageToClient";

    public Object data;
    public Integer slot;
    public ResourceLocation id;

    public ClientDataManagerMessageToClient(Object data, Integer slot, ResourceLocation id) {
        this.data = data;
        this.slot = slot;
        this.id = id;
    }

    public ClientDataManagerMessageToClient(FriendlyByteBuf buf) {
        id = buf.readResourceLocation();
        ClientDataManager<?> manager = Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().get().get();
        data = manager.fromBytes(buf);
        slot = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        ClientDataManager<?> manager = Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().get().get();
        manager.unsafeWrite(data, buf);
        buf.writeInt(slot);
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            ClientDataManager<?> manager = Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().get().get();
            manager.unsafeSet(slot, data);
        });
        return true;
    }
}