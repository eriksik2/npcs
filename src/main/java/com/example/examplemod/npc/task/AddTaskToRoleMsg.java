package com.example.examplemod.npc.task;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class AddTaskToRoleMsg implements Message {
    private Integer teamId;
    private Integer roleId;
    private ResourceLocation taskType;

    public AddTaskToRoleMsg(Integer teamId, Integer roleId, ResourceLocation taskType) {
        this.teamId = teamId;
        this.roleId = roleId;
        this.taskType = taskType;
    }

    public AddTaskToRoleMsg(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        roleId = buf.readInt();
        taskType = buf.readResourceLocation();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeInt(roleId);
        buf.writeResourceLocation(taskType);
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
            TaskType type = TaskRegistration.TASK_TYPES.getEntries().stream().filter(entry -> entry.getId().equals(taskType)).findFirst().get().get();
            role.addTask(type);
        });
        return true;
    }
}
