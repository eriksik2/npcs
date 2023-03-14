package com.example.examplemod.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;

public class TextureWidget extends ModWidget {

    private ResourceLocation texture;
    private Integer textureW = null;
    private Integer textureH = null;
    private Integer textureBlitX = null;
    private Integer textureBlitY = null;
    private Integer textureBlitW = null;
    private Integer textureBlitH = null;
    
    public TextureWidget(ModWidget parent) {
        super(parent);
    }
    
    public void setTexture(ResourceLocation texture, int w, int h) {
        this.texture = texture;
        this.textureW = w;
        this.textureH = h;
    }

    public void setTextureBlit(int x, int y, int w, int h) {
        this.textureBlitX = x;
        this.textureBlitY = y;
        this.textureBlitW = w;
        this.textureBlitH = h;
    }

    private int getTextureBlitX() {
        if(textureBlitX == null) return 0;
        return textureBlitX;
    }

    private int getTextureBlitY() {
        if(textureBlitY == null) return 0;
        return textureBlitY;
    }

    private int getTextureBlitW() {
        if(textureBlitW == null) return textureW - getTextureBlitX();
        return textureBlitW;
    }

    private int getTextureBlitH() {
        if(textureBlitH == null) return textureH - getTextureBlitY();
        return textureBlitH;
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(texture == null) return;
        float scaleX = (float)getInnerWidth() / (float)getTextureBlitW();
        float scaleY = (float)getInnerHeight() / (float)getTextureBlitH();
        stack.pushPose();
        stack.scale(scaleX, scaleY, 1);
        RenderSystem.setShaderTexture(0, texture);
        blit(stack, 0, 0, getBlitOffset(), getTextureBlitX(), getTextureBlitY(), getTextureBlitW(), getTextureBlitH(), textureW, textureH);
        stack.popPose();
    }
}
