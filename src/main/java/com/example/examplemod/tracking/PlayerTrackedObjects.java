package com.example.examplemod.tracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.example.examplemod.npc.NpcEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerTrackedObjects implements INBTSerializable<CompoundTag> {

    private HashSet<Integer> trackedNpcs = new HashSet<Integer>();

    public PlayerTrackedObjects() {
    }

    public void copyFrom(PlayerTrackedObjects source) {
        trackedNpcs = (HashSet<Integer>)source.trackedNpcs.clone();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag npcsTag = new ListTag();
        for(Integer npcId : trackedNpcs) {
            npcsTag.add(IntTag.valueOf(npcId));
        }
        tag.put("npcs", npcsTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag npcsTag = (ListTag)tag.get("npcs");
        trackedNpcs = new HashSet<Integer>();
        if(npcsTag != null) {
            for(Tag itag : npcsTag) {
                IntTag intTag = (IntTag)itag;
                trackedNpcs.add(intTag.getAsInt());
            }
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(serializeNBT());
    }

    public PlayerTrackedObjects(FriendlyByteBuf buf) {
        deserializeNBT(buf.readNbt());
    }

    public boolean isTrackingNpc(Integer npcId) {
        return trackedNpcs.contains(npcId);
    }

    public void trackNpc(Integer npcId) {
        trackedNpcs.add(npcId);
    }

    public void untrackNpc(Integer npcId) {
        trackedNpcs.removeIf((id) -> id == npcId);
    }

    public List<Integer> getTrackedNpcs() {
        return new ArrayList<Integer>(trackedNpcs);
    }
    
}
