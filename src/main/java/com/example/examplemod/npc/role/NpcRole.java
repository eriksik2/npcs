package com.example.examplemod.npc.role;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.npc.NpcTeam;

import net.minecraft.nbt.CompoundTag;

public class NpcRole {
    private NpcTeam manager;
    private Integer id;
    private String name;
    private String description;

    private ArrayList<Integer> npcMembers;

    public NpcRole(Integer id, String name, String description, NpcTeam manager) {
        this.manager = manager;
        this.id = id;
        this.name = name;
        this.description = description;
        npcMembers = new ArrayList<Integer>();
    }

    public NpcRole(CompoundTag data, NpcTeam manager) {
        this.manager = manager;
        id = data.getInt("id");
        name = data.getString("name");
        description = data.getString("description");

        npcMembers = new ArrayList<Integer>();
        for(int value : data.getIntArray("npcMembers")) {
            npcMembers.add(value);
        }
    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", id);
        data.putString("name", name);
        data.putString("description", description);

        data.putIntArray("npcMembers", npcMembers);
        
        return data;
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

    public List<Integer> getNpcIds() {
        return npcMembers;
    }

    public NpcRole addNpcId(Integer npcId) {
        npcMembers.add(npcId);
        if(manager == null) throw new RuntimeException("NpcRole.addNpcId presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public NpcRole removeNpcId(Integer npcId) {
        npcMembers.remove(npcId);
        if(manager == null) throw new RuntimeException("NpcRole.removeNpcId presumably called on the client.");
        manager.setDirty();
        return this;
    }
}
