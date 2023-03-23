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
        ModWidget cnt = new ModWidget(drawer);
        cnt.setSize(50, 50);
        new TextWidget(cnt, "Test content");
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
        this.task = task;
    }
}
