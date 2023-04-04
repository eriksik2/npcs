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

    private NpcTask task;
    private TaskParameterType<?, ?> parameter;

    public TaskParameterWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        nameWidget = new TextWidget(this, "");
        content = new ModWidget(this) {

            @Override
            public void onRelayoutPre() {
                for(ModWidget child : getChildren()) {
                    child.layoutFillX();
                }
            }

            @Override
            public void onRelayoutPost() {
                layoutShrinkwrapChildren();
            }
        };
        errorWidget = new TextWidget(this, "");
        errorWidget.setActive(false);
        errorWidget.setColor(0xFFFF0000);
        errorWidget.setWrap(true);
    }

    @Override
    public void onRelayoutPre() {
        layoutFillRemaining();
        content.setPosition(nameWidget.getWidth() + 4, 2);
        content.setWidth(getInnerWidth() - nameWidget.getWidth() - 4);
        errorWidget.setWidth(getInnerWidth());
        errorWidget.setY(content.getY() + content.getHeight() + 2);
    }
    
    @Override
    public void onRelayoutPost() {
        nameWidget.setY(content.getY() + (content.getHeight() - nameWidget.getHeight()) / 2);
        //errorWidget.setY(getInnerHeight());
        layoutShrinkwrapChildren();
    }

    public <TValue, TOutput> void setParameter(NpcTask task, TaskParameterType<TValue, TOutput> parameter) {
        TValue value = task.getValue(parameter);
        if(this.currentValue != null && this.currentValue.equals(value)) return;
        this.currentValue = value;
        if(this.inputValue == null) this.inputValue = value;

        boolean isSame = this.task != null
            && this.task.getId() == task.getId()
            && this.task.getManager().getId() == task.getManager().getId()
            && this.task.getManager().getManager().getId() == task.getManager().getManager().getId()
            && this.parameter != null
            && this.parameter.getSlot() == parameter.getSlot();
        this.task = task;
        this.parameter = parameter;

        if(valueWidget == null || !isSame) {
            content.clearChildren();
            nameWidget.setText(parameter.getName() + ":");
            valueWidget = parameter.buildWidget(() -> (TValue)this.currentValue, (newValue) -> {
                this.inputValue = newValue;
                String error = parameter.validate(newValue);
                if(error != null) {
                    errorWidget.setText(error);
                    errorWidget.setActive(true);
                    return;
                } else {
                    errorWidget.setActive(false);
                    errorWidget.setText("");
                }
                if(newValue != null && newValue.equals(this.currentValue)) return;
                Messages.sendToServer(new SetTaskParameterValueMsg(task, parameter.getSlot(), newValue));
            });
            valueWidget.init();
            content.addChild(valueWidget);
        }
        setLayoutDirty();
    }
}
