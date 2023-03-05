package com.example.examplemod.npc;

import net.minecraft.nbt.CompoundTag;

public class PassiveNpcData {
    double x;
    double y;
    double z;
    NpcData data;

    public PassiveNpcData(NpcEntity entity) {
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
        data = entity.npcData;
    }

    public PassiveNpcData(CompoundTag tag) {
        x = tag.getDouble("x");
        y = tag.getDouble("y");
        z = tag.getDouble("z");
        data = new NpcData(tag.getCompound("data"));
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
        tag.put("data", data.toCompoundTag());
        return tag;
    }
}
