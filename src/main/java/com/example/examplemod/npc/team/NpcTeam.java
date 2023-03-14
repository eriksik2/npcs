package com.example.examplemod.npc.team;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.setup.Registration;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

class NpcAssignedRoles {
    // npcId -> roles
    private HashMap<Integer, HashSet<Integer>> npcRolesMap = new HashMap<>();

    public NpcAssignedRoles() {
    }

    public NpcAssignedRoles(ListTag tag) {
        for(Tag value : tag) {
            CompoundTag entry = (CompoundTag) value;
            int npcId = entry.getInt("npcId");
            HashSet<Integer> roles = new HashSet<>();
            for(int roleId : entry.getIntArray("roles")) {
                roles.add(roleId);
            }
            npcRolesMap.put(npcId, roles);
        }
    }

    public ListTag toListTag() {
        ListTag tag = new ListTag();
        for(Integer npcId : npcRolesMap.keySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("npcId", npcId);
            HashSet<Integer> roles = npcRolesMap.get(npcId);
            int[] rolesArray = new int[roles.size()];
            int i = 0;
            for(Integer roleId : roles) {
                rolesArray[i] = roleId;
                i++;
            }
            entry.putIntArray("roles", rolesArray);
            tag.add(entry);
        }
        return tag;
    }

    public boolean npcHasRole(Integer npcId, Integer roleId) {
        HashSet<Integer> roles = npcRolesMap.get(npcId);
        if(roles == null) return false;
        return roles.contains(roleId);
    }

    public List<Integer> getRoles(Integer npcId) {
        HashSet<Integer> roles = npcRolesMap.get(npcId);
        if(roles == null) return new ArrayList<>();
        return new ArrayList<>(roles);
    }

    public List<Integer> getNpcs(Integer roleId) {
        List<Integer> npcs = new ArrayList<>();
        for(Integer npcId : npcRolesMap.keySet()) {
            HashSet<Integer> roles = npcRolesMap.get(npcId);
            if(roles == null) continue;
            if(roles.contains(roleId)) npcs.add(npcId);
        }
        return npcs;
    }

    public boolean addRole(Integer npcId, Integer roleId) {
        HashSet<Integer> roles = npcRolesMap.get(npcId);
        if(roles == null) {
            roles = new HashSet<>();
            npcRolesMap.put(npcId, roles);
        }
        boolean didAdd = roles.add(roleId);
        if(!didAdd) return false;
        return true;
    }

    public boolean removeRole(Integer npcId, Integer roleId) {
        HashSet<Integer> roles = npcRolesMap.get(npcId);
        if(roles == null) return false;
        roles.remove(roleId);
        if(roles.size() == 0) npcRolesMap.remove(npcId);
        return true;
    }

    public boolean removeRole(Integer roleId) {
        boolean didRemove = false;
        for(Integer npcId : npcRolesMap.keySet()) {
            HashSet<Integer> roles = npcRolesMap.get(npcId);
            if(roles == null) continue;
            if(roles.remove(roleId)) didRemove = true;
            if(roles.size() == 0) npcRolesMap.remove(npcId);
        }
        return didRemove;
    }

    public void clearRoles(Integer npcId) {
        npcRolesMap.remove(npcId);
    }

    @Override
    public int hashCode() {
        return npcRolesMap.hashCode();
    }
   
}

public class NpcTeam {

    private NpcManager manager;
    private Integer id;
    private int nextRoleId = 0;

    
    private String name;
    private ArrayList<UUID> owners;
    private ArrayList<Integer> npcMembers;
    private ArrayList<NpcRole> roles;
    private NpcAssignedRoles npcAssignedRoles;


    public static NpcTeam initialNpcTeam(Integer id, NpcManager manager) {
        NpcTeam team = new NpcTeam(id, manager);
        team.addRole("Wheat farmer", "Plant and harvest wheat.");
        team.addRole("Woodcutter", "Cut down trees and plant saplings.");
        team.addRole("Fisher", "Fish for fish and other items.");
        team.addRole("Soldier", "Fight mobs and players.");
        team.addRole("Cook", "Cook food.");
        team.addRole("Labourer", "Sort items into the right chests.");
        team.addRole("Cow rancher", "Feed and breed cows.");
        return team;
    }

