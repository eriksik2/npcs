package com.example.examplemod.npc.task;

import java.util.EnumSet;
import java.util.List;

import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.NpcTaskProvider;

import net.minecraft.world.entity.ai.goal.Goal;

public class PerformTaskGoal extends Goal {

    private NpcEntity mob;

    private NpcTask currentTask;
    private Goal currentGoal;

    public PerformTaskGoal(NpcEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.allOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        if(mob.npcData == null) {
            return false;
        }
        if(currentGoal != null && currentGoal.canUse()) {
            return true;
        }
        NpcManager manager = NpcManager.get(mob.level);
        NpcTaskProvider provider = manager.getTaskProvider();
        List<NpcTask> tasks = provider.getTasksOf(mob.npcData.getId());
        for(NpcTask task : tasks) {
            Goal goal = task.getType().getGoal(task, mob);
            if(goal.canUse()) {
                currentTask = task;
                currentGoal = goal;
                return true;
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        if(this.mob.isVehicle()) return false;

        if(currentGoal == null) {
            return canUse();
        }

        NpcManager manager = NpcManager.get(mob.level);
        NpcTaskProvider provider = manager.getTaskProvider();
        List<NpcTask> tasks = provider.getTasksOf(mob.npcData.getId());
        if(!tasks.contains(currentTask)) {
            return false;
        }

        return currentGoal.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        if(currentGoal != null) {
            currentGoal.start();
        }
    }
  
    @Override
    public void stop() {
        if(currentGoal != null) {
            currentGoal.stop();
            currentTask = null;
            currentGoal = null;
        }

        super.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        if(currentGoal != null) {
            return currentGoal.requiresUpdateEveryTick();
        }
        return true;
    }

    @Override
    public void tick() {
        if(currentGoal != null) {
            currentGoal.tick();
        }
        super.tick();
    }
    
}
