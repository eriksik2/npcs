package com.example.examplemod.npc.area;

import com.example.examplemod.networking.Messages;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;

public class EditingArea implements INBTSerializable<CompoundTag> {

    private ServerPlayer player = null;
    private NpcArea area = new NpcArea(null, 0);


    public static EditingArea get(ServerPlayer player) {
        EditingArea editingArea = player.getCapability(EditingAreaProvider.EDITING_AREA).orElse(null);
        if(editingArea == null) {
            return null;
        }
        editingArea.player = player;
        return editingArea;
    }

    public void copyFrom(EditingArea source) {
        area = source.area;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("isEditingArea", area != null);
        if(area != null) tag.put("area", area.toCompoundTag());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.getBoolean("isEditingArea")) area = new NpcArea(nbt.getCompound("area"), null);
        else area = null;
    }

    public NpcArea getArea() {
        return area;
    }

    public void setAABB(AABB aabb) {
        area.setCorner1(new BlockPos(aabb.minX, aabb.minY, aabb.minZ));
        area.setCorner2(new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ));
        setDirty();
    }

    private void setDirty() {
        if(player == null) return;
        Messages.sendToPlayer(new SyncEditingAreaToClient(area), player);
    }
    
}
