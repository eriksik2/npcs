package com.example.examplemod.npc.task.taskTypes;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.task.NpcTask;
import com.example.examplemod.npc.task.TaskRegistration;
import com.example.examplemod.npc.task.TaskType;
import com.example.examplemod.npc.task.taskParameterTypes.IntParameter;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cow;

public class KillMobTask extends TaskType {
    
    private IntParameter targetNum = new IntParameter("Keep alive", 0)
        .withValidator("Value must be a number greater than 0", (value) -> value > 0);

    public KillMobTask() {
        super(TaskRegistration.KILL_MOB_TASK.getId());

        registerParameter(targetNum);
    }

    @Override
    public String getName() {
        return "Butcher";
    }

    @Override
    public Goal getGoal(NpcTask task, NpcEntity mob) {
        return new Goal() {
            private ArrayList<Cow> cows = new ArrayList<>();

            @Override
            public boolean canUse() {
                List<Cow> foundCows = mob.getLevel().getNearbyEntities(Cow.class, TargetingConditions.forCombat().range(10), mob, mob.getBoundingBox().inflate(10));
                if(foundCows.size() <= task.get(targetNum)) return false;
                cows.clear();
                cows.addAll(foundCows);
                return true;
            }

            @Override
            public boolean canContinueToUse() {
                if(cows.size() <= task.get(targetNum)) return canUse();
                return true;
            }

            @Override
            public boolean requiresUpdateEveryTick() {
                return true;
            }

            @Override
            public void tick() {
                if(cows.size() <= task.get(targetNum)) return;
                Cow nextCow = cows.get(0);
                while(!nextCow.isAlive()) {
                    cows.remove(0);
                    if(cows.size() <= task.get(targetNum)) return;
                    nextCow = cows.get(0);
                }
                if(mob.distanceToSqr(nextCow) < 3) {
                    mob.doHurtTarget(nextCow);
                } else {
                    mob.getNavigation().moveTo(nextCow, 1);
                }
            }
        };
    }
    
}
