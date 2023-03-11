package com.example.examplemod.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;

class DebugPropertyEntry<T> {
    public String name;
    public Supplier<T> supplier;
    public DebugPropertyEntry(String name, Supplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
    }
}

class DebugChildrenEntry<T extends ModWidget> {
    public String name;
    public Supplier<List<T>> supplier;
    public DebugChildrenEntry(String name, Supplier<List<T>> supplier) {
        this.name = name;
        this.supplier = supplier;
    }
}

public class ModWidget extends GuiComponent implements Renderable {

    // Internal state management
    private boolean isInitialized = false;
    protected boolean layoutDirty = true;
    private ArrayList<ModWidget> removeQueue = new ArrayList<ModWidget>();

    // Layouting properties
    private ArrayList<Runnable> layoutListeners = new ArrayList<Runnable>();
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

    // debugging properties
    private ArrayList<DebugPropertyEntry<?>> debugProperties = new ArrayList<DebugPropertyEntry<?>>();
    private ArrayList<DebugChildrenEntry<?>> debugChildren = new ArrayList<DebugChildrenEntry<?>>();


    public ModWidget(ModWidget parent) {
        if(parent != null) parent.addChild(this);

        registerDebugProperty("global", () -> globalX + ", " + globalY);
        registerDebugProperty("local", () -> localX + ", " + localY);
        registerDebugProperty("size", () -> width + ", " + height);
        registerDebugProperty("padding", () -> padding);
        registerDebugProperties();
    }

    protected void registerDebugProperties() {
    }

    public <T> void registerDebugProperty(String name, Supplier<T> supplier) {
        debugProperties.add(new DebugPropertyEntry<T>(name, supplier));
    }
    public <T extends ModWidget> void registerDebugChildList(String name, Supplier<List<T>> supplier) {
        debugChildren.add(new DebugChildrenEntry<T>(name, supplier));
    }
    public String getDebugName() {
        String name = getClass().getSimpleName();
        if(name == null || name.isEmpty()) {
            name = getClass().getName();
            name = name.substring(name.lastIndexOf('.') + 1);
        }
        return name;
    }

    public void deinit() {
        for (ModWidget child : children) {
            child.parent = null;
            child.deinit();
        }
        children.clear();
        if(parent != null) parent.removeQueue.add(this);
        layoutListeners.clear();
        isInitialized = false;
        setActive(false);
        onDeinit();
    }

    public void onDeinit() {
    }

    public void addListener(ModWidget listener) {
        addListener(listener::relayout);
    }

    public void removeListener(ModWidget listener) {
        removeListener(listener::relayout);
    }

    public void addListener(Runnable listener) {
        layoutListeners.add(listener);
    }

    public void removeListener(Runnable listener) {
        layoutListeners.remove(listener);
    }

    public String getDebugString() {
        return getDebugString(0);
    }
    public String getDebugString(int indent) {
        String indentString = "";
        for (int i = 0; i < indent; i++) indentString += "  ";
        if(!getActive()) {
            return indentString + "(inactive " + getDebugName() + ")\n";
        }
        String result = indentString + getDebugName() + "(\n";
        for (DebugPropertyEntry<?> entry : debugProperties) {
            result += indentString + "  " + entry.name + ": " + entry.supplier.get().toString() + "\n";
        }
        result += indentString + ")\n";
        for (ModWidget child : children) {
            result += child.getDebugString(indent + 1);
        }
        for(DebugChildrenEntry<?> entry : debugChildren) {
            result += indentString + "<<" + entry.name + ">>\n";
            for (ModWidget child : entry.supplier.get()) {
                result += child.getDebugString(indent + 1);
            }
        }
        return result;
    }

    public ModWidget debugWidget() {
        return new DebugWidget(null, this);
    }

    public static <T extends AbstractWidget> AbstractWidgetWrapper<T> of(ModWidget parent, T widget) {
        return new AbstractWidgetWrapper<T>(parent, widget);
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
        if(!getActive()) return;
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
        int minX = 0;
        int minY = 0;
        int maxX = 0;
        int maxY = 0;
        for (ModWidget child : children) {
            minX = Math.min(minX, child.getX());
            minY = Math.min(minY, child.getY());
            maxX = Math.max(maxX, child.getX() + child.getWidth());
            maxY = Math.max(maxY, child.getY() + child.getHeight());
        }

        setInnerWidth(maxX - minX);
        setInnerHeight(maxY - minY);
        for (ModWidget child : children) {
            if(minX < 0) child.setX(child.getX() - minX);
            if(minY < 0) child.setY(child.getY() - minY);
        }
    }

    public void layoutCenterX() {
        if(parent == null) return;
        setX((parent.getInnerWidth() - getWidth()) / 2);
    }

