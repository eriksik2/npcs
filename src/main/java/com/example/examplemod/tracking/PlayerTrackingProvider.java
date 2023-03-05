package com.example.examplemod.tracking;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerTrackingProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerTrackedObjects> TRACKED_OBJECTS = CapabilityManager.get(new CapabilityToken<>(){});

    private PlayerTrackedObjects trackedObjects = null;

    private final LazyOptional<PlayerTrackedObjects> opt = LazyOptional.of(this::getTrackedObjects);

    public PlayerTrackingProvider() {
    }

    @Nonnull
    private PlayerTrackedObjects getTrackedObjects() {
        if (trackedObjects == null) {
            trackedObjects = new PlayerTrackedObjects();
        }
        return trackedObjects;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("trackedObjects", getTrackedObjects().serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        getTrackedObjects().deserializeNBT(tag.getCompound("trackedObjects"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == TRACKED_OBJECTS) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }
    
}
