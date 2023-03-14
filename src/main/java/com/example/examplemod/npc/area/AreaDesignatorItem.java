package com.example.examplemod.npc.area;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AreaDesignatorItem extends Item {

    private static final Properties PROPERTIES = new Properties();

    private NpcArea area;

    public AreaDesignatorItem() {
        super(PROPERTIES);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        return super.useOn(context);
    }
    
}
