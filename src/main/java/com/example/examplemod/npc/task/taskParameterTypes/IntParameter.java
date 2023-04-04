package com.example.examplemod.npc.task.taskParameterTypes;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.example.examplemod.npc.task.TaskParameterType;
import com.example.examplemod.widgets.AbstractWidgetWrapper;
import com.example.examplemod.widgets.ModWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;


public class IntParameter extends TaskParameterType<String, Integer> {

    public IntParameter(String name, Integer defaultValue) {
        super(name, defaultValue.toString(), tag -> {
            return tag.getString("value");
        }, value -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("value", value);
            return tag;
        });
    }

    @Override
    protected Integer convert(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected ModWidget buildWidget(Supplier<String> getter, Consumer<String> setter) {
        return new ModWidget(null) {

            private AbstractWidgetWrapper<EditBox> textBox;

            @Override
            public void onInit() {
                textBox = addChild(new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.nullToEmpty("Value")));
                textBox.getWrappedWidget().setValue(getter.get() == null ? "" : getter.get());
                textBox.getWrappedWidget().setResponder(text -> {
                    setter.accept(text);
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
