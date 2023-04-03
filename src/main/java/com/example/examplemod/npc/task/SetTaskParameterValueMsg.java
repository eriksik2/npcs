package com.example.examplemod.npc.task;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class SetTaskParameterValueMsg implements Message {
    private Integer taskId;
    private Integer roleId;
    private Integer teamId;
    private Integer slot;
    private ResourceLocation taskTypeId;
    private Object value;

    public SetTaskParameterValueMsg(NpcTask task, Integer slot, Object value) {
        this.teamId = task.getManager().getManager().getId();
        this.roleId = task.getManager().getId();
        this.taskId = task.getId();
        this.slot = slot;
        this.taskTypeId = task.getType().getId();
        this.value = value;
    }

    public SetTaskParameterValueMsg(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        roleId = buf.readInt();
        taskId = buf.readInt();
        slot = buf.readInt();
        taskTypeId = buf.readResourceLocation();
        TaskType type = TaskRegistration.TASK_TYPES.getEntries().stream().filter(entry -> entry.getId().equals(taskTypeId)).findFirst().get().get();
        TaskParameterType<?, ?> parameterType = type.getParameters().get(slot);
        value = parameterType.deserialize(buf.readNbt());
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeInt(roleId);
        buf.writeInt(taskId);
        buf.writeInt(slot);
        buf.writeResourceLocation(taskTypeId);
        TaskType type = TaskRegistration.TASK_TYPES.getEntries().stream().filter(entry -> entry.getId().equals(taskTypeId)).findFirst().get().get();
        TaskParameterType<?, ?> parameterType = type.getParameters().get(slot);
        buf.writeNbt(parameterType.serializeUnsafe(value));
    }

    @Override
    public boolean handle(Supplier<Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            ServerPlayer player = ctx.getSender();
            NpcManager manager = NpcManager.get(player.level);
            NpcTeam team = manager.getTeam(teamId);
            if(team == null) {
                System.out.println("Could not find team with id " + teamId);
                return;
            }
            NpcRole role = team.getRole(roleId);
            if(role == null) {
                System.out.println("Could not find role with id " + roleId);
                return;
            }
            NpcTask task = role.getTask(taskId);
            if(task == null) {
                System.out.println("Could not find task with id " + taskId);
                return;
            }
            task.setUnsafe(slot, value);
        });
        return true;
    }
    
}
