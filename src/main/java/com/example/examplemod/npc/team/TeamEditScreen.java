package com.example.examplemod.npc.team;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.NpcTeamServerToClientBroker;
import com.example.examplemod.networking.SetNpcTeamData;
import com.example.examplemod.networking.subscribe.ServerSubscription;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.PopupManagerWidget;
import com.example.examplemod.widgets.RolesExplorerWidget;
import com.example.examplemod.widgets.ScrollableListWidget;
import com.example.examplemod.widgets.TabsWidget;
import com.example.examplemod.widgets.TextWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
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
    private RolesExplorerWidget rolesExplorer;
    private ScrollableListWidget areasList;
    private PopupManagerWidget popupManager;
    private boolean showDebug = false;

    private ModWidget debug;

    private ServerSubscription<NpcTeam> teamSubscription;
    private NpcTeam team;

    public TeamEditScreen(TeamEditMenu container, Inventory inv, Component title) {
        super(container, inv, title);

        teamSubscription = Registration.TEAM_SUBSCRIPTION_BROKER.get().subscribe(container.getTeamId());
        teamSubscription.addListener(this::onNewTeam);

        this.font = Minecraft.getInstance().font;
        this.imageWidth = 176;
        this.imageHeight = 166;
        init();
    }

    @Override
    public void onClose() {
        teamSubscription.deinit();
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();

        popupManager = new PopupManagerWidget(null);
        popupManager.setSize(width, height);
        tabs = new TabsWidget(popupManager);
        tabs.setWidth(width);
        tabs.setHeight(height);
        tabs.addTab(Component.literal("Settings"), new ModWidget(tabs) {
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
        rolesExplorer = tabs.addTab(Component.literal("Roles"), new RolesExplorerWidget(tabs, menu.getTeamId(), popupManager));
        tabs.addTab(Component.literal("Areas"), new ModWidget(tabs) {
            public void onInit() {
                this.layoutFillRemaining();
                areasList = new ScrollableListWidget(this);
                areasList.layoutFillRemaining();
                areasList.setGap(5);
                areasList.setHeight(areasList.getHeight() - 40);

                Button bb = Button.builder(Component.literal("Add new area"), (button) -> {
                    if(team == null) return;
                    System.out.println("Add new area");
                }).build();
                ModWidget addAreaButton = this.addChild(bb);

                addAreaButton.setWidth(100);
                addAreaButton.layoutCenterX();
                addAreaButton.setHeight(20);
                addAreaButton.setY(areasList.getHeight());
            }
        });
        popupManager.init();
    }

    private void onNewTeam(NpcTeam newTeam) {
        team = newTeam;
        if(team == null) return;
        if(rolesExplorer != null) rolesExplorer.setRolesList(team.getRoles());
        if(nameInput != null) nameInput.setValue(team.getName());
        if(titleWidget != null) titleWidget.setMessage(Component.literal("Editing team: " + team.getName()));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(popupManager != null) popupManager.tick();
        if(debug != null) debug.tick();

        this.renderBackground(stack);
        popupManager.render(stack, mouseX, mouseY, partialTicks);
        if(debug != null) debug.render(stack, mouseX, mouseY, partialTicks);
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
        return popupManager.mousePressed(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(debug != null) debug.mouseScrolled(mouseX, mouseY, amount);
        return popupManager.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(debug != null) debug.keyPressed(keyCode, scanCode, modifiers);
        if(popupManager.keyPressed(keyCode, scanCode, modifiers)) return true;
        if(keyCode == 256) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(debug != null) debug.keyReleased(keyCode, scanCode, modifiers);
        return popupManager.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(debug != null) debug.charTyped(codePoint, modifiers);
        return popupManager.charTyped(codePoint, modifiers);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

    }
    
}
