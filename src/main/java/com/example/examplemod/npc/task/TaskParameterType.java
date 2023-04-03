package com.example.examplemod.npc.task;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import org.checkerframework.common.returnsreceiver.qual.This;

import com.example.examplemod.widgets.ModWidget;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class TaskParameterType<TValue, TOutput> {
    
    private int slot = -1;
    private final String name;
    private final TValue defaultValue;
    private final Function<CompoundTag, TValue> deserializer;
    private final Function<TValue, CompoundTag> serializer;
    private final ArrayList<TaskParameterValidator<TValue>> validators = new ArrayList<>();

    public TaskParameterType(String name, TValue defaultValue, Function<CompoundTag, TValue> deserializer, Function<TValue, CompoundTag> serializer) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    protected abstract TOutput convert(TValue value);
    protected abstract ModWidget buildWidget(TValue value, Consumer<TValue> setter);

    public <T extends TaskParameterType<TValue, TOutput>> T withValidator(String message, Function<TValue, Boolean> validator) {
        validators.add(new TaskParameterValidator<TValue>(message, validator));
        return (T)this;
    }

    public String getName() {
        return name;
    }

    public TValue getDefaultValue() {
        return defaultValue;
    }

    public CompoundTag serialize(TValue value) {
        return serializer.apply(value);
    }

    public CompoundTag serializeUnsafe(Object value) {
        return serializer.apply((TValue)value);
    }

    public TValue deserialize(CompoundTag tag) {
        return deserializer.apply(tag);
    }

    public String validate(TValue value) {
        for(TaskParameterValidator<TValue> validator : validators) {
            if(!validator.validate(value)) return validator.getMessage();
        }
        return null;
    }

    public final TOutput apply(TValue value) {
        if (value == null) return convert(defaultValue);
        return convert(value);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
