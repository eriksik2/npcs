
package com.example.examplemod.npc.role;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToggleNpcHasRoleMsg implements Message {

    private Integer teamId;
    private Integer roleId;
    private Integer npcId;

    public ToggleNpcHasRoleMsg(Integer teamId, Integer roleId, Integer npcId) {
        this.teamId = teamId;
        this.roleId = roleId;
        this.npcId = npcId;
    }

    public ToggleNpcHasRoleMsg(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        roleId = buf.readInt();
        npcId = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeInt(roleId);
        buf.writeInt(npcId);
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
            if(team.getRolesOf(npcId).contains(roleId))
                team.unassignRole(npcId, roleId);
            else
                team.assignRole(npcId, roleId);
        });
        return true;
    }
    
}
