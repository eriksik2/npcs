package com.example.examplemod.npc.area;

import com.example.examplemod.npc.team.NpcTeam;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;

public class NpcArea {

    private NpcTeam manager;
    private Integer id;

    private BlockPos corner1 = BlockPos.ZERO;
    private BlockPos corner2 = BlockPos.ZERO;

    private String name = "Unnamed area";
    private int color;

    public NpcArea(NpcTeam manager, int id) {
        this.manager = manager;
        this.id = id;
    }

    public NpcArea(CompoundTag tag, NpcTeam manager) {
        this.manager = manager;
        id = tag.getInt("id");
        corner1 = BlockPos.of(tag.getLong("corner1"));
        corner2 = BlockPos.of(tag.getLong("corner2"));
        name = tag.getString("name");
        color = tag.getInt("color");
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", id);
        tag.putLong("corner1", corner1.asLong());
        tag.putLong("corner2", corner2.asLong());
        tag.putString("name", name);
        tag.putInt("color", color);
        return tag;
    }

    public void setDirty() {
        if(manager == null) return;//throw new IllegalStateException("Cannot set dirty on an area that does not have a manager.");
        manager.setDirty();
    }

    public AABB toAABB() {
        return new AABB(corner1, corner2);
    }

    public void fromAAAB(AABB aabb) {
        corner1 = new BlockPos(aabb.minX, aabb.minY, aabb.minZ);
        corner2 = new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public Integer getId() {
        return id;
    }

    public BlockPos getCorner1() {
        return corner1;
    }

    public void setCorner1(BlockPos corner1) {
        this.corner1 = corner1;
        setDirty();
    }

    public BlockPos getCorner2() {
        return corner2;
    }

    public void setCorner2(BlockPos corner2) {
        this.corner2 = corner2;
        setDirty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setDirty();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setDirty();
    }
}
