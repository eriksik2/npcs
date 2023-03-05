package com.example.examplemod.networking;

import java.util.UUID;
import java.util.function.Supplier;

import com.example.examplemod.encyclopedia.EncyclopediaMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class OpenEncyclopedia implements Message {
    public static final String MESSAGE = "message.OpenEncyclopedia";
    
    private Integer npcId = null;

    public OpenEncyclopedia() {
    }

    public OpenEncyclopedia(Integer npcId) {
        this.npcId = npcId;
    }

    public OpenEncyclopedia(FriendlyByteBuf buf) {
        boolean hasNpcId = buf.readBoolean();
        if (hasNpcId) {
            npcId = buf.readInt();
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(npcId != null);
        if (npcId != null) {
            buf.writeInt(npcId);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        Integer npcId = this.npcId;
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            NetworkHooks.openScreen(ctx.getSender(), new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                    return new EncyclopediaMenu(windowId, player, npcId);
                }

                @Override
                public Component getDisplayName() {
                    return Component.literal("Encyclopedia");
                }
            });
        });
        return true;
    }
}
