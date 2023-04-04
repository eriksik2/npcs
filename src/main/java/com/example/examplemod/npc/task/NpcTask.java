package com.example.examplemod.npc.task;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.npc.role.NpcRole;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

public class NpcTask {
    
    private NpcRole manager;
    private Integer id;
    private TaskType type;

    private List<Object> parameterValues;

    public NpcTask(NpcRole manager, Integer id, TaskType type) {
        this.manager = manager;
        this.id = id;
        this.type = type;
        this.parameterValues = (List<Object>)type.getParameters().stream().map(parameter -> parameter.getDefaultValue()).toList();
    }

    public NpcTask(CompoundTag tag, NpcRole manager) {
        this.manager = manager;
        id = tag.getInt("id");
        ResourceLocation typeId = new ResourceLocation(tag.getString("type"));
        type = TaskRegistration.TASK_TYPES.getEntries().stream().filter(entry -> entry.getId().equals(typeId)).findFirst().get().get();

        ListTag parameterList = tag.getList("parameters", CompoundTag.TAG_COMPOUND);
        parameterValues = new ArrayList<>();
        for (int i = 0; i < parameterList.size(); i++) {
            CompoundTag parameterTag = parameterList.getCompound(i);
            Object value = type.getParameters().get(i).deserialize(parameterTag);
            parameterValues.add(value);
        }
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", id);
        tag.putString("type", type.getId().toString());

        ListTag parameterList = new ListTag();
        for (int i = 0; i < type.getParameters().size(); i++) {
            CompoundTag parameterTag = type.getParameters().get(i).serializeUnsafe(parameterValues.get(i));
            parameterList.add(parameterTag);
        }
        tag.put("parameters", parameterList);
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

    public NpcRole getManager() {
        return manager;
    }

    public <TValue> void setUnsafe(int slot, TValue value) {
        parameterValues.set(slot, value);
        setDirty();
    }

    public <TValue, TOutput> TOutput get(TaskParameterType<TValue, TOutput> parameter) {
        return parameter.apply(getValue(parameter));
    }

    public <TValue, TOutput> TValue getValue(TaskParameterType<TValue, TOutput> parameter) {
        return (TValue)parameterValues.get(parameter.getSlot());
    }
}
