package com.example.examplemod.npc;

import com.example.examplemod.npc.NpcData.Gender;

import net.minecraft.network.FriendlyByteBuf;

public class NpcRenderData {
    public Gender gender;

    public NpcRenderData(Gender gender) {
        this.gender = gender;
    }

    public NpcRenderData(NpcData data) {
        this(data.gender);
    }

    public NpcRenderData(FriendlyByteBuf buf) {
        gender = buf.readEnum(Gender.class);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(gender);
    }

    public NpcRenderData copy() {
        NpcRenderData data = new NpcRenderData(gender);
        return data;
    }
}
