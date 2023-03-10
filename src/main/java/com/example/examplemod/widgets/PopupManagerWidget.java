package com.example.examplemod.widgets;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

class PopupWidget extends ModWidget {

    private PopupManagerWidget manager;

    public PopupWidget(ModWidget parent, PopupManagerWidget manager) {
        super(parent);
        this.manager = manager;
        this.parent = manager;
    }

    @Override
    public void onRelayoutPre() {
        setX(0);
        setY(0);
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

    public void close() {
        manager.popups.remove(this);
    }
}

public class PopupManagerWidget extends ModWidget {

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
    public void mouseReleased(double mouseX, double mouseY) {
        if(popups.size() == 0) {
            super.mouseReleased(mouseX, mouseY);
        } else {
            popups.get(popups.size() - 1).mouseReleased(mouseX, mouseY);
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        if(popups.size() == 0) {
            super.mouseScrolled(mouseX, mouseY, amount);
        } else {
            popups.get(popups.size() - 1).mouseScrolled(mouseX, mouseY, amount);
        }
    }
    
}
