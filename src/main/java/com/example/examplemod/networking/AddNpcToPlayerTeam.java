package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcTeam;
import com.example.examplemod.npc.NpcManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class AddNpcToPlayerTeam implements Message {
    public static final String MESSAGE = "message.PacketAddNpcToTeam";

    private int npcEntityId;

    public AddNpcToPlayerTeam(int npcEntityId) {
        this.npcEntityId = npcEntityId;
    }

    public AddNpcToPlayerTeam(FriendlyByteBuf buf) {
        this.npcEntityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(npcEntityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Server side
            ServerPlayer sender = ctx.getSender();
            Level level = sender.level;
            NpcEntity npc = (NpcEntity)level.getEntity(npcEntityId);
            if(npc == null) {
                throw new RuntimeException("Cant find entity.");
            }
            NpcTeam team = NpcManager.get(level).getPlayerTeam(sender);
            npc.addToTeam(team);
        });
        return true;
    }
}