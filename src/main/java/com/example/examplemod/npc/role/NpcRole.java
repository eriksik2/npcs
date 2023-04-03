package com.example.examplemod.npc.role;


import java.util.ArrayList;

import com.example.examplemod.npc.task.NpcTask;
import com.example.examplemod.npc.task.TaskType;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class NpcRole {
    public NpcTeam manager;
    private Integer id;
    private String name;
    private String description;

    private ArrayList<NpcTask> tasks = new ArrayList<>();
    private int nextTaskId = 0;

    public NpcRole(Integer id, String name, String description, NpcTeam manager) {
        this.manager = manager;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public NpcRole(CompoundTag data, NpcTeam manager) {
        this.manager = manager;
        id = data.getInt("id");
        name = data.getString("name");
        description = data.getString("description");

        ListTag tasksTag = data.getList("tasks", Tag.TAG_COMPOUND);
        for(Tag taskTag : tasksTag) {
            tasks.add(new NpcTask((CompoundTag) taskTag, this));
        }

        nextTaskId = data.getInt("nextTaskId");
    }

    public CompoundTag toCompoundTag() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", id);
        data.putString("name", name);
        data.putString("description", description);

        ListTag tasksTag = new ListTag();
        for(NpcTask task : tasks) {
            tasksTag.add(task.toCompoundTag());
        }
        data.put("tasks", tasksTag);

        data.putInt("nextTaskId", nextTaskId);
        
        return data;
    }

    public NpcRole(FriendlyByteBuf buf, NpcTeam manager) {
        this(buf.readNbt(), manager);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(toCompoundTag());
    }

    public void setDirty() {
        manager.setDirty();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash ^= name == null ? 0 : name.hashCode();
        hash ^= description == null ? 0 : description.hashCode();
        return hash;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
        if(manager == null) throw new RuntimeException("NpcRole.setName presumably called on the client.");
        setDirty();
    }

    public void setDescription(String description) {
        this.description = description;
        if(manager == null) throw new RuntimeException("NpcRole.setDescription presumably called on the client.");
        setDirty();
    }

    public ArrayList<NpcTask> getTasks() {
        return tasks;
    }

    public NpcTask addTask(TaskType taskType) {
        NpcTask task = new NpcTask(this, nextTaskId++, taskType);
        tasks.add(task);
        if(manager == null) throw new RuntimeException("NpcRole.addTask presumably called on the client.");
        manager.taskWasAddedToRole(this, task);
        setDirty();
        return task;
    }

    public void removeTask(NpcTask task) {
        tasks.remove(task);
        if(manager == null) throw new RuntimeException("NpcRole.removeTask presumably called on the client.");
        manager.taskWasRemovedFromRole(this, task);
        setDirty();
    }
}
