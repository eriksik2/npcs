package com.example.examplemod.networking;

import java.util.function.Supplier;

import com.example.examplemod.encyclopedia.EncyclopediaMenu;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.team.TeamEditMenu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class OpenEditTeam implements Message {
    public static final String MESSAGE = "message.OpenEditTeam";
    
    private Integer teamId = null;

    public OpenEditTeam() {
    }

    public OpenEditTeam(Integer teamId) {
        this.teamId = teamId;
    }

    public OpenEditTeam(FriendlyByteBuf buf) {
        boolean hasTeamId = buf.readBoolean();
        if (hasTeamId) {
            teamId = buf.readInt();
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(teamId != null);
        if (teamId != null) {
            buf.writeInt(teamId);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        Integer teamId = this.teamId;
        ctx.enqueueWork(() -> {
            // Here we are Server side.
            NetworkHooks.openScreen(ctx.getSender(), new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                    Integer teamId1 = teamId;
                    if (teamId1 == null) {
                        NpcManager npcManager = NpcManager.get(player.level);
                        teamId1 = npcManager.getPlayerTeam(player).getId();
                    }
                    return new TeamEditMenu(windowId, teamId1);
                }

                @Override
                public Component getDisplayName() {
                    return Component.literal("Edit Team");
                }
            });
        });
        return true;
    }
}
