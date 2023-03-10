package com.example.examplemod.npc.team;

import com.example.examplemod.networking.AddRoleToTeam;
import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.NpcTeamServerToClientBroker;
import com.example.examplemod.networking.SetNpcTeamData;
import com.example.examplemod.npc.role.NpcRole;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.PopupManagerWidget;
import com.example.examplemod.widgets.RoleWidget;
import com.example.examplemod.widgets.ScrollableListWidget;
import com.example.examplemod.widgets.TabsWidget;
import com.example.examplemod.widgets.TextWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import ca.weblite.objc.Message;
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
    private TabsWidget tabs;
    private ScrollableListWidget rolesList;
    private PopupManagerWidget popupManager;
    private boolean showDebug = false;

    private ModWidget debug;

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

        popupManager = new PopupManagerWidget(null);
        tabs = new TabsWidget(popupManager);
        tabs.setWidth(width);
        tabs.setHeight(height);
        tabs.addTab(Component.literal("Team"), new ModWidget(tabs) {
            public void onInit() {
                this.layoutFillRemaining();
                this.setPadding(10);

                titleWidget = new CenteredStringWidget(Component.literal(""), font);
                titleWidget.setPosition(0, 0);
                titleWidget.setWidth(getInnerWidth());
                titleWidget.setHeight(20);
                addChild(titleWidget);

                TextWidget nameText = new TextWidget(this, "Name");
                nameText.setPosition(0, 30);
                nameText.setWidth(getInnerWidth()/2);

                nameInput = new EditBox(font, 0, 0, 0, 0, Component.nullToEmpty("Name"));
                nameInput.setPosition(getInnerWidth()/2, 30);
                nameInput.setWidth(getInnerWidth()/2);
                nameInput.setHeight(20);
                addChild(nameInput);

                saveButton = Button.builder(Component.literal("Save"), (button) -> {
                    if(team == null) return;
                    Messages.sendToServer(new SetNpcTeamData(team.getId(), nameInput.getValue()));
                }).build();
                saveButton.setPosition(0, 60);
                saveButton.setWidth(getInnerWidth()/3);
                saveButton.setHeight(20);
                addChild(saveButton).layoutCenterX();
            }
        });
        tabs.addTab(Component.literal("Roles"), new ModWidget(tabs) {
            public void onInit() {
                this.layoutFillRemaining();
                rolesList = new ScrollableListWidget(this);
                rolesList.layoutFillRemaining();
                rolesList.setGap(5);
                rolesList.setHeight(rolesList.getHeight() - 20);

                Button bb = Button.builder(Component.literal("Add role"), (button) -> {
                    if(team == null) return;
                    Messages.sendToServer(new AddRoleToTeam(team.getId(), "New role", "New role"));
                }).build();
                ModWidget addRoleButton = this.addChild(bb);

                addRoleButton.setWidth(100);
                addRoleButton.layoutCenterX();
                addRoleButton.setHeight(20);
                addRoleButton.setY(this.getInnerHeight() - 20);
            }
        });
        popupManager.init();
    }

    private void onNewTeam() {
        if(team == null) return;
        if(rolesList != null) {
            rolesList.clearChildren();
            for(NpcRole role : team.getRoles()) {
                new RoleWidget(rolesList, popupManager, role);
            }
        }
        if(nameInput != null) nameInput.setValue(team.getName());
        if(titleWidget != null) titleWidget.setMessage(Component.literal("Editing team: " + team.getName()));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        NpcTeam newTeam = teamBroker.get(menu.getTeamId());
        int hc1 = team == null ? 0 : team.hashCode();
        int hc2 = newTeam == null ? 0 : newTeam.hashCode();
        if (hc1 != hc2) {
            team = newTeam;
            onNewTeam();
        }

        this.renderBackground(stack);
        popupManager.render(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
        if(debug != null) debug.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.renderFg(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 1) {
            showDebug = !showDebug;
            if(showDebug) {
                debug = popupManager.debugWidget();
                debug.setSize(width/2, height);
                popupManager.setPosition(width/2, 0);
            } else {
                debug = null;
                popupManager.setPosition(0, 0);
                popupManager.setSize(width, height);
            }
            return true;
        }
        if(debug != null && debug.mousePressed(mouseX, mouseY, button)) return true;
        if(popupManager.mousePressed(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(debug != null) debug.mouseScrolled(mouseX, mouseY, amount);
        popupManager.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    protected void renderFg(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        //nameInput.render(stack, mouseX, mouseY, partialTicks);
        //saveButton.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

    }
    
}
