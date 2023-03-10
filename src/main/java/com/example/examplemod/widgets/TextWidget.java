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
        this.setHeight(font.lineHeight);
    }

    public TextWidget(ModWidget parent, String text) {
        this(parent, Component.literal(text));
    }

    public void setFont(Font font) {
        this.font = font;
        setLayoutDirty();
    }

    public Font getFont() {
        return font;
    }

    public void setText(Component text) {
        this.text = text;
        setLayoutDirty();
    }
    
    public void setText(String text) {
        setText(Component.literal(text));
    }

    public Component getText() {
        return text;
    }

    @Override
    public int getHeight() {
        if(getWrap()) {
            return getFont().wordWrapHeight(getText(), getWidth());
        } else {
            return getFont().lineHeight;
        }
    }

    @Override
    public int getWidth() {
        if(getWrap()) {
            return super.getWidth();
        } else {
            return getFont().width(getText());
        }
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
        setLayoutDirty();
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
        //if(wrap) {
        //    int textHeight = getFont().wordWrapHeight(getText(), getWidth());
        //    int lines = textHeight / getFont().lineHeight;
        //    setHeight(textHeight - lines);
        //} else {
        //    setWidth(getFont().width(getText()));
        //    setHeight(getFont().lineHeight - 1);
        //}
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(getWrap()) {
            getFont().drawWordWrap(getText(), getGlobalX(), getGlobalY(), getWidth(), getColor());
        } else {
            getFont().draw(stack, getText(), 0, 0, getColor());
        }
    }
}
