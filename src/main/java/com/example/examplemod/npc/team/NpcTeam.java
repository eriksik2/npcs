package com.example.examplemod.npc.team;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.role.NpcRole;

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

    private ArrayList<NpcRole> roles;
    private int nextRoleId = 0;

    public NpcTeam(Integer id, NpcManager manager) {
        this.id = id;
        this.manager = manager;
        owners = new ArrayList<UUID>();
        npcMembers = new ArrayList<Integer>();
        roles = new ArrayList<NpcRole>();
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

        roles = new ArrayList<NpcRole>();
        for(Tag value : data.getList("roles", Tag.TAG_COMPOUND)) {
            roles.add(new NpcRole((CompoundTag) value, this));
        }
        nextRoleId = data.getInt("nextRoleId");
    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", id);
        data.putString("name", name == null ? "" : name);

        ListTag ownersTag = new ListTag();
        for(UUID owner : owners) {
            ownersTag.add(StringTag.valueOf(owner.toString()));
        }
        data.put("owners", ownersTag);

        data.putIntArray("npcMembers", npcMembers);

        ListTag rolesTag = new ListTag();
        for(NpcRole role : roles) {
            rolesTag.add(role.toCompoundTag());
        }
        data.put("roles", rolesTag);
        data.putInt("nextRoleId", nextRoleId);

        return data;
    }

    public NpcTeam(FriendlyByteBuf buf) {
        this(buf.readNbt(), null);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(toCompoundTag());
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ owners.hashCode() ^ npcMembers.hashCode() ^ roles.hashCode();
    }

    public void setDirty() {
        manager.setDirty();
    }

    public Integer getId() {
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
        if(manager == null) throw new RuntimeException("NpcTeam.addOwner presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public NpcTeam removeOwner(Player player) {
        owners.remove(player.getUUID());
        if(manager == null) throw new RuntimeException("NpcTeam.removeOwner presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public List<Integer> getNpcIds() {
        return npcMembers;
    }

    public NpcTeam addNpcId(Integer npcId) {
        npcMembers.add(npcId);
        if(manager == null) throw new RuntimeException("NpcTeam.addNpcId presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public NpcTeam removeNpcId(Integer npcId) {
        boolean didRemove = npcMembers.remove(npcId);
        if(!didRemove) return this;
        for(NpcRole role : roles) {
            role.removeNpcId(npcId);
        }
        if(manager == null) throw new RuntimeException("NpcTeam.removeNpcId presumably called on the client.");
        manager.setDirty();
        return this;
    }

    public List<NpcRole> getRoles() {
        return roles;
    }

    public NpcRole getRoleOf(int npcId) {
        for(NpcRole role : roles) {
            if(role.getNpcIds().contains(npcId)) return role;
        }
        return null;
    }

    public NpcRole addRole(String name, String description) {
        NpcRole role = new NpcRole(nextRoleId, name, description, this);
        roles.add(role);
        nextRoleId++;
        if(manager == null) throw new RuntimeException("NpcTeam.addRole presumably called on the client.");
        manager.setDirty();
        return role;
    }

    public NpcTeam removeRole(int roleId) {
        roles.removeIf(role -> role.getId() == roleId);
        if(manager == null) throw new RuntimeException("NpcTeam.removeRole presumably called on the client.");
        manager.setDirty();
        return this;
    }
}
