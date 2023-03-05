package com.example.examplemod.networking;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.antlr.v4.codegen.model.Sync;

import com.example.examplemod.tracking.ClientTrackedObjects;
import com.example.examplemod.tracking.PlayerTrackedObjects;
import com.example.examplemod.tracking.TrackedEntityData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class SyncTrackingToClient implements Message {
    public static final String MESSAGE = "message.PacketSyncTrackingToClient";
    
    public ArrayList<TrackedEntityData> entities = new ArrayList<TrackedEntityData>();

    public SyncTrackingToClient(PlayerTrackedObjects objects, Player player) {
        Level level = player.level;
        for(Integer entityId : objects.getTrackedEntities()) {
            Entity entity = level.getEntity(entityId);
            if (entity != null) {
                entities.add(new TrackedEntityData(entityId, entity.getX(), entity.getY(), entity.getZ()));
            }
        }
    }

    public SyncTrackingToClient(FriendlyByteBuf buf) {
        entities = new ArrayList<TrackedEntityData>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            TrackedEntityData data = new TrackedEntityData(0, 0, 0, 0);
            data.fromBytes(buf);
            entities.add(data);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entities.size());
        for (TrackedEntityData data : entities) {
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
