package com.example.examplemod.npc.role;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class NpcRole {
    public NpcTeam manager;
    private Integer id;
    private String name;
    private String description;

    public NpcRole(Integer id, String name, String description, NpcTeam manager) {
        this.manager = manager;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public NpcRole(CompoundTag data, NpcTeam manager) {
        this.manager = manager;
        id = data.getInt("id");
        name = data.getString("name");
        description = data.getString("description");
    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", id);
        data.putString("name", name);
        data.putString("description", description);
        
        return data;
    }

    public NpcRole(FriendlyByteBuf buf, NpcTeam manager) {
        this(buf.readNbt(), manager);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(toCompoundTag());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash ^= name == null ? 0 : name.hashCode();
        hash ^= description == null ? 0 : description.hashCode();
        return hash;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
        if(manager == null) throw new RuntimeException("NpcRole.setName presumably called on the client.");
        manager.setDirty();
    }

    public void setDescription(String description) {
        this.description = description;
        if(manager == null) throw new RuntimeException("NpcRole.setDescription presumably called on the client.");
        manager.setDirty();
    }
}
