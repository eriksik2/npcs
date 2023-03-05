package com.example.examplemod.tracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerTrackedObjects implements INBTSerializable<CompoundTag> {

    private HashSet<Integer> trackedEntities = new HashSet<Integer>();

    public PlayerTrackedObjects() {
    }

    public void copyFrom(PlayerTrackedObjects source) {
        trackedEntities = (HashSet<Integer>)source.trackedEntities.clone();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag entitiesTag = new ListTag();
        for(Integer entityId : trackedEntities) {
            // TODO entity id changes all the time, use some other identifying feature that persists.
            entitiesTag.add(IntTag.valueOf(entityId));
        }
        tag.put("entities", entitiesTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag entitiesTag = (ListTag)tag.get("entities");
        if(entitiesTag == null) throw new RuntimeException("Failed to deserialize PlayerTrackedObject: No 'entities' entry.");
        trackedEntities = new HashSet<Integer>();
        for(Tag itag : entitiesTag) {
            IntTag intTag = (IntTag)itag;
            trackedEntities.add(intTag.getAsInt());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(serializeNBT());
    }

    public PlayerTrackedObjects(FriendlyByteBuf buf) {
        deserializeNBT(buf.readNbt());
    }

    public boolean isTrackingEntity(Entity entity) {
        return trackedEntities.contains(Integer.valueOf(entity.getId()));
    }

    public void trackEntity(Entity entity) {
        trackedEntities.add(Integer.valueOf(entity.getId()));
    }

    public void untrackEntity(Entity entity) {
        trackedEntities.remove(Integer.valueOf(entity.getId()));
    }

    public List<Integer> getTrackedEntities() {
        return new ArrayList<Integer>(trackedEntities);
    }
    
}
