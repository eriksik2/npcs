package com.example.examplemod.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.task.NpcTask;
import com.example.examplemod.npc.team.NpcTeam;

// Keeps track of all the tasks assigned to all npcs.
// This is not the canonical place where this is tracked - it is tracked in the NpcTeam class.
// This class is used to provide a way to get all the tasks assigned to a given npc without having to iterate through all the teams.
public class NpcTaskProvider {
    private NpcManager manager;
    private HashMap<Integer, ArrayList<NpcTask>> npcTasks = new HashMap<>();

    public NpcTaskProvider(NpcManager manager) {
        this.manager = manager;
    }

    public void repopulate() {
        npcTasks.clear();
        for (NpcTeam team : manager.getTeams()) {
            for(Integer npcId : team.getNpcIds()) {
                for(Integer roleId : team.getRolesOf(npcId)) {
                    NpcRole role = team.getRole(roleId);
                    List<NpcTask> tasks = role.getTasks();
                    if(tasks.size() == 0) continue;
                    if(!npcTasks.containsKey(npcId)) {
                        npcTasks.put(npcId, new ArrayList<>());
                    }
                    npcTasks.get(npcId).addAll(tasks);
                }
            }
        }
    }

    public List<NpcTask> getTasksOf(int npcId) {
        if(!npcTasks.containsKey(npcId)) {
            return new ArrayList<>();
        }
        return npcTasks.get(npcId);
    }

    public void registerTask(int npcId, NpcTask task) {
        if(!npcTasks.containsKey(npcId)) {
            npcTasks.put(npcId, new ArrayList<>());
        }
        npcTasks.get(npcId).add(task);
    }

    public void unregisterTask(int npcId, NpcTask task) {
        if(!npcTasks.containsKey(npcId)) {
            return;
        }
        npcTasks.get(npcId).remove(task);
    }
}
