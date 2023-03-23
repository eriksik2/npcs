package com.example.examplemod.npc.task;

import net.minecraft.resources.ResourceLocation;

public abstract class TaskType {
    private final ResourceLocation id;

    public TaskType(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }
    
    public abstract String getName();
}
