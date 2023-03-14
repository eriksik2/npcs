package com.example.examplemod.npc.area;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class EditingArea implements INBTSerializable<CompoundTag> {

    public void copyFrom(EditingArea source) {
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
    }
    
}
