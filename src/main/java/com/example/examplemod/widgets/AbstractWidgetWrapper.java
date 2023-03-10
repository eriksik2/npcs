package com.example.examplemod.widgets;

import java.lang.reflect.InvocationTargetException;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;

public class AbstractWidgetWrapper extends ModWidget {

    private AbstractWidget wrappedWidget;

    public AbstractWidgetWrapper(ModWidget parent, AbstractWidget wrappedWidget) {
        super(parent);
        this.wrappedWidget = wrappedWidget;
        this.setX(wrappedWidget.getX());
        this.setY(wrappedWidget.getY());
        this.wrappedWidget.setX(getGlobalX());
        this.wrappedWidget.setY(getGlobalY());
        this.setWidth(wrappedWidget.getWidth());
        this.setHeight(wrappedWidget.getHeight());
    }

    @Override
    protected void registerDebugProperties() {
        registerDebugProperty("message", () -> "\"" + wrappedWidget.getMessage().getString() + "\"");
    }

    @Override
    public String getDebugName() {
        return "Minecraft." + wrappedWidget.getClass().getSimpleName();
    }

    @Override
    public void onRelayoutPost() {
        this.wrappedWidget.setX(getGlobalX());
        this.wrappedWidget.setY(getGlobalY());
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        stack.pushPose();
        stack.translate(-getGlobalX(), -getGlobalY(), 0);
        wrappedWidget.render(stack, mouseX, mouseY, partialTicks);
        stack.popPose();
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int button) {
        return wrappedWidget.mouseClicked(mouseX, mouseY, button);
    }
 
    @Override
    public void onMouseReleased(double mouseX, double mouseY) {
        wrappedWidget.onRelease(mouseX, mouseY);
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double amount) {
        //wrappedWidget.onScroll(mouseX, mouseY, amount);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        wrappedWidget.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        wrappedWidget.setHeight(height);
    }

    @Override
    public void tick() {
        super.tick();
        Class<AbstractWidget> clazz = AbstractWidget.class;
        try {
            clazz.getMethod("tick").invoke(wrappedWidget);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            
        }
    }
}
