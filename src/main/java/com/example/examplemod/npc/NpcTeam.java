package com.example.examplemod.npc;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class NpcTeam {

    private NpcManager manager;
    private Integer id;
    private String name;
    private ArrayList<UUID> owners;

    private ArrayList<Integer> npcMembers;

    public NpcTeam(Integer id, NpcManager manager) {
        this.id = id;
        this.manager = manager;
        owners = new ArrayList<UUID>();
        npcMembers = new ArrayList<Integer>();
    }

    public NpcTeam(CompoundTag data, NpcManager manager) {
        this.manager = manager;
        id = data.getInt("id");
        name = data.getString("name");
        if(name == "") name = null;

        owners = new ArrayList<UUID>();
        for(Tag value : data.getList("owners", Tag.TAG_STRING)) {
            owners.add(UUID.fromString(value.getAsString()));
        }
        
        npcMembers = new ArrayList<Integer>();
        for(int value : data.getIntArray("npcMembers")) {
            npcMembers.add(value);
        }
    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", id);
        data.putString("name", name == null ? "" : name);

        ListTag ownersTag = new ListTag();
        for(UUID owner : owners) {
            ownersTag.add(StringTag.valueOf(owner.toString()));
        }

        data.putIntArray("npcMembers", npcMembers);
        return data;
    }

    public NpcTeam(FriendlyByteBuf buf) {
        this(buf.readNbt(), null);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(toCompoundTag());
    }

    Integer getId() {
        return id;
    }

    public String getName() {
        if(name == null) return "Team " + id;
        return name;
    }

    public NpcTeam setName(String newName) {
        name = newName;
        if(manager == null) throw new RuntimeException("NpcTeam.setName presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public boolean isOwner(Player player) {
        return owners.contains(player.getUUID());
    }

    public List<UUID> getOwners() {
        return owners;
    }

    public NpcTeam addOwner(Player player) {
        owners.add(player.getUUID());
        if(manager == null) throw new RuntimeException("NpcTeam.setLeader presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public NpcTeam removeOwner(Player player) {
        owners.remove(player.getUUID());
        if(manager == null) throw new RuntimeException("NpcTeam.setLeader presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public List<Integer> getNpcIds() {
        return npcMembers;
    }

    public NpcTeam addNpcId(Integer npcId) {
        npcMembers.add(npcId);
        if(manager == null) throw new RuntimeException("NpcTeam.setLeader presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public NpcTeam removeNpcId(Integer npcId) {
        npcMembers.remove(npcId);
        if(manager == null) throw new RuntimeException("NpcTeam.setLeader presumably called on the client.");
        manager.setDirty();
        return this;
    }
}
