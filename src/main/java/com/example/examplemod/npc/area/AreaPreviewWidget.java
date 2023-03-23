package com.example.examplemod.npc.area;

import com.example.examplemod.networking.subscribe.ServerSubscription;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.npc.team.TeamSubscriptionBroker;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.TextWidget;
import com.mojang.blaze3d.vertex.PoseStack;

public class AreaPreviewWidget extends ModWidget {

    private TeamSubscriptionBroker teamSubscriptionBroker = Registration.TEAM_SUBSCRIPTION_BROKER.get();
    private ServerSubscription<NpcTeam> teamSubscription;
    private Integer teamId;
    private Integer areaId;

    private TextWidget name;
    private Integer color;

    public AreaPreviewWidget(ModWidget parent) {
        super(parent);
    }

    public void setArea(Integer teamId, Integer areaId) {
        this.teamId = teamId;
        this.areaId = areaId;
        if(teamSubscription != null) {
            teamSubscription.unsubscribe();
            teamSubscription = null;
        }
        teamSubscription = teamSubscriptionBroker.subscribe(teamId, this::onTeamUpdate);
    }

    private void onTeamUpdate(NpcTeam team) {
        NpcArea area = team.getArea(areaId);
        name.setText(area.getName());
        color = area.getColor();
    }

    @Override
    public void onInit() {
        name = new TextWidget(this, "");
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
        var height = name.getHeight();
        name.setX(height + 2);
        name.setY(0);
        layoutShrinkwrapChildren();
    }
    
    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        fill(stack, 0, 0, getHeight(), getHeight(), 0xFF_ff9999);
    }
}
