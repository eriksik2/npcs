
package com.example.examplemod.npc.area;

import java.util.function.Supplier;

import com.example.examplemod.networking.Message;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class UpdateAreaPositionMsg implements Message {

    public static final String MESSAGE = "message.UpdateAreaPositionMsg";
    
    private final int teamId;
    private final int areaId;
    private final BlockPos corner1;
    private final BlockPos corner2;

    public UpdateAreaPositionMsg(int teamId, int areaId, BlockPos corner1, BlockPos corner2) {
        this.teamId = teamId;
        this.areaId = areaId;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public UpdateAreaPositionMsg(FriendlyByteBuf buf) {
        teamId = buf.readInt();
        areaId = buf.readInt();
        corner1 = buf.readBlockPos();
        corner2 = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(teamId);
        buf.writeInt(areaId);
        buf.writeBlockPos(corner1);
        buf.writeBlockPos(corner2);
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
            NpcArea area = team.getArea(areaId);
            if(area == null) {
                System.out.println("Could not find area with id " + areaId + " in team with id " + teamId);
                return;
            }
            area.setCorner1(corner1);
            area.setCorner2(corner2);
        });
        return true;
    }
    
}
