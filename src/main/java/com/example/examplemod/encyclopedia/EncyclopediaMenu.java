package com.example.examplemod.encyclopedia;

import java.util.UUID;

import com.example.examplemod.setup.Registration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class EncyclopediaMenu extends AbstractContainerMenu {

    private Player player;
    public DataSlot npcIdSlot;

    // Server constructor
    public EncyclopediaMenu(int windowId, Player player, Integer npcId) {
        super(Registration.ENCYCLOPEDIA_MENU.get(), windowId);
        this.player = player;

        this.npcIdSlot = this.addDataSlot(DataSlot.standalone());
        this.npcIdSlot.set(npcId == null ? -1 : npcId);
    }

    // Client constructor
    public EncyclopediaMenu(int windowId, Player player) {
        super(Registration.ENCYCLOPEDIA_MENU.get(), windowId);
        this.player = player;

        this.npcIdSlot = this.addDataSlot(DataSlot.standalone());
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        throw new UnsupportedOperationException("Unimplemented method 'quickMoveStack'");
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
    
}
