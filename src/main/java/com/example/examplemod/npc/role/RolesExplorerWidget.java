package com.example.examplemod.npc.role;

import java.util.List;
import java.util.concurrent.locks.Condition;

import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.widgets.AbstractWidgetWrapper;
import com.example.examplemod.widgets.ButtonWidget;
import com.example.examplemod.widgets.ConditionalWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.PopupManagerWidget;
import com.example.examplemod.widgets.ScrollableListWidget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class RolesExplorerWidget extends ModWidget {

    private ScrollableListWidget rolesList;
    private PopupManagerWidget popupManager;
    private ButtonWidget addRoleButton;

    private NpcRole selectedRole;

    private ModWidget detailsView;
    private RoleEditorWidget roleEditor;

    private NpcTeam team;

    public RolesExplorerWidget(ModWidget parent, NpcTeam team, PopupManagerWidget popupManager) {
        super(parent);
        this.popupManager = popupManager;
        this.team = team;
    }

    public void onInit() {
        this.layoutFillRemaining();
        rolesList = new ScrollableListWidget(this);

        addRoleButton = new ButtonWidget(this, "Add role");
        addRoleButton.setOnClick(() -> {
            if(team == null) return;
            System.out.println("add role");
            popupManager.push(new AddRoleWidget(null, team.getId()));
        });


        detailsView = new ConditionalWidget(this, () -> selectedRole != null);
        roleEditor = new RoleEditorWidget(detailsView) {
            @Override
            public void onRelayoutPre() {
                layoutFillRemaining();
                super.onRelayoutPre();
            }
        };
    }

    @Override
    public void onRelayoutPre() {
        rolesList.setGap(5);
        rolesList.setWidth(getInnerWidth()/2 - 5);
        rolesList.setHeight(getInnerHeight() - 20);

        addRoleButton.setWidth(getInnerWidth()/2 - 5);
        addRoleButton.setHeight(20);
        addRoleButton.setY(rolesList.getHeight());

        detailsView.setWidth(getInnerWidth() - rolesList.getWidth() - 5);
        detailsView.setHeight(getInnerHeight());
        detailsView.setX(rolesList.getWidth() + 10);
    }

    public void setTeam(NpcTeam team) {
        this.team = team;
        rolesList.clearChildren();
        for(NpcRole role : team.getRoles()) {
            new RoleWidget(rolesList, popupManager, role, (selectedRole) -> {
                this.selectedRole = selectedRole;
                roleEditor.setRole(team, selectedRole);
            });
        }
    }
}
