package com.example.examplemod.npc.role;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.widgets.ButtonWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.NpcPreviewWidget;
import com.example.examplemod.widgets.RowLayoutWidget;
import com.example.examplemod.widgets.ScrollableListWidget;
import com.example.examplemod.widgets.ScrollableWidget;
import com.example.examplemod.widgets.TextWidget;

public class RoleEditorWidget extends ModWidget {

    private ScrollableWidget scrollable;
    private TextWidget name;
    private TextWidget description;

    private TextWidget npcListLabel;

    private RowLayoutWidget npcList;

    private RoleAreasEditorWidget roleAreasEditor;

    private ButtonWidget saveButton;
    private ButtonWidget removeButton;

    private NpcTeam team;
    private NpcRole role;

    public RoleEditorWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        scrollable = new ScrollableWidget(this);
        scrollable.setPadding(5);
        name = new TextWidget(scrollable, "Name");
        description = new TextWidget(scrollable, "Description");
        description.setWrap(true);

        npcListLabel = new TextWidget(scrollable, "Workers");
        npcList = new RowLayoutWidget(scrollable);
        npcList.setGap(2);
        npcList.setRowGap(2);
        npcList.setWrap(true);

        roleAreasEditor = new RoleAreasEditorWidget(scrollable);

        saveButton = new ButtonWidget(this, "Save");
        saveButton.setOnClick(() -> {

        });

        removeButton = new ButtonWidget(this, "Remove role");
        removeButton.setOnClick(() -> {
            if(team == null) return;
            if(role == null) return;
            Messages.sendToServer(new RemoveTeamRoleMsg(team.getId(), role.getId()));
        });
    }

    @Override
    public void onRelayoutPre() {
        if(role != null) {
            name.setText(role.getName());
            description.setText(role.getDescription());
        }

        scrollable.layoutFillX();
        scrollable.setHeight(getInnerHeight() - 20);

        name.setY(0);
        name.layoutFillX();
        description.layoutFillX();

        npcListLabel.setY(description.getY() + description.getHeight() + 5);
        npcListLabel.layoutFillX();
        npcList.setY(npcListLabel.getY() + npcListLabel.getHeight() + 5);
        npcList.setX(5);
        npcList.setWidth(scrollable.getInnerWidth() - npcList.getX() - 5);
        npcList.relayout();

        roleAreasEditor.setY(npcList.getY() + npcList.getHeight() + 18);
        roleAreasEditor.setX(0);
        roleAreasEditor.setWidth(scrollable.getInnerWidth() - roleAreasEditor.getX() - 2);
        roleAreasEditor.setHeight(100);

        saveButton.setY(scrollable.getHeight());
        saveButton.setX(getInnerWidth()/2 + 5);
        saveButton.setWidth(getInnerWidth()/2 - 5);
        saveButton.setHeight(20);

        removeButton.setY(scrollable.getHeight());
        removeButton.setX(0);
        removeButton.setWidth(getInnerWidth()/2 - 5);
        removeButton.setHeight(20);
    }

    @Override
    public void onRelayoutPost() {
        description.setY(name.getY() + name.getHeight());
    }

    public void setRole(NpcTeam team, NpcRole role) {
        this.team = team;
        this.role = role;

        roleAreasEditor.setRole(team.getId(), role.getId());
        npcList.clearChildren();
        if(role != null) {
            for(Integer npc : team.getNpcsOf(role.getId())) {
                NpcPreviewWidget widget = new NpcPreviewWidget(npcList) {
                    @Override
                    public void onRelayoutPre() {
                        super.onRelayoutPre();
                        //layoutFillX();
                    }
                };
                widget.init();
                widget.setNpcId(npc);
            }
        }
        setLayoutDirty();
    }
    
}
