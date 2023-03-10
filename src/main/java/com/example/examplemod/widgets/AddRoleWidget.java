package com.example.examplemod.widgets;

import com.example.examplemod.networking.AddRoleToTeam;
import com.example.examplemod.networking.Messages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class AddRoleWidget extends ModWidget {

    private TextWidget nameText;
    private TextWidget descriptionText;
    private AbstractWidgetWrapper<EditBox> nameBox;
    private AbstractWidgetWrapper<EditBox> descriptionBox;
    private AbstractWidgetWrapper<Button> addButton;
    private AbstractWidgetWrapper<Button> closeButton;
    private Font font;

    private Integer teamId;

    public AddRoleWidget(ModWidget parent, Integer teamId) {
        super(parent);
        this.font = Minecraft.getInstance().font;
        this.teamId = teamId;
    }

    @Override
    public void onInit() {
        nameText = new TextWidget(this, "Name");
        descriptionText = new TextWidget(this, "Description");

        nameBox = addChild(new EditBox(font, 0, 0, 100, 20, Component.literal("Name")));
        

        descriptionBox = addChild(new EditBox(font, 0, 0, 100, 20, Component.literal("Description")));

        addButton = addChild(Button.builder(Component.literal("Create"), (button) -> {
            Messages.sendToServer(new AddRoleToTeam(teamId, nameBox.get().getValue(), descriptionBox.get().getValue()));
            deinit();
        }).build());

        closeButton = addChild(Button.builder(Component.literal("Close"), (button) -> {
            deinit();
        }).build());
    }

    @Override
    public void onRelayoutPost() {
        setSize(200, 60);

        nameText.setPosition(0, 0);
        nameText.setWidth(getInnerWidth()/2);
        nameBox.setPosition(getInnerWidth()/2, 0);
        nameBox.setWidth(getInnerWidth()/2);
        nameBox.setHeight(20);

        descriptionText.setPosition(0, 30);
        descriptionText.setWidth(getInnerWidth()/2);
        descriptionBox.setPosition(getInnerWidth()/2, 30);
        descriptionBox.setWidth(getInnerWidth()/2);
        descriptionBox.setHeight(20);

        addButton.setPosition(2*getInnerWidth()/3, 60);
        addButton.setWidth(getInnerWidth()/3);

        closeButton.setPosition(0, 60);
        closeButton.setWidth(getInnerWidth()/3);

        layoutShrinkwrapChildren();
    }
    
}
