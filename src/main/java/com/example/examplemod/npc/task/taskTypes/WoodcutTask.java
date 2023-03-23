package com.example.examplemod.npc.task.taskTypes;

import com.example.examplemod.npc.task.TaskRegistration;
import com.example.examplemod.npc.task.TaskType;

import net.minecraft.resources.ResourceLocation;

public class WoodcutTask extends TaskType {

    public WoodcutTask() {
        super(TaskRegistration.WOODCUT_TASK.getId());
    }

    @Override
    public String getName() {
        return "Cut wood";
    }
    
}
