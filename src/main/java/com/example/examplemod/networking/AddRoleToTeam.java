package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class AddRoleToTeam implements Message {
    public static final String MESSAGE = "message.AddRoleToTeam";
    
    private final int teamId;
    private final String name;
    private final String description;

    public AddRoleToTeam(int teamId, String name, String description) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
    }

    public AddRoleToTeam(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        name = buf.readUtf();
        description = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeUtf(name);
        buf.writeUtf(description);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
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
            team.addRole(name, description);
        });
        return true;
    }
}
