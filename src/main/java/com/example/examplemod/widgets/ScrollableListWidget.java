package com.example.examplemod.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

public class ScrollableListWidget extends ModWidget {

    private int childrenHeight = 0;
    private int gap = 0;
    private float scroll = 0;
    private float scrollSpeed = 8.0f;

    public ScrollableListWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double amount) {
        if(!isMouseOver(mouseX, mouseY)) return;
        scroll += amount*scrollSpeed;
        super.onMouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public int getInnerY() {
        return super.getInnerY() + (int)scroll;
    }

    @Override
    public void onRelayoutPost() {
        childrenHeight = 0;
        for (ModWidget child : children) {
            child.setY(childrenHeight);
            childrenHeight += child.getHeight() + gap;
        }
    }

    @Override
    public <T extends ModWidget> T addChild(T child) {
        T addedChild = super.addChild(child);
        setLayoutDirty();
        return addedChild;
    }

    @Override
    public void clearChildren() {
        childrenHeight = 0;
        super.clearChildren();
    }

    public void setGap(int gap) {
        this.gap = gap;
        setLayoutDirty();
    }
    
    public int getGap() {
        return gap;
    }

    @Override
    public boolean layoutBasedOnChildren() {
        return true;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        enableScissor(getGlobalX(), getGlobalY(), getInnerWidth(), getInnerHeight());
        super.render(stack, mouseX, mouseY, partialTicks);
        disableScissor();
    }
}
