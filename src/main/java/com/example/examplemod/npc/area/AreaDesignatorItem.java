package com.example.examplemod.npc.area;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.AABB;

public class AreaDesignatorItem extends Item {

    private static final Properties PROPERTIES = new Properties();

    private NpcArea area;

    public AreaDesignatorItem() {
        super(PROPERTIES);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if(player == null) return InteractionResult.FAIL;

        if(player.level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if(player.isShiftKeyDown()) {
        }

        EditingArea editingArea = EditingArea.get((ServerPlayer)player);
        if(editingArea == null) return InteractionResult.FAIL;

        BlockPos hit = context.getClickedPos();
        BlockPos plr = new BlockPos(player.position());
        AABB aabb = new AABB(hit, plr);

        editingArea.setAABB(aabb);
        

        return super.useOn(context);
    }
    
}
