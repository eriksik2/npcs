package com.example.examplemod.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.events.GuiEventListener;

public interface ModWidgetRoot {

    public void setWidgetTree(ModWidget widgetTree);
    public ModWidget getWidgetTree();

    public default void tick() {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return;
        widgetTree.tick();
    }

    public default void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return;
        widgetTree.render(stack, mouseX, mouseY, partialTicks);
    }
    
    public default boolean mousePressed(double mouseX, double mouseY, int button) {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return false;
        return widgetTree.mousePressed(mouseX, mouseY, button);
    }
 
    public default boolean mouseReleased(double mouseX, double mouseY, int button) {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return false;
        return widgetTree.mouseReleased(mouseX, mouseY, button);
    }

    public default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return false;
        return widgetTree.mouseScrolled(mouseX, mouseY, amount);
    }

    public default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return false;
        return widgetTree.keyPressed(keyCode, scanCode, modifiers);
    }

    public default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return false;
        return widgetTree.keyReleased(keyCode, scanCode, modifiers);
    }

    public default boolean charTyped(char codePoint, int modifiers) {
        ModWidget widgetTree = getWidgetTree();
        if(widgetTree == null) return false;
        return widgetTree.charTyped(codePoint, modifiers);
    }
}
