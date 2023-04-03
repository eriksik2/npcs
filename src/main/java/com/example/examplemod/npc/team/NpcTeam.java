package com.example.examplemod.npc.team;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.example.examplemod.networking.subscribe.DataVersion;
import com.example.examplemod.networking.subscribe.Versionable;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.NpcTaskProvider;
import com.example.examplemod.npc.area.NpcArea;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.task.NpcTask;
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

public class NpcTeam implements Versionable {

    private final DataVersion version;

    private NpcManager manager;
    private Integer id;
    private int nextRoleId = 0;
    private int nextAreaId = 0;

    
    private String name;
    private ArrayList<UUID> owners;
    private ArrayList<Integer> npcMembers;
    private ArrayList<NpcRole> roles;
    private NpcAssignedRoles npcAssignedRoles;
    private ArrayList<NpcArea> areas;
    private RoleAssignedAreas roleAssignedAreas;


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
        version = new DataVersion(id);
        this.id = id;
        this.manager = manager;
        owners = new ArrayList<UUID>();
        npcMembers = new ArrayList<Integer>();
        roles = new ArrayList<NpcRole>();
        npcAssignedRoles = new NpcAssignedRoles();
        areas = new ArrayList<NpcArea>();
        roleAssignedAreas = new RoleAssignedAreas();
    }

    public NpcTeam(CompoundTag data, NpcManager manager) {
        this.manager = manager;

        version = new DataVersion(data.getCompound("version"));

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

        areas = new ArrayList<NpcArea>();
        for(Tag value : data.getList("areas", Tag.TAG_COMPOUND)) {
            areas.add(new NpcArea((CompoundTag)value, this));
        }
        nextAreaId = data.getInt("nextAreaId");

        roleAssignedAreas = new RoleAssignedAreas(data.getList("roleAssignedAreas", Tag.TAG_COMPOUND));

    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();

        data.put("version", version.toCompoundTag());

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

        ListTag areasTag = new ListTag();
        for(NpcArea area : areas) {
            areasTag.add(area.toCompoundTag());
        }
        data.put("areas", areasTag);
        data.putInt("nextAreaId", nextAreaId);

        data.put("roleAssignedAreas", roleAssignedAreas.toListTag());

        return data;
    }

    public NpcTeam(FriendlyByteBuf buf, NpcManager manager) {
        this(buf.readNbt(), manager);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(toCompoundTag());
    }

    @Override
    public DataVersion getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash ^= name == null ? 0 : name.hashCode();
        hash ^= owners == null ? 0 : owners.hashCode();
        hash ^= npcMembers == null ? 0 : npcMembers.hashCode();
        hash ^= roles == null ? 0 : roles.hashCode();
        hash ^= npcAssignedRoles == null ? 0 : npcAssignedRoles.hashCode();
        hash ^= areas == null ? 0 : areas.hashCode();
        hash ^= roleAssignedAreas == null ? 0 : roleAssignedAreas.hashCode();
        return hash;
    }

    public void onRemove() {
        NpcTaskProvider provider = manager.getTaskProvider();
        for(NpcRole role : roles) {
            for(Integer npcId : npcAssignedRoles.getNpcs(role.getId())) {
                for(NpcTask task : role.getTasks()) {
                    provider.unregisterTask(npcId, task);
                }
            }
        }
    }

    public void setDirty() {
        version.markDirty();
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
        if(manager == null) throw new RuntimeException("NpcTeam.removeNpcId presumably called on the client.");
        boolean didRemove = npcMembers.remove(npcId);
        if(!didRemove) return this;

        NpcTaskProvider provider = manager.getTaskProvider();
        
        for(Integer roleId : npcAssignedRoles.getRoles(npcId)) {
            NpcRole role = getRole(roleId);
            for(NpcTask task : role.getTasks()) {
                provider.unregisterTask(npcId, task);
            }
        }

        npcAssignedRoles.clearRoles(npcId);
        setDirty();
        return this;
    }

    public NpcRole getRole(Integer roleId) {
        for(NpcRole role : roles) {
            if(role.getId().equals(roleId)) return role;
        }
        return null;
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
        if(manager == null) throw new RuntimeException("NpcTeam.assignRole presumably called on the client.");

        NpcRole role = roles.stream().filter(r -> r.getId() == roleId).findFirst().orElse(null);
        NpcTaskProvider provider = manager.getTaskProvider();
        for(NpcTask task : role.getTasks()) {
            provider.registerTask(npcId, task);
        }

        boolean didAdd = npcAssignedRoles.addRole(npcId, roleId);
        if(!didAdd) return this;
        setDirty();
        return this;
    }

    public NpcTeam unassignRole(Integer npcId, Integer roleId) {
        if(manager == null) throw new RuntimeException("NpcTeam.unassignRole presumably called on the client.");

        NpcRole role = roles.stream().filter(r -> r.getId() == roleId).findFirst().orElse(null);
        NpcTaskProvider provider = manager.getTaskProvider();
        for(NpcTask task : role.getTasks()) {
            provider.unregisterTask(npcId, task);
        }

        boolean didRemove = npcAssignedRoles.removeRole(npcId, roleId);
        if(!didRemove) return this;
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
        if(manager == null) throw new RuntimeException("NpcTeam.removeRole presumably called on the client.");
        NpcRole role = roles.stream().filter(r -> r.getId() == roleId).findFirst().orElse(null);
        if(role == null) return this;
        boolean didRemove = roles.remove(role);
        if(!didRemove) return this;

        NpcTaskProvider provider = manager.getTaskProvider();
        for(Integer npcId : npcAssignedRoles.getNpcs(role.getId())) {
            for(NpcTask task : role.getTasks()) {
                provider.unregisterTask(npcId, task);
            }
        }

        npcAssignedRoles.removeRole(roleId);
        roleAssignedAreas.removeRole(roleId);
        setDirty();
        return this;
    }

    public void taskWasAddedToRole(NpcRole role, NpcTask task) {
        NpcTaskProvider provider = manager.getTaskProvider();
        for(Integer npcId : npcAssignedRoles.getNpcs(role.getId())) {
            provider.registerTask(npcId, task);
        }
    }

    public void taskWasRemovedFromRole(NpcRole role, NpcTask task) {
        NpcTaskProvider provider = manager.getTaskProvider();
        for(Integer npcId : npcAssignedRoles.getNpcs(role.getId())) {
            provider.unregisterTask(npcId, task);
        }
    }

    public List<NpcArea> getAreas() {
        return areas;
    }

    public NpcArea getArea(int areaId) {
        return areas.stream().filter(area -> area.getId() == areaId).findFirst().orElse(null);
    }

    public NpcArea addArea() {
        NpcArea area = new NpcArea(this, nextAreaId);
        areas.add(area);
        nextAreaId++;
        if(manager == null) throw new RuntimeException("NpcTeam.addArea presumably called on the client.");
        setDirty();
        return area;
    }

    public NpcTeam removeArea(int areaId) {
        boolean didRemove = areas.removeIf(area -> area.getId() == areaId);
        if(!didRemove) return this;
        roleAssignedAreas.removeArea(areaId);
        if(manager == null) throw new RuntimeException("NpcTeam.removeArea presumably called on the client.");
        setDirty();
        return this;
    }

    public void assignAreaToRole(int areaId, int roleId) {
        roleAssignedAreas.associateRoleWithArea(roleId, areaId);
        if(manager == null) throw new RuntimeException("NpcTeam.assignAreaToRole presumably called on the client.");
        setDirty();
    }

    public void unassignAreaFromRole(int areaId, int roleId) {
        roleAssignedAreas.disassociateRoleWithArea(roleId, areaId);
        if(manager == null) throw new RuntimeException("NpcTeam.unassignAreaFromRole presumably called on the client.");
        setDirty();
    }

    public List<Integer> getAreasOf(int roleId) {
        return roleAssignedAreas.getAreas(roleId);
    }

    public boolean roleHasArea(int roleId, int areaId) {
        return roleAssignedAreas.roleHasArea(roleId, areaId);
    }
}
