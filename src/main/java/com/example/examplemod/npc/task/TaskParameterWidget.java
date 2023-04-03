package com.example.examplemod.npc.task;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.TextWidget;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class TaskParameterWidget extends ModWidget {

    private TextWidget nameWidget;
    private ModWidget content;
    private ModWidget valueWidget;
    private TextWidget errorWidget;

    private Object currentValue;
    private Object inputValue;

    public TaskParameterWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        nameWidget = new TextWidget(this, "");
        content = new ModWidget(this) {
            @Override
            public void onRelayoutPost() {
                layoutShrinkwrapChildren();
            }
        };
        errorWidget = new TextWidget(this, "");
        errorWidget.setActive(false);
        errorWidget.setColor(0xFFFF0000);
    }

    @Override
    public void onRelayoutPre() {
        layoutFillRemaining();
        nameWidget.setPosition(0, 0);
        content.setPosition(nameWidget.getWidth() + 4, 0);
        content.setWidth(getInnerWidth() - nameWidget.getWidth() - 4);
    }
    
    @Override
    public void onRelayoutPost() {
        layoutShrinkwrapChildren();
        nameWidget.layoutCenterY();
    }

    public <TValue, TOutput> void setParameter(NpcTask task, TaskParameterType<TValue, TOutput> parameter) {
        TValue value = task.getValue(parameter);
        if(this.currentValue != null && this.currentValue.equals(value)) return;
        this.currentValue = value;
        if(this.inputValue == null) this.inputValue = value;
        content.clearChildren();
        nameWidget.setText(parameter.getName() + ":");
        valueWidget = parameter.buildWidget(value, (newValue) -> {
            if(value != null && value.equals(newValue)) return;
            if(newValue == null) {
                return;
            }
            this.inputValue = newValue;
            String error = parameter.validate(newValue);
            if(error != null) {
                errorWidget.setText(error);
                errorWidget.setActive(true);
                return;
            } else {
                errorWidget.setActive(false);
            }
            Messages.sendToServer(new SetTaskParameterValueMsg(task, parameter.getSlot(), newValue));
        });
        content.addChild(valueWidget);
        setLayoutDirty();
    }
}
