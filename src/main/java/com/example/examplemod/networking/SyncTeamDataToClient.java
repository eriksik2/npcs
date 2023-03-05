package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.npc.ClientNpcData;
import com.example.examplemod.npc.ClientNpcTeam;
import com.example.examplemod.npc.NpcTeam;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncTeamDataToClient implements Message {
    public static final String MESSAGE = "message.SyncTeamDataToClient";
    
    public NpcTeam data;

    public SyncTeamDataToClient(NpcTeam data) {
        this.data = data;
    }

    public SyncTeamDataToClient(FriendlyByteBuf buf) {
        data = new NpcTeam(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        data.toBytes(buf);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            ClientNpcTeam.set(data);
        });
        return true;
    }
}
