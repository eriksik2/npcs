package com.example.examplemod.widgets;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

class PopupWidget extends ModWidget {

    private PopupManagerWidget manager;

    public PopupWidget(ModWidget parent, PopupManagerWidget manager) {
        super(parent);
        this.manager = manager;
    }

    @Override
    public void onRelayoutPre() {
        setX(manager.getGlobalX());
        setY(manager.getGlobalY());
        setWidth(manager.getInnerWidth());
        setHeight(manager.getInnerHeight());
    }

    @Override
    public void onRelayoutPost() {
        for (ModWidget child : children) {
            child.layoutCenterX();
            child.layoutCenterY();
        }
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(children.size() == 0) {
            close();
            return;
        }
    }

    @Override
    public boolean mousePressed(double mouseX, double mouseY, int button) {
        if(children.size() == 0) return false;
        if(!getActive()) return false;
        if(!children.get(0).isMouseOver(mouseX, mouseY)) {
            close();
            return false;
        }
        return super.mousePressed(mouseX, mouseY, button);
    }

    public void close() {
        manager.removalQueue.add(this);
        deinit();
    }
}

public class PopupManagerWidget extends ModWidget {

    public ArrayList<PopupWidget> removalQueue = new ArrayList<>();
    public ArrayList<PopupWidget> popups = new ArrayList<>();

    public PopupManagerWidget(PopupWidget parent) {
        super(parent);
    }

    @Override
    protected void registerDebugProperties() {
        registerDebugChildList("popups", () -> popups);
    }

    public PopupWidget push(ModWidget popup) {
        PopupWidget popupWidget = new PopupWidget(null, this);
        popupWidget.addChild(popup);
        popups.add(popupWidget);
        setLayoutDirty();
        return popupWidget;
    }

    @Override
    public void onRelayoutPre() {
    }

    @Override
    public void onRelayoutPost() {
        for (PopupWidget popup : popups) {
            popup.relayout();
        }
    }

    public void pop() {
        popups.remove(popups.size() - 1);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        for (PopupWidget popup : popups) {
            popup.render(stack, mouseX, mouseY, partialTicks);
        }
        for (PopupWidget popup : removalQueue) {
            popups.remove(popup);
        }
        removalQueue.clear();
    }

    @Override
    public boolean mousePressed(double mouseX, double mouseY, int button) {
        if(popups.size() == 0) {
            return super.mousePressed(mouseX, mouseY, button);
        } else {
            return popups.get(popups.size() - 1).mousePressed(mouseX, mouseY, button);
        }
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(popups.size() == 0) {
            return super.mouseReleased(mouseX, mouseY, button);
        } else {
            return popups.get(popups.size() - 1).mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(popups.size() == 0) {
            return super.mouseScrolled(mouseX, mouseY, amount);
        } else {
            return popups.get(popups.size() - 1).mouseScrolled(mouseX, mouseY, amount);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(popups.size() == 0) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            return popups.get(popups.size() - 1).keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(popups.size() == 0) {
            return super.keyReleased(keyCode, scanCode, modifiers);
        } else {
            return popups.get(popups.size() - 1).keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(popups.size() == 0) {
            return super.charTyped(codePoint, modifiers);
        } else {
            return popups.get(popups.size() - 1).charTyped(codePoint, modifiers);
        }
    }

    @Override
    public void onTick() {
        for (PopupWidget popup : popups) {
            popup.tick();
        }
    }
    
}
