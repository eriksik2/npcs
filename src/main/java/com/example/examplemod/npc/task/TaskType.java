package com.example.examplemod.npc.task;

import com.example.examplemod.npc.NpcEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class TaskType {
    private final ResourceLocation id;

    public TaskType(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }
    
    public abstract String getName();

    public abstract Goal getGoal(NpcTask task, NpcEntity mob);
}
