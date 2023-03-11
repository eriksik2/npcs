package com.example.examplemod.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

public class ScrollableWidget extends ModWidget {

    private int childrenHeight = 0;
    private float scrollTarget = 0;
    private float scroll = 0;
    private float scrollSpeed = 16.0f;

    public ScrollableWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        if(childrenHeight > getInnerHeight()) {
            scrollTarget -= amount*scrollSpeed;
        } else {
            scrollTarget = 0;
        }
        return super.onMouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void onTick() {
        if(scrollTarget != scroll) {
            if(Math.abs(scrollTarget - scroll) < 1) {
                scroll = scrollTarget;
            } else {
                scroll += (scrollTarget - scroll) * 0.20f;
            }
        }
        if(scrollTarget > childrenHeight - getInnerHeight()) scrollTarget = childrenHeight - getInnerHeight();
        if(scrollTarget < 0) scrollTarget = 0;
        super.onTick();
    }

    @Override
    public int getInnerY() {
        if(childrenHeight <= getInnerHeight()) return super.getInnerY();
        return super.getInnerY() - Math.round(scroll);
    }

    @Override
    public void onRelayoutPost() {
        childrenHeight = 0;
        for (ModWidget child : children) {
            childrenHeight = Math.max(childrenHeight, child.getY() + child.getHeight());
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

    @Override
    public boolean layoutBasedOnChildren() {
        return true;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        enableScissor(getGlobalX(), getGlobalY(), getGlobalX()+getInnerWidth(), getGlobalY()+getInnerHeight());
        super.render(stack, mouseX, mouseY, partialTicks);
        disableScissor();
    }

    @Override
    public boolean mousePressed(double mouseX, double mouseY, int button) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        return super.mousePressed(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

}
