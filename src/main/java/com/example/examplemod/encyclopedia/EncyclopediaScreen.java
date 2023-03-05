package com.example.examplemod.encyclopedia;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.networking.GetNpcData;
import com.example.examplemod.networking.Messages;
import com.example.examplemod.npc.ClientNpcData;
import com.example.examplemod.npc.ClientNpcTeam;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcTeam;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;

public class EncyclopediaScreen extends AbstractContainerScreen<EncyclopediaMenu> {

    private final ResourceLocation BG = new ResourceLocation(ExampleMod.MODID, "textures/gui/encyclopedia_gui.png");

    private Player player;

    private final int CANVAS_X = 92;
    private final int CANVAS_Y = 8;
    private final int CANVAS_W = 178;
    private final int CANVAS_H = 172;

    private final int PROFILE_X = 10;
    private final int PROFILE_Y = 11;
    private final int PROFILE_W = 72;
    private final int PROFILE_H = 43;

    private final int INFO_X = 8;
    private final int INFO_Y = 58;
    private final int INFO_W = 78;
    private final int INFO_H = 123;

    private final int INFO_ITEM_H = 10;
    private final int INFO_ITEM_GAP = 10;

    private int canvasOffsetX = 0;
    private int canvasOffsetY = 0;

    private int selectedNpcId = 0;
    private NpcData selectedNpcData = null;
    private NpcTeam selectedNpcTeam = null;

    private ArrayList<Integer> relatedNpcIds = new ArrayList<Integer>();
    private ArrayList<Integer> relatedY = new ArrayList<Integer>();

    public EncyclopediaScreen(EncyclopediaMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        player = inv.player;
        imageWidth = 276;
        imageHeight = 188;
    }

    private void recalculateInfo() {
        selectedNpcData = ClientNpcData.get(selectedNpcId);
        if(selectedNpcData != null && selectedNpcData.teamId != null) {
            selectedNpcTeam = ClientNpcTeam.get(selectedNpcData.teamId);
        } else {
            selectedNpcTeam = null;
        }
        if(relatedNpcIds.size() != 0) return;

        if(selectedNpcTeam != null) {
            List<Integer> members = selectedNpcTeam.getNpcIds();
            for(int i = 0; i < members.size(); i++) {
                relatedNpcIds.add(members.get(i));
                relatedY.add(i * 10);
            }
        }
    }

    private void invalidateInfo() {
        relatedNpcIds.clear();
        relatedY.clear();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int newId = menu.npcIdSlot.get();
        if(newId != selectedNpcId) {
            selectedNpcId = newId;
            invalidateInfo();
        }
        recalculateInfo();
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderProfile(matrixStack, mouseX, mouseY);
        renderCanvas(matrixStack, mouseX, mouseY);
        renderInfo(matrixStack, mouseX, mouseY);
        renderLabels(matrixStack, mouseX, mouseY);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBackground(matrixStack);
        RenderSystem.setShaderTexture(0, BG);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        blit(matrixStack, relX, relY, getBlitOffset(), 0, 0, this.imageWidth, this.imageHeight, 512, 256);
    }

    private void renderCanvas(PoseStack matrixStack, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        enableScissor(leftPos + CANVAS_X, topPos + CANVAS_Y, leftPos + CANVAS_X+CANVAS_W, topPos + CANVAS_Y+CANVAS_H);
        matrixStack.pushPose();
        matrixStack.translate(leftPos + CANVAS_X+CANVAS_W/2, topPos + CANVAS_Y+CANVAS_H/2, 0);
        matrixStack.translate(canvasOffsetX, canvasOffsetY, 0);

        font.draw(matrixStack, "Hello World!", 0, 0, 0xFFFFFF);
        matrixStack.popPose();
        disableScissor();
    }

    private void renderProfile(PoseStack matrixStack, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        // Draw face texture of selected entity.
        int face_scale = 5;
        if(selectedNpcId == -1) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(player.getUUID());
            ResourceLocation skin = info.getSkinLocation();
            RenderSystem.setShaderTexture(0, skin);
            matrixStack.pushPose();
            matrixStack.translate(leftPos + PROFILE_X, topPos + PROFILE_Y, 0);
            matrixStack.scale(face_scale, face_scale, 1);
            blit(matrixStack, 0, 0, getBlitOffset(), 8, 8, 8, 8, 64, 64);
            matrixStack.popPose();

            font.draw(matrixStack, player.getName().getString(), leftPos + PROFILE_X+8*face_scale + 2, topPos + PROFILE_Y, 0xFFFFFF);
        } else {
            NpcData data = selectedNpcData;
            if(data == null) {
                font.draw(matrixStack, "Loading", leftPos + PROFILE_X+8*face_scale + 2, topPos + PROFILE_Y, 0xFFFFFF);
            } else {
                font.draw(matrixStack, data.name, leftPos + PROFILE_X+8*face_scale + 2, topPos + PROFILE_Y, 0xFFFFFF);
            }
        }
    }

    private void renderInfo(PoseStack matrixStack, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        font.draw(matrixStack, "Mouse: " + mouseX + ", " + mouseY, 0, 0, 0xFFFFFF);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && mouseInCanvas(mouseX, mouseY)) {
            canvasOffsetX += deltaX;
            canvasOffsetY += deltaY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean mouseInCanvas(double mouseX, double mouseY) {
        return mouseX >= leftPos + CANVAS_X && mouseX < leftPos + CANVAS_X + CANVAS_W
        && mouseY >= topPos + CANVAS_Y && mouseY < topPos + CANVAS_Y + CANVAS_H;
    }
    
}
