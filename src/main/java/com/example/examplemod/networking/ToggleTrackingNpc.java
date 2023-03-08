package com.example.examplemod.networking;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.example.examplemod.tracking.PlayerTrackedObjects;
import com.example.examplemod.tracking.PlayerTrackingProvider;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class ToggleTrackingNpc implements Message {
    public static final String MESSAGE = "message.ToggleTrackingEntity";
    
    private final int npcId;

    public ToggleTrackingNpc(int npcId) {
        this.npcId = npcId;
    }

    public ToggleTrackingNpc(FriendlyByteBuf buf) {
        npcId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(npcId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            ServerPlayer player = ctx.getSender();
            PlayerTrackedObjects trackedObjects = player.getCapability(PlayerTrackingProvider.TRACKED_OBJECTS)
                    .orElse(null);
            if(!trackedObjects.isTrackingNpc(npcId)) {
                trackedObjects.trackNpc(npcId);
            } else {
                trackedObjects.untrackNpc(npcId);
            }
        });
        return true;
    }
}
