package com.example.examplemod.npc.team;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.NpcTeamServerToClientBroker;
import com.example.examplemod.networking.SetNpcTeamData;
import com.example.examplemod.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CenteredStringWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TeamEditScreen extends AbstractContainerScreen<TeamEditMenu> {

    private CenteredStringWidget titleWidget;
    private EditBox nameInput;
    private Button saveButton;

    private NpcTeamServerToClientBroker teamBroker = Registration.NPC_TEAM_BROKER.get();
    private NpcTeam team;

    public TeamEditScreen(TeamEditMenu container, Inventory inv, Component title) {
        super(container, inv, title);

        this.font = Minecraft.getInstance().font;
        this.imageWidth = 176;
        this.imageHeight = 166;
        init();
    }

    @Override
    protected void init() {
        super.init();

        titleWidget = new CenteredStringWidget(Component.literal(""), this.font);
        titleWidget.setPosition(leftPos, topPos);
        titleWidget.setWidth(this.imageWidth);
        addRenderableWidget(titleWidget);


        nameInput = new EditBox(this.font, leftPos, topPos + 30, 100, 20, Component.nullToEmpty("Name"));
        addRenderableWidget(nameInput);

        this.saveButton = Button.builder(Component.literal("Save"), (button) -> {
            if(team == null) return;
            Messages.sendToServer(new SetNpcTeamData(team.getId(), nameInput.getValue()));
        }).build();
        saveButton.setPosition(leftPos, topPos + 60);
        saveButton.setWidth(100);
        addRenderableWidget(saveButton);
    }

    private void onNewTeam() {
        if(team == null) return;
        nameInput.setValue(team.getName());
        titleWidget.setMessage(Component.literal("Team Edit:" + team.getName()));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        NpcTeam newTeam = teamBroker.get(menu.getTeamId());
        if (newTeam != team) {
            team = newTeam;
            onNewTeam();
        }
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.renderFg(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    protected void renderFg(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        nameInput.render(stack, mouseX, mouseY, partialTicks);
        saveButton.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

    }
    
}
