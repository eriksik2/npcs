package com.example.examplemod.npc.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class RoleAssignedAreas {
    // roleId -> areaId
    private HashMap<Integer, HashSet<Integer>> roleAreasMap = new HashMap<>();

    public RoleAssignedAreas() {}

    public RoleAssignedAreas(ListTag tag) {
        for(Tag value : tag) {
            CompoundTag entry = (CompoundTag) value;
            int roleId = entry.getInt("roleId");
            HashSet<Integer> roles = new HashSet<>();
            for(int areaId : entry.getIntArray("areas")) {
                roles.add(areaId);
            }
            roleAreasMap.put(roleId, roles);
        }
    }

    public ListTag toListTag() {
        ListTag tag = new ListTag();
        for(Integer roleId : roleAreasMap.keySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("roleId", roleId);
            HashSet<Integer> areas = roleAreasMap.get(roleId);
            int[] areasArray = new int[areas.size()];
            int i = 0;
            for(Integer areaId : areas) {
                areasArray[i] = areaId;
                i++;
            }
            entry.putIntArray("areas", areasArray);
            tag.add(entry);
        }
        return tag;
    }

    public boolean roleHasArea(Integer roleId, Integer areaId) {
        HashSet<Integer> areas = roleAreasMap.get(roleId);
        if(areas == null) return false;
        return areas.contains(areaId);
    }

    public List<Integer> getAreas(Integer roleId) {
        HashSet<Integer> areas = roleAreasMap.get(roleId);
        if(areas == null) return new ArrayList<>();
        return new ArrayList<>(areas);
    }

    public List<Integer> getRolesWithArea(Integer areaId) {
        List<Integer> roles = new ArrayList<>();
        for(Integer roleId : roleAreasMap.keySet()) {
            HashSet<Integer> areas = roleAreasMap.get(roleId);
            if(areas == null) continue;
            if(areas.contains(areaId)) roles.add(roleId);
        }
        return roles;
    }

    public boolean associateRoleWithArea(Integer roleId, Integer areaId) {
        HashSet<Integer> areas = roleAreasMap.get(roleId);
        if(areas == null) {
            areas = new HashSet<>();
            roleAreasMap.put(roleId, areas);
        }
        boolean didAdd = areas.add(areaId);
        if(!didAdd) return false;
        return true;
    }

    public boolean disassociateRoleWithArea(Integer roleId, Integer areaId) {
        HashSet<Integer> areas = roleAreasMap.get(roleId);
        if(areas == null) return false;
        boolean didRemove = areas.remove(areaId);
        if(areas.size() == 0) roleAreasMap.remove(roleId);
        return didRemove;
    }

    public void removeArea(Integer areaId) {
        for(Integer roleId : roleAreasMap.keySet()) {
            HashSet<Integer> areas = roleAreasMap.get(roleId);
            if(areas == null) continue;
            areas.remove(areaId);
            if(areas.size() == 0) roleAreasMap.remove(roleId);
        }
    }

    public void removeRole(Integer roleId) {
        roleAreasMap.remove(roleId);
    }

    @Override
    public int hashCode() {
        return roleAreasMap.hashCode();
    }
    
}
