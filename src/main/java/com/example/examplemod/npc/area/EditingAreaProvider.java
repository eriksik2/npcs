package com.example.examplemod.npc.area;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class EditingAreaProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    
    public static Capability<EditingArea> EDITING_AREA = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<EditingArea> opt = LazyOptional.of(this::getEditingArea);

    private EditingArea editingArea = null;

    @Nonnull
    private EditingArea getEditingArea() {
        if (editingArea == null) {
            editingArea = new EditingArea();
        }
        return editingArea;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("trackedObjects", getEditingArea().serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        getEditingArea().deserializeNBT(tag.getCompound("trackedObjects"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == EDITING_AREA) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }
}
