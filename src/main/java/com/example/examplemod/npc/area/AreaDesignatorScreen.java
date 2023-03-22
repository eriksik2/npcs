package com.example.examplemod.npc.area;

import java.util.List;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.NpcTeamServerToClientBroker;
import com.example.examplemod.networking.subscribe.ServerSubscription;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.npc.team.TeamSubscriptionBroker;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.widgets.ButtonWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.ModWidgetScreen;
import com.example.examplemod.widgets.PopupManagerWidget;
import com.example.examplemod.widgets.ScrollableListWidget;
import com.example.examplemod.widgets.TextWidget;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class AreaDesignatorScreen extends ModWidgetScreen {

    private final TeamSubscriptionBroker teamBroker = Registration.TEAM_SUBSCRIPTION_BROKER.get();

    private ServerSubscription<NpcTeam> teamSubscription;

    private final Player player;
    private Integer teamId = 0;
    private Integer selectedAreaId;

    private PopupManagerWidget popupManager;
    private ModWidget pane;
    private ScrollableListWidget areasList;
    private ButtonWidget addAreaButton;

    protected AreaDesignatorScreen(Player player) {
        super(Component.literal("Area Designator"));
        this.player = player;
    }

    @Override
    public void registerWidgets(ModWidget root) {
        popupManager = new PopupManagerWidget(root);
        popupManager.layoutFillX();
        popupManager.layoutFillY();

        pane = new ModWidget(popupManager);
        pane.setWidth(100);

        TextWidget text = new TextWidget(pane, "Area Designator");
        text.layoutCenterX();

        areasList = new ScrollableListWidget(pane);
        areasList.setY(text.getY() + text.getHeight());
        areasList.setWidth(pane.getInnerWidth());
        areasList.setHeight(100);

        addAreaButton = new ButtonWidget(pane, "Add Area");
        addAreaButton.setY(areasList.getY() + areasList.getHeight());
        addAreaButton.setOnClick(() -> {
            AreaEditorWidget editor = new AreaEditorWidget(null);
            editor.setSubmitText("Add Area");
            editor.setOnSubmit((name) -> {
                if(teamId == null) return;
                Messages.sendToServer(new AddNpcAreaMsg(teamId, name));
            });
            popupManager.push(editor);
        });
        addAreaButton.setWidth(pane.getInnerWidth()/2 - 2);

        pane.layoutShrinkwrapChildren();
        pane.layoutCenterX();
        pane.layoutCenterY();
    }

    @Override
    protected void onDeinit() {
        if(teamSubscription != null) {
            teamSubscription.deinit();
            teamSubscription = null;
        }
    }

    public void onInit() {
        if(teamSubscription != null) {
            teamSubscription.deinit();
            teamSubscription = null;
        }
        teamSubscription = teamBroker.subscribe(teamId);
        teamSubscription.addListener(this::onTeamUpdate);
    }

    private void onTeamUpdate(NpcTeam team) {
        System.out.println("Team update: " + team);

        areasList.clearChildren();
        for(NpcArea area : team.getAreas()) {
            TextWidget areaWidget = new TextWidget(areasList, area.getName()) {
                @Override
                public boolean onMousePressed(double mouseX, double mouseY, int button) {
                    if(!isMouseOver(mouseX, mouseY)) return false;
                    if(button != 0) return false;
                    if(teamId == null) return false;
                    selectedAreaId = area.getId();
                    ClientEditingArea.setEditingArea(teamId, area.getId());
                    return true;
                }

                @Override
                public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
                    if(area.getId().equals(selectedAreaId)) {
                        fill(stack, 0, 0, getWidth(), getHeight(), 0x80FFFFFF);
                    }
                    super.onRender(stack, mouseX, mouseY, partialTicks);
                }
            };
            areaWidget.setWidth(areasList.getInnerWidth());
        }
    }
    
}
