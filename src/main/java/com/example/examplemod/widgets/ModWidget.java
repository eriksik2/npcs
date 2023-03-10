package com.example.examplemod.widgets;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;

public class ModWidget extends GuiComponent implements Renderable {

    private boolean isInitialized = false;
    protected boolean layoutDirty = true;

    private int globalX = 0;
    private int globalY = 0;
    private int localX = 0;
    private int localY = 0;
    private int width = 0;
    private int height = 0;
    private boolean active = true;
    protected ModWidget parent;
    protected ArrayList<ModWidget> children = new ArrayList<ModWidget>();
    private int padding = 0;

    public ModWidget(ModWidget parent) {
        if(parent != null) parent.addChild(this);
    }

    public String toDebugString() {
        return toDebugString(0);
    }
    public String toDebugString(int indent) {
        String indentString = "";
        for (int i = 0; i < indent; i++) indentString += "  ";
        String result = indentString + this.getClass().getSimpleName() + "(\n";
        result += indentString + "  global: " + globalX + ", " + globalY + "\n";
        result += indentString + "  local:  " + localX + ", " + localY + "\n";
        result += indentString + "  size:   " + width + ", " + height + "\n";
        result += indentString + "  padding: " + padding + "\n";
        result += indentString + ")\n";
        for (ModWidget child : children) {
            result += child.toDebugString(indent + 1);
        }
        return result;
    }

    public ModWidget debugWidget() {
        String debugString = toDebugString();
        String[] lines = debugString.split("\n");
        ScrollableListWidget root = new ScrollableListWidget(null);
        for(String line : lines) {
            TextWidget text = new TextWidget(root, line);
            root.addChild(text);
        }
        root.layoutShrinkwrapChildren();
        return root;
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
        init();
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

    public void layoutShrinkwrapChildren() {
        int width = 0;
        int height = 0;
        for (ModWidget child : children) {
            width = Math.max(width, child.getX() + child.getWidth());
            height = Math.max(height, child.getY() + child.getHeight());
        }
        setWidth(width);
        setHeight(height);
    }

    public void layoutCenterX() {
        if(parent == null) return;
        setX((parent.getInnerWidth() - getWidth()) / 2);
    }

    public void layoutCenterY() {
        if(parent == null) return;
        setX((parent.getInnerWidth() - getWidth()) / 2);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        int x = getGlobalX();
        int y = getGlobalY();
        return mouseX >= x && mouseX <= x + getWidth() && mouseY >= y && mouseY <= y + getHeight();
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
        stack.translate(getX() + getInnerX(), getY() + getInnerY(), 0);
        onRender(stack, mouseX, mouseY, partialTicks);
        for (ModWidget child : children) {
            child.render(stack, mouseX, mouseY, partialTicks);
        }
        stack.popPose();
    }

    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {}

    public <T extends ModWidget> T addChild(T child) {
        if(child.parent != null) child.parent.children.remove(child);
        child.parent = this;
        children.add(child);
        return child;
    }

    public <T extends AbstractWidget> AbstractWidgetWrapper addChild(T child) {
        return new AbstractWidgetWrapper(this, child);
    }

    public void clearChildren() {
        children.clear();
    }

    public boolean mousePressed(double mouseX, double mouseY, int button) {
        for (ModWidget child : children) {
            if(child.mousePressed(mouseX, mouseY, button)) return true;
        }
        return onMousePressed(mouseX, mouseY, button);
    }
 
    public void mouseReleased(double mouseX, double mouseY) {
        onMouseReleased(mouseX, mouseY);
        for (ModWidget child : children) {
            child.mouseReleased(mouseX, mouseY);
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        onMouseScrolled(mouseX, mouseY, amount);
        for (ModWidget child : children) {
            child.mouseScrolled(mouseX, mouseY, amount);
        }
    }

    public boolean onMousePressed(double mouseX, double mouseY, int button) { return false; }
    public void onMouseReleased(double mouseX, double mouseY) {}
    public void onMouseScrolled(double mouseX, double mouseY, double amount) {}

    public void tick() {
        onTick();
        for (ModWidget child : children) {
            child.tick();
        }
    }

    public void onTick() {}

    public void setActive(boolean active) {
        this.active = active;
        parent.setLayoutDirty();
    }

    public boolean getActive() {
        return active;
    }

    public int getInnerX() {
        return padding;
    }

    public int getInnerY() {
        return padding;
    }

    public int getInnerWidth() {
        return getWidth() - padding * 2;
    }

    public int getInnerHeight() {
        return getHeight() - padding * 2;
    }

    public int getGlobalX() {
        return this.globalX;
    }

    public int getGlobalY() {
        return this.globalY;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        setLayoutDirty();
    }

    public void setWidth(int width) {
        this.width = width;
        setLayoutDirty();
    }

    public void setHeight(int height) {
        this.height = height;
        setLayoutDirty();
    }

    public void setX(int x) {
        this.localX = x;
        refreshGlobalPosition();
        setLayoutDirty();
    }

    public void setY(int y) {
        this.localY = y;
        refreshGlobalPosition();
        setLayoutDirty();
    }

    private void refreshGlobalPosition() {
        if(parent == null) {
            globalX = localX;
            globalY = localY;
        } else {
            this.globalX = parent.getGlobalX() + parent.getInnerX() + getX();
            this.globalY = parent.getGlobalY() + parent.getInnerY() + getY();
        }
        for(ModWidget child : children) {
            child.refreshGlobalPosition();
        }
    }

    public int getX() {
        return localX;
    }

    public int getY() {
        return localY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPosition(int x, int y) {
        this.localX = x;
        this.localY = y;
        refreshGlobalPosition();
        setLayoutDirty();
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
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
