package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.setup.Registration;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerToClientBrokerRequest implements Message {

    public final static String MESSAGE = "message.ClientDataManagerMessageToServer";

    public Integer slot;
    public Integer dataHash;
    public ResourceLocation id;

    public ServerToClientBrokerRequest(Integer slot, Integer dataHash, ResourceLocation id) {
        this.slot = slot;
        this.dataHash = dataHash;
        this.id = id;
    }

    public ServerToClientBrokerRequest(FriendlyByteBuf buf) {
        slot = buf.readInt();
        boolean hasHash = buf.readBoolean();
        dataHash = hasHash ? buf.readInt() : null;
        id = buf.readResourceLocation();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeBoolean(dataHash != null);
        if(dataHash != null) buf.writeInt(dataHash);
        buf.writeResourceLocation(id);
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().ifPresent(e -> {
                ServerToClientBroker<?> manager = e.get();
                Object data = manager.handle(ctx, slot);
                if(data == null) {
                    return;
                }
                Integer hash = manager.unsafeHashCode(data);
                if(dataHash != null && hash.equals(dataHash)) {
                    // Data is up to date on the client
                    ServerToClientBrokerResponse message = new ServerToClientBrokerResponse(slot, id);
                    Messages.sendToPlayer(message, ctx.getSender());
                } else {
                    // Data is not up to date on the client
                    ServerToClientBrokerResponse message = new ServerToClientBrokerResponse(data, slot, id);
                    Messages.sendToPlayer(message, ctx.getSender());
                }
                
            });
        });
        return true;
    }
}
