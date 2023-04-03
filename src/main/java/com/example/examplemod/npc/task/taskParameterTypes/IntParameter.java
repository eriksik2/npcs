package com.example.examplemod.npc.task.taskParameterTypes;

import java.util.function.Consumer;

import com.example.examplemod.npc.task.TaskParameterType;
import com.example.examplemod.widgets.AbstractWidgetWrapper;
import com.example.examplemod.widgets.ModWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;


public class IntParameter extends TaskParameterType<Integer, Integer> {

    public IntParameter(String name, Integer defaultValue) {
        super(name, defaultValue, tag -> {
            return tag.getInt("value");
        }, value -> {
            CompoundTag tag = new CompoundTag();
            tag.putInt("value", value);
            return tag;
        });
    }

    @Override
    protected Integer convert(Integer value) {
        return value;
    }

    @Override
    protected ModWidget buildWidget(Integer value, Consumer<Integer> setter) {
        return new ModWidget(null) {

            private AbstractWidgetWrapper<EditBox> textBox;

            @Override
            public void onInit() {
                textBox = addChild(new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.nullToEmpty("Value")));
                textBox.getWrappedWidget().setValue(value == null ? "" : value.toString());
                textBox.getWrappedWidget().setResponder(text -> {
                    try {
                        setter.accept(Integer.parseInt(text));
                    } catch (NumberFormatException e) {
                        setter.accept(null);
                    }
                });
            }

            @Override
            public void onRelayoutPre() {
                layoutFillRemaining();
                textBox.setWidth(getInnerWidth());
                textBox.setHeight(20);
                layoutShrinkwrapChildren();
            }
        };
    }
    
}
