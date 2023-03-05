package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.npc.ClientNpcData;
import com.example.examplemod.npc.NpcData;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncNpcDataToClient implements Message {
    public static final String MESSAGE = "message.SyncNpcDataToClient";
    
    public NpcData data;

    public SyncNpcDataToClient(NpcData data) {
        this.data = data;
    }

    public SyncNpcDataToClient(FriendlyByteBuf buf) {
        data = new NpcData(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        data.toBytes(buf);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            LogUtils.getLogger().info("GetNpcData: " + data);
            ClientNpcData.set(data);
        });
        return true;
    }
}
