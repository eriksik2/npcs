package com.example.examplemod.npc.area;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.example.examplemod.widgets.AbstractWidgetWrapper;
import com.example.examplemod.widgets.ButtonWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.TextWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class AreaEditorWidget extends ModWidget {

    private TextWidget nameLabel;
    private AbstractWidgetWrapper<EditBox> nameEditBox;
    private ButtonWidget submitButton;
    private ButtonWidget cancelButton;

    private Consumer<String> onSubmit;
    private String submitText = "Add area";

    private final Font font = Minecraft.getInstance().font;

    public AreaEditorWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        setWidth(100);
        setHeight(150);
        nameLabel = new TextWidget(this, "Name");
        nameEditBox = addChild(new EditBox(font, 0, 0, 0, 0, Component.literal("New Area")));

        submitButton = new ButtonWidget(this, submitText);
        submitButton.setOnClick(() -> {
            if (onSubmit != null) {
                onSubmit.accept(nameEditBox.get().getValue());
            }
            deinit();
        });

        cancelButton = new ButtonWidget(this, "Cancel");
        cancelButton.setOnClick(() -> {
            deinit();
        });
    }

    @Override
    public void onRelayoutPre() {
        setWidth(200);
        setHeight(150);
        nameLabel.setWidth(getInnerWidth()/2 - 2);
        nameEditBox.setWidth(getInnerWidth()/2 - 2);
        nameEditBox.setHeight(20);
        nameEditBox.setX(getInnerWidth()/2 + 2);


        submitButton.setY(nameEditBox.getY() + nameEditBox.getHeight() + 2);
        submitButton.setWidth(getInnerWidth()/2 - 2);
        cancelButton.setY(nameEditBox.getY() + nameEditBox.getHeight() + 2);
        cancelButton.setX(getInnerWidth()/2 + 2);
        cancelButton.setWidth(getInnerWidth()/2 - 2);

        submitButton.setText(submitText);

        layoutShrinkwrapChildren();
    }
    
    public void setOnSubmit(Consumer<String> onSubmit) {
        this.onSubmit = onSubmit;
        
    }

    public void setSubmitText(String submitText) {
        this.submitText = submitText;
        setLayoutDirty();
    }
}