    public NpcTeam(Integer id, NpcManager manager) {
        this.id = id;
        this.manager = manager;
        owners = new ArrayList<UUID>();
        npcMembers = new ArrayList<Integer>();
        roles = new ArrayList<NpcRole>();
        npcAssignedRoles = new NpcAssignedRoles();
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

        npcAssignedRoles = new NpcAssignedRoles(data.getList("npcAssignedRoles", Tag.TAG_COMPOUND));

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

        data.put("npcAssignedRoles", npcAssignedRoles.toListTag());

        return data;
    }

    public NpcTeam(FriendlyByteBuf buf, NpcManager manager) {
        this(buf.readNbt(), manager);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(toCompoundTag());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash ^= name == null ? 0 : name.hashCode();
        hash ^= owners == null ? 0 : owners.hashCode();
        hash ^= npcMembers == null ? 0 : npcMembers.hashCode();
        hash ^= roles == null ? 0 : roles.hashCode();
        hash ^= npcAssignedRoles == null ? 0 : npcAssignedRoles.hashCode();
        return hash;
    }

    public void setDirty() {
        Registration.TEAM_SUBSCRIPTION_BROKER.get().publish(id, this);
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
        setDirty();
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
        setDirty();
        return this;
    }

    public NpcTeam removeOwner(Player player) {
        owners.remove(player.getUUID());
        if(manager == null) throw new RuntimeException("NpcTeam.removeOwner presumably called on the client.");
        setDirty();
        return this;
    }

    public List<Integer> getNpcIds() {
        return npcMembers;
    }

    public NpcTeam addNpcId(Integer npcId) {
        npcMembers.add(npcId);
        if(manager == null) throw new RuntimeException("NpcTeam.addNpcId presumably called on the client.");
        setDirty();
        return this;
    }

    public NpcTeam removeNpcId(Integer npcId) {
        boolean didRemove = npcMembers.remove(npcId);
        if(!didRemove) return this;
        npcAssignedRoles.clearRoles(npcId);
        if(manager == null) throw new RuntimeException("NpcTeam.removeNpcId presumably called on the client.");
        setDirty();
        return this;
    }

    public List<NpcRole> getRoles() {
        return roles;
    }

    public List<Integer> getRolesOf(Integer npcId) {
        return npcAssignedRoles.getRoles(npcId);
    }

    public List<Integer> getNpcsOf(Integer roleId) {
        return npcAssignedRoles.getNpcs(roleId);
    }

    public NpcTeam assignRole(Integer npcId, Integer roleId) {
        boolean didAdd = npcAssignedRoles.addRole(npcId, roleId);
        if(!didAdd) return this;
        if(manager == null) throw new RuntimeException("NpcTeam.assignRole presumably called on the client.");
        setDirty();
        return this;
    }

    public NpcTeam unassignRole(Integer npcId, Integer roleId) {
        boolean didRemove = npcAssignedRoles.removeRole(npcId, roleId);
        if(!didRemove) return this;
        if(manager == null) throw new RuntimeException("NpcTeam.unassignRole presumably called on the client.");
        setDirty();
        return this;
    }

    public NpcRole addRole(String name, String description) {
        NpcRole role = new NpcRole(nextRoleId, name, description, this);
        roles.add(role);
        nextRoleId++;
        if(manager == null) throw new RuntimeException("NpcTeam.addRole presumably called on the client.");
        setDirty();
        return role;
    }

    public NpcTeam removeRole(int roleId) {
        boolean didRemove = roles.removeIf(role -> role.getId() == roleId);
        if(!didRemove) return this;
        npcAssignedRoles.removeRole(roleId);
        if(manager == null) throw new RuntimeException("NpcTeam.removeRole presumably called on the client.");
        setDirty();
        return this;
    }
}
