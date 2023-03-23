package com.example.examplemod.npc.role;

import java.util.List;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.subscribe.ServerSubscription;
import com.example.examplemod.npc.area.AreaPreviewWidget;
import com.example.examplemod.npc.area.NpcArea;
import com.example.examplemod.npc.area.ToggleRoleHasAreaMsg;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.npc.team.TeamSubscriptionBroker;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.widgets.ButtonWidget;
import com.example.examplemod.widgets.ColumnLayoutWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.TextWidget;
import com.mojang.blaze3d.vertex.PoseStack;

public class RoleAreasEditorWidget extends ModWidget {

    private TeamSubscriptionBroker teamSubscriptionBroker = Registration.TEAM_SUBSCRIPTION_BROKER.get();
    private ServerSubscription<NpcTeam> teamSubscription;
    private Integer teamId;
    private Integer roleId;

    private TextWidget allowedAreasLabel;
    private TextWidget disallowedAreasLabel;
    private ColumnLayoutWidget roleAreas;
    private ColumnLayoutWidget otherAreas;

    public RoleAreasEditorWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        roleAreas = new ColumnLayoutWidget(this);
        roleAreas.setGap(2);
        otherAreas = new ColumnLayoutWidget(this);
        otherAreas.setGap(2);

        allowedAreasLabel = new TextWidget(this, "Allowed areas");
        disallowedAreasLabel = new TextWidget(this, "Disallowed areas");
    }

    @Override
    public void onDeinit() {
        if(teamSubscription != null) {
            teamSubscription.unsubscribe();
            teamSubscription = null;
        }
    }

    @Override
    public void onRelayoutPost() {
        disallowedAreasLabel.setX(getInnerWidth() / 2 + 2);
        roleAreas.setY(allowedAreasLabel.getHeight() + 2);
        otherAreas.setY(disallowedAreasLabel.getHeight() + 2);
        roleAreas.setWidth(getInnerWidth() / 2 - 2);
        otherAreas.setX(getInnerWidth() / 2 + 2);
        otherAreas.setWidth(getInnerWidth() / 2 - 2);
        setInnerHeight(Math.max(roleAreas.getHeight(), otherAreas.getHeight()) + disallowedAreasLabel.getHeight());
    }

    public void setRole(Integer teamId, Integer roleId) {
        this.teamId = teamId;
        this.roleId = roleId;
        if(teamSubscription != null) {
            teamSubscription.unsubscribe();
            teamSubscription = null;
        }
        teamSubscription = teamSubscriptionBroker.subscribe(teamId, this::onTeamUpdate);
    }

    private void onTeamUpdate(NpcTeam team) {
        List<NpcArea> areas = team.getAreas();
        List<Integer> roleAreaIds = team.getAreasOf(roleId);
        roleAreas.clearChildren();
        otherAreas.clearChildren();
        for(NpcArea area : areas) {
            AreaPreviewWidget areaPreview = new AreaPreviewWidget(null);
            areaPreview.init();
            areaPreview.setArea(teamId, area.getId());
            boolean hasArea = roleAreaIds.stream().anyMatch((id) -> id.equals(area.getId()));
            ModWidget container = new ModWidget(hasArea ? roleAreas : otherAreas) {
                private ButtonWidget addButton;
                @Override
                public void onInit() {
                    addButton = new ButtonWidget(this, hasArea ? "X" : "<");
                    addButton.setOnClick(() -> {
                        Messages.sendToServer(new ToggleRoleHasAreaMsg(teamId, roleId, area.getId()));
                    });
                }
                @Override
                public void onRelayoutPost() {
                    addButton.setSize(10, 10);
                    areaPreview.setX(addButton.getWidth() + 2);
                    areaPreview.setY(0);
                    layoutShrinkwrapChildren();
                }
            };
            container.addChild(areaPreview);
        }
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        vLine(stack, getInnerWidth() / 2 - 10, 5, getInnerHeight() - 5, 0xFFffffff);
    }
    
}
