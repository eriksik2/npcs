package com.example.examplemod.npc;

import com.example.examplemod.networking.NpcDataServerToClientBroker;
import com.example.examplemod.setup.Registration;

import ca.weblite.objc.Client;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class NpcInteractMenu extends AbstractContainerMenu {

    public Player player;
    private IItemHandler playerInventory;

    public DataSlot nidSlot;
    public DataSlot eidSlot;
    private NpcDataServerToClientBroker npcDataBroker = Registration.NPC_DATA_BROKER.get();

    // Server constructor
    public NpcInteractMenu(int windowId, BlockPos pos, Inventory playerInventory, Player player, NpcEntity npcEntity) {
        super(Registration.NPC_MENU.get(), windowId);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);

        this.eidSlot = this.addDataSlot(DataSlot.standalone());
        this.eidSlot.set(npcEntity.getId());
        this.nidSlot = this.addDataSlot(DataSlot.standalone());
        this.nidSlot.set(npcEntity.npcId);

        //layoutPlayerInventorySlots(10, 70);
    }

    // Client constructor
    public NpcInteractMenu(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
        super(Registration.NPC_MENU.get(), windowId);
        this.player = player;
        this.playerInventory = new InvWrapper(playerInventory);

        this.eidSlot = this.addDataSlot(DataSlot.standalone());
        this.nidSlot = this.addDataSlot(DataSlot.standalone());
        
        //layoutPlayerInventorySlots(10, 70);
    }


    public NpcEntity getEntity() {
        return (NpcEntity)player.level.getEntity(eidSlot.get());
    }

    public int getEntityId(){
        return eidSlot.get();
    }

    public NpcData getNpcData() {
        if(!player.level.isClientSide) throw new RuntimeException("Cannot get client data on server");
        return npcDataBroker.get(nidSlot.get());
    }

    public int getNpcId(){
        return nidSlot.get();
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.moveItemStackTo(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.moveItemStackTo(stack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}