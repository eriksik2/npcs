package com.example.examplemod.npc;

import com.example.examplemod.generator.NpcGenerator;
import com.example.examplemod.networking.subscribe.DataVersion;
import com.example.examplemod.networking.subscribe.Versionable;
import com.example.examplemod.setup.Registration;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class NpcData implements Versionable {

    public enum Gender {
        MALE,
        FEMALE
    }

    private boolean _isInitialized = true;
    private NpcManager manager;
    private DataVersion version;
    private int npcId;
    private Gender gender;
    private String name;
    private Integer teamId;

    public NpcData() {
        _isInitialized = false;
    }

    public NpcData(Gender gender, String name, NpcManager manager) {
        this.gender = gender;
        this.name = name;
        this.manager = manager;
    }

    public NpcData(CompoundTag data, NpcManager manager) {
        this.manager = manager;
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

    public NpcData(FriendlyByteBuf buf, NpcManager manager) {
        this.manager = manager;
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
        NpcData data = new NpcData(gender, name, manager);
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

    public void setDirty() {
        version.markDirty();
        Registration.NPC_DATA_SUBSCRIPTION_BROKER.get().publish(getId(), this);
        if(manager != null) manager.setDirty();
    }

    public int getId() {
        return npcId;
    }

    public void setId(int id) {
        npcId = id;
        version = new DataVersion(id);
        setDirty();
    }

    public void setManager(NpcManager manager) {
        this.manager = manager;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
        setDirty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setDirty();
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
        setDirty();
    }

    public boolean isInitialized() {
        return _isInitialized;
    }

    static NpcData generate() {
        NpcGenerator gen = new NpcGenerator();
        return gen.generate();
    }
}
