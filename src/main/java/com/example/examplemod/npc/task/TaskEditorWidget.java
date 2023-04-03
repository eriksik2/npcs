package com.example.examplemod.npc.task;

import com.example.examplemod.widgets.DrawerWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.TextWidget;

public class TaskEditorWidget extends ModWidget {

    private DrawerWidget drawer;
    private NpcTask task;

    public TaskEditorWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        drawer = new DrawerWidget(this);
        drawer.init();
        drawer.setHeader(new TextWidget(null, ""));
    }

    @Override
    public void onRelayoutPre() {
        drawer.setWidth(getInnerWidth());
        if(task != null) {
            drawer.setHeader(new TextWidget(null, task.getType().getName()));
        }
    }

    @Override
    public void onRelayoutPost() {
        layoutShrinkwrapChildren();
    }
    
    public void setTask(NpcTask task) {
        boolean isSame = this.task != null
            && this.task.getId() == task.getId()
            && this.task.getManager().getId() == task.getManager().getId()
            && this.task.getManager().getManager().getId() == task.getManager().getManager().getId();
        this.task = task;

        if(!isSame) {
            drawer.clearChildren();
            for(TaskParameterType<?, ?> paramType : task.getType().getParameters()) {
                TaskParameterWidget widget = new TaskParameterWidget(drawer);
                widget.init();
            }
        }
        for(int i = 0; i < task.getType().getParameters().size(); i++) {
            TaskParameterWidget widget = (TaskParameterWidget) drawer.getChildren().get(i);
            widget.setParameter(task, task.getType().getParameters().get(i));
        }
    }
}
