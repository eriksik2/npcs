package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

// Class that keeps track of data changes across the network
public class DataVersion {
    private final long id;
    private long version = 0;

    public DataVersion(long id) {
        this.id = id;
    }

    public DataVersion(CompoundTag tag) {
        id = tag.getLong("id");
        version = tag.getLong("version");
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("id", id);
        tag.putLong("version", version);
        return tag;
    }

    public DataVersion(FriendlyByteBuf buf) {
        id = buf.readLong();
        version = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeLong(id);
        buf.writeLong(version);
    }

    public void markDirty() {
        version++;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(!(obj instanceof DataVersion)) return false;
        DataVersion other = (DataVersion)obj;
        return id == other.id && version == other.version;
    }

    @Override
    public int hashCode() {
        return (int)(id ^ version);
    }
}
