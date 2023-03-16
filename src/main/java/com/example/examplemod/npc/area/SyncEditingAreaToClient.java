package com.example.examplemod.npc.area;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncEditingAreaToClient implements Message {

    private NpcArea area;

    public SyncEditingAreaToClient(NpcArea area) {
        this.area = area;
    }

    public SyncEditingAreaToClient(FriendlyByteBuf buf) {
        area = new NpcArea(buf.readNbt(), null);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(area.toCompoundTag());
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            ClientEditingArea.area = area;
        });
        return true;
    }
    
}
