package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.encyclopedia.EncyclopediaMenu;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcManager;
import com.mojang.logging.LogUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class GetNpcData implements Message {
    public static final String MESSAGE = "message.GetNpcData";
    
    private Integer npcId = null;

    public GetNpcData() {
    }

    public GetNpcData(int npcId) {
        this.npcId = npcId;
    }

    public GetNpcData(FriendlyByteBuf buf) {
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
            NpcData data = manager.getNpcData(npcId);
            if(data == null) {
                return;
            }
            SyncNpcDataToClient message = new SyncNpcDataToClient(data);
            Messages.sendToPlayer(message, player);
        });
        return true;
    }

}
