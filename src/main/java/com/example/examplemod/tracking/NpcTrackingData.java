package com.example.examplemod.tracking;

import net.minecraft.network.FriendlyByteBuf;

public class NpcTrackingData {
    public int npcId;
    public double x, y, z;

    public NpcTrackingData(int npcId, double x, double y, double z) {
        this.npcId = npcId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        npcId = buf.readInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(npcId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }
}