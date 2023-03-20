package com.example.examplemod.npc.area;

import net.minecraft.world.item.Item;

public class AreaDesignatorItem extends Item {

    private static final Properties PROPERTIES = new Properties();
    

    
    public AreaDesignatorItem() {
        super(PROPERTIES);
    }

    /*@Override
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
    */
}
