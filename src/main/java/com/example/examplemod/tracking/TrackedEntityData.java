package com.example.examplemod.tracking;

import net.minecraft.network.FriendlyByteBuf;

public class TrackedEntityData {
    public int entityId;
    public double x, y, z;

    public TrackedEntityData(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        entityId = buf.readInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }
}