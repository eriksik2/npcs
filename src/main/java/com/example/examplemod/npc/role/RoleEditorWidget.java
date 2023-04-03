package com.example.examplemod.npc.role;

import java.util.Collection;
import java.util.List;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.task.AddTaskToRoleMsg;
import com.example.examplemod.npc.task.NpcTask;
import com.example.examplemod.npc.task.TaskEditorWidget;
import com.example.examplemod.npc.task.TaskRegistration;
import com.example.examplemod.npc.task.TaskType;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.widgets.ButtonWidget;
import com.example.examplemod.widgets.ColumnLayoutWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.NpcPreviewWidget;
import com.example.examplemod.widgets.RowLayoutWidget;
import com.example.examplemod.widgets.ScrollableListWidget;
import com.example.examplemod.widgets.ScrollableWidget;
import com.example.examplemod.widgets.TextWidget;

import net.minecraftforge.registries.RegistryObject;

public class RoleEditorWidget extends ModWidget {

    private ScrollableWidget scrollable;
    private TextWidget name;
    private TextWidget description;

    private TextWidget npcListLabel;
    private RowLayoutWidget npcList;

    private TextWidget taskListLabel;
    private ColumnLayoutWidget taskList;
    private RowLayoutWidget addTaskButtons;

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

        taskListLabel = new TextWidget(scrollable, "Tasks");
        taskList = new ColumnLayoutWidget(scrollable);

        addTaskButtons = new RowLayoutWidget(scrollable);
        addTaskButtons.setWrap(true);
        addTaskButtons.setGap(4);
        addTaskButtons.setRowGap(2);
        Collection<RegistryObject<TaskType>> taskTypes = TaskRegistration.TASK_TYPES.getEntries();
        for(RegistryObject<TaskType> taskType : taskTypes) {
            ButtonWidget button = new ButtonWidget(addTaskButtons, "Add " + taskType.get().getName());
            button.setWrap(false);
            button.setOnClick(() -> {
                if(team == null) return;
                if(role == null) return;
                Messages.sendToServer(new AddTaskToRoleMsg(team.getId(), role.getId(), taskType.getId()));
            });
            button.setWidth(50);
            button.setHeight(10);
        }

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

        taskListLabel.setY(npcList.getY() + npcList.getHeight() + 5);
        taskList.setY(taskListLabel.getY() + taskListLabel.getHeight() + 5);
        taskList.setX(5);
        taskList.setWidth(scrollable.getInnerWidth() - taskList.getX() - 2);
        for(ModWidget child : taskList.getChildren()) {
            //child.setWidth(taskList.getInnerWidth());
        }
        taskList.relayout();

        addTaskButtons.setY(taskList.getY() + taskList.getHeight() + 5);
        addTaskButtons.setX(5);
        addTaskButtons.setWidth(scrollable.getInnerWidth() - addTaskButtons.getX() - 2);
        addTaskButtons.relayout();

        roleAreasEditor.setY(addTaskButtons.getY() + addTaskButtons.getHeight() + 5);
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
        boolean isSame = this.team != null
            && this.role != null
            && this.team.getId() == team.getId()
            && this.role.getId() == role.getId();

        roleAreasEditor.setRole(team.getId(), role.getId());
        
        List<Integer> npcsOfRole = team.getNpcsOf(role.getId());
        List<NpcTask> tasksOfRole = role.getTasks();
        if(!isSame || team.getNpcsOf(role.getId()).size() != npcList.getChildren().size()) {
            npcList.clearChildren();
            for(Integer npc : npcsOfRole) {
                NpcPreviewWidget widget = new NpcPreviewWidget(npcList) {
                    @Override
                    public void onRelayoutPre() {
                        super.onRelayoutPre();
                        //layoutFillX();
                    }
                };
                widget.init();
            }
            
        }
        if(!isSame || role.getTasks().size() != taskList.getChildren().size()) {
            taskList.clearChildren();
            for(NpcTask task : tasksOfRole) {
                TaskEditorWidget widget = new TaskEditorWidget(taskList) {
                    @Override
                    public void onRelayoutPre() {
                        layoutFillX();
                        super.onRelayoutPre();
                    }
                };
                widget.init();
            }
        }
        for(int i = 0; i < npcsOfRole.size(); i++) {
            NpcPreviewWidget widget = (NpcPreviewWidget) npcList.getChildren().get(i);
            widget.setNpcId(npcsOfRole.get(i));
        }
        for(int i = 0; i < tasksOfRole.size(); i++) {
            TaskEditorWidget widget = (TaskEditorWidget) taskList.getChildren().get(i);
            widget.setTask(tasksOfRole.get(i));
        }
        this.team = team;
        this.role = role;
        setLayoutDirty();
    }
    
}
