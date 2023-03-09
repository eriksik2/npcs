package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SetNpcTeamData implements Message {
    public static final String MESSAGE = "message.SetNpcTeamData";
    
    private final int teamId;
    private final String newName;

    public SetNpcTeamData(int teamId, String newName) {
        this.teamId = teamId;
        this.newName = newName;
    }

    public SetNpcTeamData(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        boolean hasName = buf.readBoolean();
        if(hasName) newName = buf.readUtf();
        else newName = null;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeBoolean(newName != null);
        if(newName != null) buf.writeUtf(newName);
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

            if(newName != null) team.setName(newName);

        });
        return true;
    }
}
