package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcTeam;
import com.example.examplemod.npc.NpcManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class AddNpcToPlayerTeam implements Message {
    public static final String MESSAGE = "message.PacketAddNpcToTeam";

    private int npcId;

    public AddNpcToPlayerTeam(int npcId) {
        this.npcId = npcId;
    }

    public AddNpcToPlayerTeam(FriendlyByteBuf buf) {
        this.npcId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(npcId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Server side
            ServerPlayer sender = ctx.getSender();
            Level level = sender.level;
            NpcManager manager = NpcManager.get(level);
            NpcTeam team = manager.getPlayerTeam(sender);
            NpcData data = manager.getNpcData(npcId);
            manager.addNpcToTeam(data, team);
        });
        return true;
    }
}