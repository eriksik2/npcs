package com.example.examplemod.npc.task;

import com.example.examplemod.npc.role.NpcRole;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class NpcTask {
    
    private NpcRole manager;
    private Integer id;
    private TaskType type;

    public NpcTask(NpcRole manager, Integer id, TaskType type) {
        this.manager = manager;
        this.id = id;
        this.type = type;
    }

    public NpcTask(CompoundTag tag, NpcRole manager) {
        this.manager = manager;
        id = tag.getInt("id");
        ResourceLocation typeId = new ResourceLocation(tag.getString("type"));
        type = TaskRegistration.TASK_TYPES.getEntries().stream().filter(entry -> entry.getId().equals(typeId)).findFirst().get().get();
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", id);
        tag.putString("type", type.getId().toString());
        return tag;
    }

    public void setDirty() {
        manager.setDirty();
    }

    public Integer getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }
}
