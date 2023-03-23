package com.example.examplemod.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

public class ButtonWidget extends ModWidget {

    private TextWidget text;
    private Runnable onClick;

    private boolean isDown = false;

    public ButtonWidget(ModWidget parent, String text) {
        super(parent);
        this.text = new TextWidget(this, text);
        this.text.setWrap(true);
    }

    public void setWrap(boolean wrap) {
        text.setWrap(wrap);
    }

    public boolean getWrap() {
        return text.getWrap();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void onClick() {
        if(onClick != null) onClick.run();
    }

    @Override
    public void onRelayoutPre() {
        text.setX(0);
        text.setWidth(getInnerWidth());
    }

    @Override
    public void onRelayoutPost() {
        setInnerHeight(text.getHeight());
        text.layoutCenterY();
        if(getInnerHeight() < text.getHeight()) {
            setInnerHeight(text.getHeight());
        }
        if(getInnerWidth() < text.getWidth()) {
            setInnerWidth(text.getWidth());
        }
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        int x = -getInnerX();
        int y = -getInnerY();
        if(isMouseOver(mouseX, mouseY)) {
            fillGradient(stack, x, y, getWidth(), getHeight(), -0x194D33, -0x7B7B7B);
        } else if(isDown) {
            fillGradient(stack, x, y, getWidth(), getHeight(), -0x7B7B7B, -0x194D33);
        } else {
            fillGradient(stack, x, y, getWidth(), getHeight(), -1072689136, -804253680);
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int button) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        if(button == 0) {
            isDown = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if(button == 0) {
            if(isDown) {
                isDown = false;
                if(isMouseOver(mouseX, mouseY)) onClick();
                return true;
            }
        }
        return false;
    }
}
