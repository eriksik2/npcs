package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.NpcTeam;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class GetTeamData implements Message {
    public static final String MESSAGE = "message.GetTeamData";
    
    private Integer npcId = null;

    public GetTeamData() {
    }

    public GetTeamData(int npcId) {
        this.npcId = npcId;
    }

    public GetTeamData(FriendlyByteBuf buf) {
        npcId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(npcId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        Integer npcId = this.npcId;
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            ServerPlayer player = ctx.getSender();
            NpcManager manager = NpcManager.get(player.level);
            NpcTeam data = manager.getTeam(npcId);
            if(data == null) {
                return;
            }
            SyncTeamDataToClient message = new SyncTeamDataToClient(data);
            Messages.sendToPlayer(message, player);
        });
        return true;
    }
}
