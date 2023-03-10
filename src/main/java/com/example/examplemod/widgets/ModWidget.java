package com.example.examplemod.widgets;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ModWidget extends AbstractWidget {

    private boolean isInitialized = false;
    protected boolean layoutDirty = false;
    protected ModWidget parent;
    protected ArrayList<ModWidget> children = new ArrayList<ModWidget>();
    private int padding = 0;

    public ModWidget(ModWidget parent) {
        super(0, 0, 0, 0, Component.empty());
        if(parent != null) parent.addChild(this);
    }

    public static AbstractWidgetWrapper of(ModWidget parent, AbstractWidget widget) {
        return new AbstractWidgetWrapper(parent, widget);
    }

    public final void init() {
        if(isInitialized) return;
        onInit();
        for (ModWidget child : children) {
            child.init();
        }
        isInitialized = true;
    }

    public void onInit() {}

    public final void relayout() {
        onRelayoutPre();
        for (ModWidget child : children) {
            child.relayout();
        }
        onRelayoutPost();
        layoutDirty = false;
    }

    public void onRelayoutPre() {}
    public void onRelayoutPost() {}

    public void layoutFillRemaining() {
        if(parent == null) return;
        setWidth(parent.getInnerWidth() - getX());
        setHeight(parent.getInnerHeight() - getY());
    }

    public void layoutCenterX() {
        if(parent == null) return;
        setX((parent.getInnerWidth() - getWidth()) / 2);
    }

    public void layoutCenterY() {
        if(parent == null) return;
        setX((parent.getInnerWidth() - getWidth()) / 2);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int x = getGlobalX();
        int y = getGlobalY();
        return mouseX >= x && mouseX <= x + getWidth() && mouseY >= y && mouseY <= y + getHeight();
    }

    public int getInnerX() {
        return getX() + padding;
    }

    public int getInnerY() {
        return getY() + padding;
    }

    public int getInnerWidth() {
        return getWidth() - padding * 2;
    }

    public int getInnerHeight() {
        return getHeight() - padding * 2;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(!isInitialized) {
            init();
        }
        if(layoutDirty) {
            relayout();
        }
        stack.pushPose();
        stack.translate(getInnerX(), getInnerY(), 0);
        onRender(stack, mouseX, mouseY, partialTicks);
        for (ModWidget child : children) {
            child.render(stack, mouseX, mouseY, partialTicks);
        }
        stack.popPose();
    }

    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {}

    public <T extends ModWidget> T addChild(T child) {
        child.parent = this;
        children.add(child);
        return child;
    }

    public <T extends AbstractWidget> AbstractWidgetWrapper addChild(T child) {
        AbstractWidgetWrapper child1 = new AbstractWidgetWrapper(this, child);
        children.add(child1);
        return child1;
    }

    public void clearChildren() {
        children.clear();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        for (ModWidget child : children) {
            if(!child.isMouseOver(mouseX, mouseY)) continue;
            child.onClick(mouseX, mouseY);
        }
    }
 
    @Override
    public void onRelease(double mouseX, double mouseY) {
        for (ModWidget child : children) {
            if(!child.isMouseOver(mouseX, mouseY)) continue;
            child.onRelease(mouseX, mouseY);
        }
    }

    public void onScroll(double mouseX, double mouseY, double amount) {
        for (ModWidget child : children) {
            if(!child.isMouseOver(mouseX, mouseY)) continue;
            child.onScroll(mouseX, mouseY, amount);
        }
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
        throw new UnsupportedOperationException("ModWidget does not support narration");
    }

    public int getGlobalX() {
        if(parent == null) return getX();
        return getX() + parent.getGlobalX();
    }

    public int getGlobalY() {
        if(parent == null) return getY();
        return getY() + parent.getGlobalY();
    }

    public void tick() {
        for (ModWidget child : children) {
            child.tick();
        }
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        setLayoutDirty();
        this.padding = padding;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        setLayoutDirty();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        setLayoutDirty();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        setLayoutDirty();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        setLayoutDirty();
    }

    @Override
    public void setPosition(int x, int y) {
        super.setX(x);
        super.setY(y);
        setLayoutDirty();
    }

    public boolean layoutBasedOnChildren() {
        return false;
    }

    public boolean layoutBasedOnParent() {
        return false;
    }

    public void setLayoutDirty() {
        setLayoutDirty(true, null);
    }
    public void setLayoutDirty(boolean doParent, ModWidget dontDo) {
        if(dontDo == this) return;
        layoutDirty = true;
        if(doParent && parent != null && parent.layoutBasedOnChildren()) parent.setLayoutDirty(true, this);
        for (ModWidget child : children) {
            if(!child.layoutBasedOnParent()) continue;
            child.setLayoutDirty(false, null);
        }
    }

}
