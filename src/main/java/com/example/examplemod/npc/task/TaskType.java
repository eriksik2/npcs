package com.example.examplemod.npc.task;

import java.util.ArrayList;

import com.example.examplemod.npc.NpcEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class TaskType {
    private final ResourceLocation id;

    private final ArrayList<TaskParameterType<?, ?>> parameters = new ArrayList<>();

    public TaskType(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    protected <T extends TaskParameterType<?, ?>> T registerParameter(T parameter) {
        int slot = parameters.size();
        parameters.add(parameter);
        parameter.setSlot(slot);
        return parameter;
    }

    public ArrayList<TaskParameterType<?, ?>> getParameters() {
        return parameters;
    }
    
    public abstract String getName();

    public abstract Goal getGoal(NpcTask task, NpcEntity mob);
}
