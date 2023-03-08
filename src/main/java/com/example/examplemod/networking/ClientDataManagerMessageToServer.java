package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.setup.Registration;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientDataManagerMessageToServer implements Message {

    public final static String MESSAGE = "message.ClientDataManagerMessageToServer";

    public ResourceLocation id;
    public Integer slot;

    public ClientDataManagerMessageToServer(Integer slot, ResourceLocation id) {
        this.slot = slot;
        this.id = id;
    }

    public ClientDataManagerMessageToServer(FriendlyByteBuf buf) {
        slot = buf.readInt();
        id = buf.readResourceLocation();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeResourceLocation(id);
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            Registration.MESSAGE_BROKERS.getEntries().stream().filter(e -> e.getId().equals(id)).findFirst().ifPresent(e -> {
                ClientDataManager<?> manager = e.get();
                Object data = manager.handle(ctx, slot);
                if(data == null) {
                    return;
                }
                ClientDataManagerMessageToClient message = new ClientDataManagerMessageToClient(data, slot, id);
                Messages.sendToPlayer(message, ctx.getSender());
            });
        });
        return true;
    }
}
