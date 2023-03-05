package com.example.examplemod.npc;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;

public class NpcTeam {

    private NpcManager manager;
    private Integer id;
    private String leader;
    private boolean isLeaderPlayer;

    private ArrayList<Integer> npcMembers;

    public NpcTeam(Integer id, NpcManager manager) {
        this.id = id;
        this.manager = manager;
        leader = null;
        isLeaderPlayer = false;
        npcMembers = new ArrayList<Integer>();
    }

    public NpcTeam(CompoundTag data, NpcManager manager) {
        this.manager = manager;
        id = data.getInt("id");
        leader = data.getString("leader");
        isLeaderPlayer = data.getBoolean("isLeaderPlayer");
        npcMembers = new ArrayList<Integer>();
        for(int value : data.getIntArray("npcMembers")) {
            npcMembers.add(value);
        }
    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", id);
        data.putString("leader", leader);
        data.putBoolean("isLeaderPlayer", isLeaderPlayer);
        data.putIntArray("npcMembers", npcMembers);
        return data;
    }

    Integer getId() {
        return id;
    }

    String getLeader() {
        return leader;
    }

    public NpcTeam setLeader(String newLeader) {
        leader = newLeader;
        manager.setDirty();
        return this;
    }

    public NpcTeam addNpcId(Integer npcId) {
        npcMembers.add(npcId);
        manager.setDirty();
        return this;
    }

    public NpcTeam removeNpcId(Integer npcId) {
        npcMembers.remove(npcId);
        manager.setDirty();
        return this;
    }

    public List<Integer> getNpcIds() {
        return npcMembers;
    }
}
