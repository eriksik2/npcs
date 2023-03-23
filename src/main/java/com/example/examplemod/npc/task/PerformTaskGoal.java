package com.example.examplemod.npc.task;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class PerformTaskGoal extends Goal {

    private NpcEntity mob;

    private NpcManager manager;
    private NpcTeam team;
    private Player player;

    public PerformTaskGoal(NpcEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if(mob.npcData == null) {
            return false;
        }
        manager = NpcManager.get(mob.level);
        team = manager.getNpcTeam(mob.npcData);
        if(getRoles().isEmpty()) {
            return false;
        }
        List<? extends Player> players = mob.level.players();
        if(players.isEmpty()) {
            return false;
        }
        player = players.get(0);
        return true;
    }

    protected List<NpcRole> getRoles() {
        if(mob.npcId == null || team == null) {
            return new ArrayList<>();
        }
        List<Integer> roleIds = team.getRolesOf(mob.npcId);
        if(roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return roleIds.stream().map(id -> team.getRole(id)).toList();
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && !this.mob.isVehicle() && canUse();
    }

    @Override
    public void start() {
        super.start();
        mob.getNavigation().moveTo(player, 1);
    }
  
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }
    
}
