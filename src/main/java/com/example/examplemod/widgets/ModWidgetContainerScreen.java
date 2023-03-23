package com.example.examplemod.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ModWidgetContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private ModWidget debug;
    private ModWidget root;

    public ModWidgetContainerScreen(T menu, Inventory inv, Component title) {
        super(menu, inv, title);
        root = new ModWidget(null);
    }

    public void toggleDebug() {
        if(debug == null) {
            debug = new DebugWidget(null, root);
            debug.setPosition(0, 0);
            debug.setSize(width/2, height);
        } else {
            debug.deinit();
            debug = null;
        }
    }

    public ModWidget addWidget(ModWidget widget) {
        return root.addChild(widget);
    }

    public ModWidget addWidget(AbstractWidget widget) {
        return root.addChild(widget);
    }

    @Override
    protected void init() {
        super.init();
        root.clearChildren();
        registerWidgets(root);
        root.setSize(width, height);
        root.init();
        onInit();
    }

    protected void onInit() {

    }

    protected void registerWidgets(ModWidget root) {

    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(debug != null) debug.tick();
        root.tick();
        onRender(stack, mouseX, mouseY, partialTicks);
        //super.render(stack, mouseX, mouseY, partialTicks);
        root.render(stack, mouseX, mouseY, partialTicks);
        if(debug != null) debug.render(stack, mouseX, mouseY, partialTicks);
    }

    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

    }
     
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(debug != null) debug.mousePressed(mouseX, mouseY, button);
        if(onMouseClicked(mouseX, mouseY, button)) return true;
        if(root.mousePressed(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
 
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(debug != null) debug.mouseReleased(mouseX, mouseY, button);
        if(onMouseReleased(mouseX, mouseY, button)) return true;
        if(root.mouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(debug != null) debug.mouseScrolled(mouseX, mouseY, amount);
        if(onMouseScrolled(mouseX, mouseY, amount)) return true;
        if(root.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(debug != null) debug.keyPressed(keyCode, scanCode, modifiers);
        if(onKeyPressed(keyCode, scanCode, modifiers)) return true;
        if(root.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(debug != null) debug.keyReleased(keyCode, scanCode, modifiers);
        if(onKeyReleased(keyCode, scanCode, modifiers)) return true;
        if(root.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(debug != null) debug.charTyped(codePoint, modifiers);
        if(onCharTyped(codePoint, modifiers)) return true;
        if(root.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }

    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean onCharTyped(char codePoint, int modifiers) {
        return false;
    }
    
}
