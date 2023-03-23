package com.example.examplemod.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;

public class DrawerWidget extends ModWidget {

    private ModWidget header;
    private TextWidget headerArrow;
    private ModWidget headerContent;
    private ModWidget content;

    private int contentHeight = 0;
    private float height = 0;
    private int targetHeight = 0;
    private boolean open = false;

    public DrawerWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        header = super.addChild(new ModWidget(null) {
            @Override
            public void onRelayoutPre() {
                layoutFillX();
                headerContent.setWidth(getInnerWidth() - headerArrow.getWidth() - 5);
                headerContent.setX(headerArrow.getWidth() + 5);
            }
            @Override
            public void onRelayoutPost() {
                layoutShrinkwrapChildren();
                headerArrow.layoutCenterY();
            }

            @Override
            public boolean onMousePressed(double mouseX, double mouseY, int button) {
                if(!isMouseOver(mouseX, mouseY)) return false;
                setOpen(!open);
                return true;
            }
        });
        setHeader(new ModWidget(null));
        headerArrow = new TextWidget(header, ">");
        content = super.addChild(new ModWidget(null) {
            @Override
            public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
                enableScissor(getGlobalX(), getGlobalY(), getGlobalX()+getInnerWidth(), getGlobalY()+getInnerHeight());
                super.render(stack, mouseX, mouseY, partialTicks);
                disableScissor();
            }
        });
    }

    @Override
    public void onRelayoutPre() {
        header.setWidth(getInnerWidth());
        content.setWidth(getInnerWidth());
    }

    @Override
    public void onRelayoutPost() {
        content.setY(header.getHeight());
        content.layoutShrinkwrapChildren();
        contentHeight = content.getHeight();
        content.setX(10);
        content.setWidth(getInnerWidth() - 10);
        content.setHeight(Math.round(height));
        layoutShrinkwrapChildren();
    }

    @Override
    public void onTick() {
        if(targetHeight != height) {
            if(Math.abs(targetHeight - height) < 1) {
                height = targetHeight;
            } else {
                height += (targetHeight - height) * 0.20f;
            }
            setLayoutDirty();
        }
        super.onTick();
    }

    public void setOpen(boolean open) {
        if(open) {
            targetHeight = contentHeight;
            headerArrow.setText("v");
            this.open = true;
        } else {
            targetHeight = 0;
            headerArrow.setText(">");
            this.open = false;
        }
    }
    
    public void setHeader(ModWidget headerContent) {
        if(this.headerContent != null) {
            this.headerContent.deinit();
        }
        this.headerContent = header.addChild(headerContent);
    }

    @Override
    public <T extends ModWidget> T addChild(T child) {
        return content.addChild(child);
    }

    @Override
    public <T extends AbstractWidget> AbstractWidgetWrapper<T> addChild(T child) {
        return content.addChild(child);
    }

    @Override
    public void clearChildren() {
        content.clearChildren();
    }
    
}
