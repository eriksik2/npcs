package com.example.examplemod.npc.area;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToggleRoleHasAreaMsg implements Message {

    public static final String MESSAGE = "message.ToggleRoleHasAreaMsg";
    
    private Integer teamId;
    private Integer roleId;
    private Integer areaId;

    public ToggleRoleHasAreaMsg(Integer teamId, Integer roleId, Integer areaId) {
        this.teamId = teamId;
        this.roleId = roleId;
        this.areaId = areaId;
    }

    public ToggleRoleHasAreaMsg(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        roleId = buf.readInt();
        areaId = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeInt(roleId);
        buf.writeInt(areaId);
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
            if(team.roleHasArea(roleId, areaId))
                team.unassignAreaFromRole(areaId, roleId);
            else
                team.assignAreaToRole(areaId, roleId);
        });
        return true;
    }
    
}
