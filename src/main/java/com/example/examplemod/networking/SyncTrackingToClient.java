package com.example.examplemod.networking;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.antlr.v4.codegen.model.Sync;

import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.tracking.ClientTrackedObjects;
import com.example.examplemod.tracking.PlayerTrackedObjects;
import com.example.examplemod.tracking.NpcTrackingData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class SyncTrackingToClient implements Message {
    public static final String MESSAGE = "message.PacketSyncTrackingToClient";
    
    public ArrayList<NpcTrackingData> npcs = new ArrayList<NpcTrackingData>();

    public SyncTrackingToClient(PlayerTrackedObjects objects, Player player) {
        Level level = player.level;
        NpcManager npcManager = NpcManager.get(level);
        for(Integer npcId : objects.getTrackedNpcs()) {
            Vec3 pos = npcManager.getNpcPos(npcId);
            npcs.add(new NpcTrackingData(npcId, pos.x, pos.y, pos.z));
        }
    }

    public SyncTrackingToClient(FriendlyByteBuf buf) {
        npcs = new ArrayList<NpcTrackingData>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            NpcTrackingData data = new NpcTrackingData(0, 0, 0, 0);
            data.fromBytes(buf);
            npcs.add(data);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(npcs.size());
        for (NpcTrackingData data : npcs) {
            data.toBytes(buf);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            // Be very careful not to access client-only classes here! (like Minecraft) because
            // this packet needs to be available server-side too
            ClientTrackedObjects.set(this);
        });
        return true;
    }
}
