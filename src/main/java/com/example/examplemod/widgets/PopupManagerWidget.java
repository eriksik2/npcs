package com.example.examplemod.widgets;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

class PopupWidget extends ModWidget {

    private PopupManagerWidget manager;

    public PopupWidget(ModWidget parent, PopupManagerWidget manager) {
        super(parent);
        this.manager = manager;
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
    public void onRelayoutPost() {
        for (PopupWidget popup : popups) {
            popup.setX((getInnerWidth() - popup.getWidth()) / 2);
            popup.setY((getInnerHeight() - popup.getHeight()) / 2);
        }
    }

    public PopupWidget push(ModWidget popup) {
        PopupWidget popupWidget = new PopupWidget(null, this);
        popupWidget.addChild(popup);
        popups.add(popupWidget);
        setLayoutDirty();
        return popupWidget;
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
    public void onClick(double mouseX, double mouseY) {
        if(popups.size() == 0) {
            super.onClick(mouseX, mouseY);
        } else {
            popups.get(popups.size() - 1).onClick(mouseX, mouseY);
        }
    }
    
    @Override
    public void onRelease(double mouseX, double mouseY) {
        if(popups.size() == 0) {
            super.onRelease(mouseX, mouseY);
        } else {
            popups.get(popups.size() - 1).onRelease(mouseX, mouseY);
        }
    }

    @Override
    public void onScroll(double mouseX, double mouseY, double amount) {
        if(popups.size() == 0) {
            super.onScroll(mouseX, mouseY, amount);
        } else {
            popups.get(popups.size() - 1).onScroll(mouseX, mouseY, amount);
        }
    }
    
}
