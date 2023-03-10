package com.example.examplemod.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class TextWidget extends ModWidget {

    private Component text;
    private int color = 0xffffff;
    private Font font;
    private boolean wrap = false;
    
    public TextWidget(ModWidget parent, Component text) {
        super(parent);
        this.text = text;
        this.font = Minecraft.getInstance().font;
    }

    public TextWidget(ModWidget parent, String text) {
        this(parent, Component.literal(text));
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setText(Component text) {
        this.text = text;
    }
    
    public void setText(String text) {
        this.text = Component.literal(text);
    }

    public Component getText() {
        return text;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public boolean getWrap() {
        return wrap;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void onRelayoutPost() {
        if(wrap) {
            setHeight(getFont().wordWrapHeight(getText(), getWidth()));
        } else {
            setWidth(getFont().width(getText()));
            setHeight(getFont().lineHeight);
        }
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(getWrap()) {
            getFont().drawWordWrap(getText(), 0, 0, getWidth(), getColor());
        } else {
            getFont().draw(stack, getText(), 0, 0, getColor());
        }
    }
}