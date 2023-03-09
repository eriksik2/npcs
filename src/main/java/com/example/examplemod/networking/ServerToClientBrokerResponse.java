package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.setup.Registration;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

enum ResponseType {
    DATA, IS_UP_TO_DATE
}

public class ServerToClientBrokerResponse implements Message {
    public final static String MESSAGE = "message.ClientDataManagerMessageToClient";

    public ResponseType type;
    public Object data;
    public Integer slot;
    public ResourceLocation id;

    public ServerToClientBrokerResponse(Integer slot, ResourceLocation id) {
        this.type = ResponseType.IS_UP_TO_DATE;
        this.slot = slot;
        this.id = id;
    }

    public ServerToClientBrokerResponse(Object data, Integer slot, ResourceLocation id) {
        this.type = ResponseType.DATA;
        this.data = data;
        this.slot = slot;
        this.id = id;
    }

    public ServerToClientBrokerResponse(FriendlyByteBuf buf) {
        type = ResponseType.values()[buf.readInt()];
        id = buf.readResourceLocation();

        if(type == ResponseType.DATA) {
            ServerToClientBroker<?> manager = Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().get().get();
            data = manager.fromBytes(buf);
        }

        else data = null;
        slot = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(type.ordinal());
        buf.writeResourceLocation(id);
        
        if(type == ResponseType.DATA) {
            ServerToClientBroker<?> manager = Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().get().get();
            manager.unsafeWrite(data, buf);
        }

        buf.writeInt(slot);
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            ServerToClientBroker<?> manager = Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().get().get();
            if(type == ResponseType.IS_UP_TO_DATE) {
                manager.resolveCallbacks(slot);
                return;
            } else {
                manager.unsafeSet(slot, data);
            }
        });
        return true;
    }
}