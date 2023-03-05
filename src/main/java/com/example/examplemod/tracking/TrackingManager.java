package com.example.examplemod.tracking;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.SyncTrackingToClient;
import com.mojang.logging.LogUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class TrackingManager extends SavedData {

    @Nonnull
    public static TrackingManager get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        // Get the mana manager if it already exists. Otherwise create a new one. Note that both
        // invocations of ManaManager::new actually refer to a different constructor. One without parameters
        // and the other with a CompoundTag parameter
        TrackingManager manager = storage.computeIfAbsent(TrackingManager::new, TrackingManager::new, "trackingmanager");
        return manager;
    }

    private int tickCounter = 0;

    public TrackingManager() {
    }

    public TrackingManager(CompoundTag tag) {
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    public void tick(Level level) {
        tickCounter--;
        if(tickCounter <= 0) {
            tickCounter = 10;

            level.players().forEach(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    PlayerTrackedObjects trackedObjects = serverPlayer.getCapability(PlayerTrackingProvider.TRACKED_OBJECTS)
                            .orElse(null);
                    Messages.sendToPlayer(new SyncTrackingToClient(trackedObjects, player), serverPlayer);
                }
            });
        }
    }
    
}
