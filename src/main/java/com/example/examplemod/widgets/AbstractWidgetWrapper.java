package com.example.examplemod.widgets;

import java.lang.reflect.InvocationTargetException;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class AbstractWidgetWrapper extends ModWidget {

    private AbstractWidget wrappedWidget;

    public AbstractWidgetWrapper(ModWidget parent, AbstractWidget wrappedWidget) {
        super(parent);
        this.wrappedWidget = wrappedWidget;
        this.setX(wrappedWidget.getX());
        this.setY(wrappedWidget.getY());
        this.wrappedWidget.setX(0);
        this.wrappedWidget.setY(0);
        this.setWidth(wrappedWidget.getWidth());
        this.setHeight(wrappedWidget.getHeight());
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        mouseX -= getGlobalX();
        mouseY -= getGlobalY();
        wrappedWidget.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        mouseX -= getGlobalX();
        mouseY -= getGlobalY();
        wrappedWidget.onClick(mouseX, mouseY);
    }
 
    @Override
    public void onRelease(double mouseX, double mouseY) {
        mouseX -= getGlobalX();
        mouseY -= getGlobalY();
        wrappedWidget.onRelease(mouseX, mouseY);
    }

    public void onScroll(double mouseX, double mouseY, double amount) {
        //wrappedWidget.onScroll(mouseX, mouseY, amount);
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
        wrappedWidget.updateNarration(p_259858_);
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
