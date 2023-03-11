package com.example.examplemod.widgets;

import java.util.List;
import java.util.concurrent.locks.Condition;

import com.example.examplemod.npc.role.NpcRole;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class RolesExplorerWidget extends ModWidget {

    private ScrollableListWidget rolesList;
    private PopupManagerWidget popupManager;
    private AbstractWidgetWrapper<Button> addRoleButton;

    private NpcRole selectedRole;

    private ModWidget detailsView;

    private Integer teamId;

    public RolesExplorerWidget(ModWidget parent, Integer teamId, PopupManagerWidget popupManager) {
        super(parent);
        this.popupManager = popupManager;
        this.teamId = teamId;
    }

    public void onInit() {
        this.layoutFillRemaining();
        rolesList = new ScrollableListWidget(this);

        addRoleButton = this.addChild(Button.builder(Component.literal("Add role"), (button) -> {
            if(teamId == null) return;
            popupManager.push(new AddRoleWidget(null, teamId));
        }).build());

        detailsView = new ScrollableWidget(this);
        new ConditionalWidget(detailsView, () -> selectedRole != null) {
            private TextWidget name;
            private TextWidget description;
            private AbstractWidgetWrapper<Button> removeButton;
            @Override
            public void onInit() {
                name = new TextWidget(this, "");
                description = new TextWidget(this, "");
                description.setWrap(true);
                removeButton = this.addChild(Button.builder(Component.literal("Remove role"), (button) -> {
                    if(selectedRole == null) return;
                    System.out.println("TODO Remove role " + selectedRole.getName());
                }).build());
            }

            @Override
            public void onRelayoutPre() {
                name.setText(selectedRole.getName());
                description.setText(selectedRole.getDescription());

                layoutFillX();
                layoutFillY();

                name.setWidth(getInnerWidth());

                description.setY(name.getHeight());
                description.setWidth(getInnerWidth());

                removeButton.setY(Math.max(name.getHeight() + description.getHeight(), getInnerHeight() - 20));
                removeButton.setX(getInnerWidth()/2);
                removeButton.setWidth(getInnerWidth()/2);
                removeButton.setHeight(20);

                layoutShrinkwrapChildren();
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

    public void setRolesList(List<NpcRole> roles) {
        rolesList.clearChildren();
        for(NpcRole role : roles) {
            new RoleWidget(rolesList, popupManager, role, (selectedRole) -> {
                this.selectedRole = selectedRole;
            });
        }
    }
}
