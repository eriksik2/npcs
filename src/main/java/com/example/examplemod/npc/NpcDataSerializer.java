package com.example.examplemod.npc;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class NpcDataSerializer implements EntityDataSerializer<NpcData> {

    @Override
    public void write(FriendlyByteBuf to, NpcData data) {
        data.toBytes(to);
    }

    @Override
    public NpcData read(FriendlyByteBuf from) {
        return new NpcData(from);
    }

    @Override
    public NpcData copy(NpcData from) {
        return from.copy();
    }

}