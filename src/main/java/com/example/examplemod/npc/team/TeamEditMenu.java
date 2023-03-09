package com.example.examplemod.npc.team;

import com.example.examplemod.setup.Registration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class TeamEditMenu extends AbstractContainerMenu {

    public DataSlot teamIdSlot;

    // Server constructor
    public TeamEditMenu(int windowId, Integer team) {
        super(Registration.TEAM_EDIT_MENU.get(), windowId);

        this.teamIdSlot = this.addDataSlot(DataSlot.standalone());
        this.teamIdSlot.set(team);
    }

    // Client constructor
    public TeamEditMenu(int windowId) {
        super(Registration.TEAM_EDIT_MENU.get(), windowId);
        this.teamIdSlot = this.addDataSlot(DataSlot.standalone());
    }

    public int getTeamId() {
        return this.teamIdSlot.get();
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
