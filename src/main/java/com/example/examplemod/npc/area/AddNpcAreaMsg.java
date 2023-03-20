package com.example.examplemod.npc.area;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class AddNpcAreaMsg implements Message {

    public static final String MESSAGE = "message.AddNpcAreaMsg";
    
    private final int teamId;
    private final String name;

    public AddNpcAreaMsg(int teamId, String name) {
        this.teamId = teamId;
        this.name = name;
    }

    public AddNpcAreaMsg(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        name = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeUtf(name);
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
            NpcArea area = team.addArea();
            area.setName(name);
            area.setCorner1(player.getOnPos());
            area.setCorner2(player.getOnPos().above(2).east(2).north(2));
        });
        return true;
    }
    
}
