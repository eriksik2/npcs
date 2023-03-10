package com.example.examplemod.widgets;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TabsWidget extends ModWidget {

    private ArrayList<Button> tabButtons = new ArrayList<Button>();
    private ArrayList<Component> tabName = new ArrayList<Component>();
    private ArrayList<ModWidget> tabs = new ArrayList<ModWidget>();
    private int selectedTab = 0;

    private int buttonsWidth = 0;
    private int buttonsHeight = 20;


    public TabsWidget(ModWidget parent) {
        super(parent);
    }

    public void addTab(Component name, ModWidget tab) {
        tabName.add(name);
        tabs.add(tab);
        setLayoutDirty();
    }

    @Override
    public void onRelayoutPost() {
        tabButtons.clear();
        buttonsWidth = 0;
        for(int i = 0; i < tabs.size(); i++) {
            Component name = tabName.get(i);
            ModWidget tab = tabs.get(i);
            tab.setWidth(getInnerWidth());
            tab.setHeight(getInnerHeight());
            tab.setX(getInnerX());
            tab.setY(getInnerY());

            Button tabButton = Button.builder(name, (button) -> {
                selectedTab = tabButtons.indexOf(button);
            }).build();
            int width = Minecraft.getInstance().font.width(name) + 4;
            tabButton.setWidth(width);
            tabButton.setHeight(buttonsHeight);
            tabButton.setX(getX() + buttonsWidth);
            tabButton.setY(getY());
            tabButtons.add(tabButton);
            buttonsWidth += width;
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(tabs.size() == 0) return;
        if(layoutDirty) {
            relayout();
        }
        for(int i = 0; i < tabButtons.size(); i++) {
            Button tabButton = tabButtons.get(i);
            tabButton.render(stack, mouseX, mouseY, partialTicks);
            if(i != selectedTab) {
                fill(stack, tabButton.getX(), tabButton.getY(), tabButton.getX() + tabButton.getWidth(), tabButton.getY() + tabButton.getHeight(), -1072689136);
            }
        }
        ModWidget tab = tabs.get(selectedTab);
        stack.pushPose();
        //stack.translate(getContentX(), getContentY(), 0);
        tab.render(stack, mouseX, mouseY, partialTicks);
        stack.popPose();
    }
 
    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int button) {
        if(tabs.size() == 0) return false;
        for(Button tabButton : tabButtons) {
            if(mouseX >= tabButton.getX() && mouseX <= tabButton.getX() + tabButton.getWidth()
            && mouseY >= tabButton.getY() && mouseY <= tabButton.getY() + tabButton.getHeight()) {
                if(tabButton.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        ModWidget tab = tabs.get(selectedTab);
        return tab.mousePressed(mouseX, mouseY, button);
    }
 
    @Override
    public void onMouseReleased(double mouseX, double mouseY) {
        if(tabs.size() == 0) return;
        for(Button tabButton : tabButtons) {
            if(mouseX >= tabButton.getX() && mouseX <= tabButton.getX() + tabButton.getWidth()
            && mouseY >= tabButton.getY() && mouseY <= tabButton.getY() + tabButton.getHeight()) {
                tabButton.onRelease(mouseX, mouseY);
            }
        }
        ModWidget tab = tabs.get(selectedTab);
        tab.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double amount) {
        if(tabs.size() == 0) return;
        ModWidget tab = tabs.get(selectedTab);
        tab.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public int getInnerHeight() {
        return super.getInnerHeight() - buttonsHeight;
    }

    @Override
    public int getInnerY() {
        return super.getInnerY() + buttonsHeight;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        for(ModWidget tab : tabs) {
            tab.setWidth(getInnerWidth());
        }
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        for(ModWidget tab : tabs) {
            tab.setHeight(getInnerHeight());
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        for(ModWidget tab : tabs) {
            tab.setX(getInnerX());
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        for(ModWidget tab : tabs) {
            tab.setY(getInnerY());
        }
    }
    
}
