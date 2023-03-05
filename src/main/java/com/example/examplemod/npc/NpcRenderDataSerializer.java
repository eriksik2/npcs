package com.example.examplemod.npc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class NpcRenderDataSerializer implements EntityDataSerializer<NpcRenderData> {

    @Override
    public void write(FriendlyByteBuf to, NpcRenderData data) {
        data.toBytes(to);
    }

    @Override
    public NpcRenderData read(FriendlyByteBuf from) {
        return new NpcRenderData(from);
    }

    @Override
    public NpcRenderData copy(NpcRenderData from) {
        return from.copy();
    }

}