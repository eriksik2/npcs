package com.example.examplemod.npc;

import com.example.examplemod.generator.NpcGenerator;
import com.example.examplemod.networking.subscribe.DataVersion;
import com.example.examplemod.networking.subscribe.Versionable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class NpcData implements Versionable {

    public enum Gender {
        MALE,
        FEMALE
    }

    private boolean _isInitialized = true;
    public DataVersion version;
    public int npcId;
    public Gender gender;
    public String name;
    public Integer teamId;

    public NpcData() {
        _isInitialized = false;
    }

    public NpcData(Gender gender, String name) {
        this.gender = gender;
        this.name = name;
    }

    public NpcData(CompoundTag data) {
        version = new DataVersion(data.getCompound("version"));
        npcId = data.getInt("npcId");
        gender = Gender.values()[data.getInt("gender")];
        name = data.getString("name");
        if(data.contains("teamId")) {
            teamId = data.getInt("teamId");
        }
    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();
        data.put("version", version.toCompoundTag());
        data.putInt("npcId", npcId);
        data.putInt("gender", gender.ordinal());
        data.putString("name", name);
        if(teamId != null) {
            data.putInt("teamId", teamId);
        }
        return data;
    }

    public NpcData(FriendlyByteBuf buf) {
        version = new DataVersion(buf);
        npcId = buf.readInt();
        gender = buf.readEnum(Gender.class);
        name = buf.readUtf();
        teamId = buf.readInt();
        if(teamId == -1) {
            teamId = null;
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        version.toBytes(buf);
        buf.writeInt(npcId);
        buf.writeEnum(gender);
        buf.writeUtf(name);
        if(teamId == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(teamId);
        }
    }

    public NpcData copy() {
        if(!_isInitialized) return new NpcData();
        NpcData data = new NpcData(gender, name);
        data.npcId = npcId;
        data.teamId = teamId;
        return data;
    }

    @Override
    public DataVersion getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        NpcData other = (NpcData)obj;
        if(_isInitialized == other._isInitialized) return true;
        if(_isInitialized == false || other._isInitialized == false) return false;
        return gender == other.gender
            && name == other.name
            && teamId == other.teamId
            && npcId == other.npcId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash ^= gender == null ? 0 : gender.hashCode();
        hash ^= name == null ? 0 : name.hashCode();
        hash ^= teamId == null ? 0 : teamId.hashCode();
        hash ^= npcId;
        return hash;
    }

    public boolean isInitialized() {
        return _isInitialized;
    }

    static NpcData generate() {
        NpcGenerator gen = new NpcGenerator();
        return gen.generate();
    }
}
