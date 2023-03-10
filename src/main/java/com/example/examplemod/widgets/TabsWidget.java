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

    private int buttonsWidth = 0;
    private int buttonsHeight = 20;


    public TabsWidget(ModWidget parent) {
        super(parent);
    }

    public void addTab(Component name, ModWidget tab) {
        tabName.add(name);
        if(tabs.size() == 0) tab.setActive(true);
        else tab.setActive(false);
        tabs.add(tab);
        setLayoutDirty();
    }

    @Override
    public void onRelayoutPre() {
        tabButtons.clear();
        clearChildren();
        buttonsWidth = 0;
        for(int i = 0; i < tabs.size(); i++) {
            Component name = tabName.get(i);
            ModWidget tab = tabs.get(i);
            addChild(tab);
            tab.setWidth(getInnerWidth());
            tab.setHeight(getInnerHeight() - buttonsHeight);
            tab.setX(getInnerX());
            tab.setY(getInnerY() + buttonsHeight);

            Button mcButton = Button.builder(name, (button) -> {
                int bindex = tabButtons.indexOf(button);
                for(int j = 0; j < tabs.size(); j++) {
                    tabs.get(j).setActive(false);
                }
                tabs.get(bindex).setActive(true);
            }).build();
            tabButtons.add(mcButton);
            ModWidget tabButton = addChild(mcButton);
            int width = Minecraft.getInstance().font.width(name) + 4;
            tabButton.setWidth(width);
            tabButton.setHeight(buttonsHeight);
            tabButton.setX(buttonsWidth);
            tabButton.setY(0);
            buttonsWidth += width;
        }
    }
    
}