    public void layoutCenterY() {
        if(parent == null) return;
        setY((parent.getInnerHeight() - getHeight()) / 2);
    }

    public void layoutFillX() {
        if(parent == null) return;
        setX(0);
        setWidth(parent.getInnerWidth());
    }

    public void layoutFillY() {
        if(parent == null) return;
        setY(0);
        setHeight(parent.getInnerHeight());
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        int x = getGlobalX();
        int y = getGlobalY();
        return mouseX >= x && mouseX <= x + getWidth() && mouseY >= y && mouseY <= y + getHeight();
    }

    private boolean getDeepLayoutDirty() {
        if(layoutDirty) return true;
        for (ModWidget child : children) {
            if(child.getDeepLayoutDirty()) return true;
        }
        return false;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(!getActive()) return;
        if(!isInitialized) {
            init();
        }
        boolean deepLayoutDirty = getDeepLayoutDirty();
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
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        if(deepLayoutDirty) {
            for(Runnable listener : layoutListeners) {
                listener.run();
            }
        }
    }

    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {}

    public <T extends ModWidget> T addChild(T child) {
        if(child.parent != null) child.parent.children.remove(child);
        child.parent = this;
        children.add(child);
        return child;
    }

    public <T extends AbstractWidget> AbstractWidgetWrapper<T> addChild(T child) {
        return new AbstractWidgetWrapper<T>(this, child);
    }

    public void clearChildren() {
        children.clear();
    }

    public boolean mousePressed(double mouseX, double mouseY, int button) {
        if(!getActive()) return false;
        for (ModWidget child : children) {
            if(child.mousePressed(mouseX, mouseY, button)) return true;
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        return onMousePressed(mouseX, mouseY, button);
    }
    public boolean onMousePressed(double mouseX, double mouseY, int button) { return false; }
 
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(!getActive()) return false;
        
        for (ModWidget child : children) {
            if(child.mouseReleased(mouseX, mouseY, button)) return true;
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        return onMouseReleased(mouseX, mouseY, button);
    }
    public boolean onMouseReleased(double mouseX, double mouseY, int button) { return false; }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(!getActive()) return false;
        for (ModWidget child : children) {
            if(child.mouseScrolled(mouseX, mouseY, amount)) return true;
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        return onMouseScrolled(mouseX, mouseY, amount);
    }
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount) { return false; }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!getActive()) return false;
        for (ModWidget child : children) {
            if(child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        return onKeyPressed(keyCode, scanCode, modifiers);
    }
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) { return false; }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(!getActive()) return false;
        for (ModWidget child : children) {
            if(child.keyReleased(keyCode, scanCode, modifiers)) return true;
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        return onKeyReleased(keyCode, scanCode, modifiers);
    }
    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) { return false; }

    public boolean charTyped(char codePoint, int modifiers) {
        if(!getActive()) return false;
        for (ModWidget child : children) {
            if(child.charTyped(codePoint, modifiers)) return true;
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        return onCharTyped(codePoint, modifiers);
    }
    public boolean onCharTyped(char codePoint, int modifiers) { return false; }

    public boolean changeFocus(boolean lookForwards) {
        if(!getActive()) return false;
        for (ModWidget child : children) {
            if(child.changeFocus(lookForwards)) return true;
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
        return onChangeFocus(lookForwards);
    }
    public boolean onChangeFocus(boolean lookForwards) { return false; }

    public void tick() {
        if(!getActive()) return;
        onTick();
        for (ModWidget child : children) {
            child.tick();
        }
        for (ModWidget child : removeQueue) {
            children.remove(child);
        }
        removeQueue.clear();
    }

    public void onTick() {}

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setActive(boolean active) {
        this.active = active;
        setLayoutDirty();
        if(parent != null) parent.setLayoutDirty();
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
        return getWidth() - getPadding() * 2;
    }

    public int getInnerHeight() {
        return getHeight() - getPadding() * 2;
    }

    public void setInnerWidth(int width) {
        setWidth(width + getPadding() * 2);
    }

    public void setInnerHeight(int height) {
        setHeight(height + getPadding() * 2);
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
            globalX = getX();
            globalY = getY();
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

    public boolean sizeBasedOnParent() {
        return true;
    }

    public void setLayoutDirty() {
        setLayoutDirty(true, true);
    }
    public void setLayoutDirty(boolean doParent, boolean doChildren) {
        layoutDirty = true;
        if(doParent && parent != null && parent.layoutBasedOnChildren()) parent.setLayoutDirty(true, false);
        if(doChildren) {
            for (ModWidget child : children) {
                if(child.sizeBasedOnParent()) {
                    child.setLayoutDirty(false, true);
                }
            }
        }
    }

}
