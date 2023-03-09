package com.example.examplemod.encyclopedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Quaternionf;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.NpcDataServerToClientBroker;
import com.example.examplemod.networking.NpcTeamServerToClientBroker;
import com.example.examplemod.networking.OpenEncyclopedia;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcRenderData;
import com.example.examplemod.npc.NpcRenderer;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class EncyclopediaScreen extends AbstractContainerScreen<EncyclopediaMenu> {

    private final ResourceLocation BG = new ResourceLocation(ExampleMod.MODID, "textures/gui/encyclopedia_gui.png");

    private Player player;

    private final int CANVAS_X = 92;
    private final int CANVAS_Y = 8;
    private final int CANVAS_W = 178;
    private final int CANVAS_H = 172;

    private final int CANVAS_ITEM_SIZE = 14;
    private final int CANVAS_LINE_WIDTH = 1;

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

    // Canvas offset smooth scroll animation
    private boolean canvasOffsetAnim = false;
    private int canvasOffsetXTarget = 0;
    private int canvasOffsetYTarget = 0;
    private int canvasOffsetXOld = 0;
    private int canvasOffsetYOld = 0;
    private int canvasOffsetAnimDurationMs = 500;
    private long canvasOffsetAnimStartTime = 0;
    // End of canvas offset smooth scroll animation

    // Persistent canvas visuals
    private boolean keepPersistentData = false;
    private static int canvasOffsetNewCenterX = 0;
    private static int canvasOffsetNewCenterY = 0;
    private static final HashMap<Integer, Integer> persistentLocationsX = new HashMap<Integer, Integer>();
    private static final HashMap<Integer, Integer> persistentLocationsY = new HashMap<Integer, Integer>();
    // End of persistent canvas visuals

    private int selectedNpcId = 0;
    private NpcData selectedNpcData = null;
    private NpcTeam selectedNpcTeam = null;

    private ArrayList<Integer> relatedNpcIds = new ArrayList<Integer>();
    private ArrayList<Integer> relatedX = new ArrayList<Integer>();
    private ArrayList<Integer> relatedY = new ArrayList<Integer>();

    private NpcDataServerToClientBroker npcDataBroker = Registration.NPC_DATA_BROKER.get();
    private NpcTeamServerToClientBroker npcTeamBroker = Registration.NPC_TEAM_BROKER.get();

    public EncyclopediaScreen(EncyclopediaMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        player = inv.player;
        imageWidth = 276;
        imageHeight = 188;
        canvasOffsetX = canvasOffsetNewCenterX;
        canvasOffsetY = canvasOffsetNewCenterY;
        smoothScrollCanvas(0, 0);
    }

    @Override
    protected void init() {
        super.init();
        canvasOffsetX = -canvasOffsetNewCenterX;
        canvasOffsetY = -canvasOffsetNewCenterY;
        //canvasOffsetNewCenterX = 0;
        //canvasOffsetNewCenterY = 0;
        smoothScrollCanvas(0, 0);
    }

    private void recalculateInfo() {
        selectedNpcData = npcDataBroker.get(selectedNpcId);
        if(selectedNpcData != null && selectedNpcData.teamId != null) {
            selectedNpcTeam = npcTeamBroker.get(selectedNpcData.teamId);
        } else {
            selectedNpcTeam = null;
        }
        if(relatedNpcIds.size() != 0) return;

        if(selectedNpcTeam != null) {
            ArrayList<Integer> members = new ArrayList<Integer>(selectedNpcTeam.getNpcIds());
            for(int i = members.size() - 1; i >= 0; i--) {
                int npcId = members.get(i);
                if(npcId == selectedNpcId) continue;
                Integer relativeX = persistentLocationsX.get(npcId);
                Integer relativeY = persistentLocationsY.get(npcId);
                if(relativeX == null) continue;

                relatedNpcIds.add(npcId);
                relatedX.add(relativeX);
                relatedY.add(relativeY);
                members.removeIf(id -> id == npcId);
            }
            for(int i = 0; i < members.size(); i++) {
                int npcId = members.get(i);
                if(npcId == selectedNpcId) continue;
                relatedNpcIds.add(npcId);
                float angle = (float) (i * 2 * Math.PI / members.size());
                relatedX.add((int) (Math.cos(angle) * 50));
                relatedY.add((int) (Math.sin(angle) * 50));
            }
        }
    }

    private void invalidateInfo() {
        relatedNpcIds.clear();
        relatedY.clear();
    }

    private void tickCanvasOffsetAnim(){
        if(!canvasOffsetAnim) return;
        long time = System.currentTimeMillis() - canvasOffsetAnimStartTime;
        if(time >= canvasOffsetAnimDurationMs) {
            canvasOffsetAnim = false;
            canvasOffsetX = canvasOffsetXTarget;
            canvasOffsetY = canvasOffsetYTarget;
            return;
        }
        float t = ((float)time) / ((float)canvasOffsetAnimDurationMs);
        t = 1-(float)Math.pow(1-t, 2);
        canvasOffsetX = (int) (canvasOffsetXOld + (canvasOffsetXTarget - canvasOffsetXOld) * t);
        canvasOffsetY = (int) (canvasOffsetYOld + (canvasOffsetYTarget - canvasOffsetYOld) * t);
    }

    public void smoothScrollCanvas(int x, int y) {
        canvasOffsetAnim = true;
        canvasOffsetXOld = canvasOffsetX;
        canvasOffsetYOld = canvasOffsetY;
        canvasOffsetXTarget = x;
        canvasOffsetYTarget = y;
        canvasOffsetAnimStartTime = System.currentTimeMillis();
    }

    private void storePersistentData(int relativeToIndex) {
        keepPersistentData = true;
        int relativeX = relatedX.get(relativeToIndex);
        int relativeY = relatedY.get(relativeToIndex);
        canvasOffsetNewCenterX = -canvasOffsetX - relativeX;
        canvasOffsetNewCenterY = -canvasOffsetY - relativeY;
        persistentLocationsX.clear();
        persistentLocationsY.clear();
        for(int i = 0; i < relatedNpcIds.size(); i++) {
            int npcId = relatedNpcIds.get(i);
            if(npcId == relatedNpcIds.get(relativeToIndex)) continue;
            persistentLocationsX.put(npcId, relatedX.get(i) - relativeX);
            persistentLocationsY.put(npcId, relatedY.get(i) - relativeY);
        }
        persistentLocationsX.put(selectedNpcId, 0 - relativeX);
        persistentLocationsY.put(selectedNpcId, 0 - relativeY);
    }

    private void clearPersistentData() {
        persistentLocationsX.clear();
        persistentLocationsY.clear();
        canvasOffsetNewCenterX = 0;
        canvasOffsetNewCenterY = 0;
    }

    @Override
    public void onClose() {
        if(!keepPersistentData) {
            clearPersistentData();
        }
        super.onClose();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int newId = menu.npcIdSlot.get();
        if(newId != selectedNpcId) {
            selectedNpcId = newId;
            invalidateInfo();
        }
        recalculateInfo();
        tickCanvasOffsetAnim();
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
        int mouseXInCanvas = mouseX - leftPos - CANVAS_X - CANVAS_W/2 - canvasOffsetX;
        int mouseYInCanvas = mouseY - topPos - CANVAS_Y - CANVAS_H/2 - canvasOffsetY;

        for(int i = 0; i < relatedNpcIds.size(); i++) {
            // Render lines
            double x = relatedX.get(i) - CANVAS_LINE_WIDTH/2;
            double y = relatedY.get(i) - CANVAS_LINE_WIDTH/2;
            double dist =Math.sqrt(x*x + y*y);

            matrixStack.pushPose();
            matrixStack.translate(CANVAS_ITEM_SIZE/2, CANVAS_ITEM_SIZE/2, 0);
            Quaternionf q = new Quaternionf();
            double angle = Math.asin(y/dist);
            if(x < 0) angle = Math.PI - angle;
            q.rotateAxis((float)angle, 0, 0, 1);
            matrixStack.mulPose(q);
            fillGradient(matrixStack, 0, -CANVAS_LINE_WIDTH/2, (int)dist, CANVAS_LINE_WIDTH, -1072689136, -804253680);
            matrixStack.popPose();
            renderCanvasItem(matrixStack, mouseXInCanvas, mouseYInCanvas, i);
        }

        if(selectedNpcData != null) {
            ResourceLocation texture = NpcRenderer.getTextureLocation(new NpcRenderData(selectedNpcData));
            matrixStack.pushPose();
            float scaleX = (float)CANVAS_ITEM_SIZE/8;
            float scaleY = (float)CANVAS_ITEM_SIZE/8;
            matrixStack.scale(scaleX, scaleY, 0);
            RenderSystem.setShaderTexture(0, texture);
            blit(matrixStack, 0, 0, getBlitOffset(), 8, 8, 8, 8, 64, 32);
            matrixStack.popPose();
        }

        matrixStack.popPose();
        disableScissor();
    }

    private void renderCanvasItem(PoseStack matrixStack, int mouseX, int mouseY, int index) {
        Font font = Minecraft.getInstance().font;
        int id = relatedNpcIds.get(index);
        NpcData data = npcDataBroker.get(id);
        if(data == null) return; // Not loaded yet
        int x = relatedX.get(index);
        int y = relatedY.get(index);
        int w = CANVAS_ITEM_SIZE;
        int h = CANVAS_ITEM_SIZE;
        int color = 0x00FF00;
        if(id == selectedNpcId) {
            color = 0xFF0000;
        }
        if(mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h) {
            color = 0x0000FF;
        }
        renderNpcFace(matrixStack, x, y, CANVAS_ITEM_SIZE, new NpcRenderData(data));
        //matrixStack.pushPose();
        //matrixStack.translate(x, y, 0);
        //fillGradient(matrixStack, 0, 0, w, h, -1072689136, -804253680);
        //font.draw(matrixStack, String.valueOf(id), 0, 0, 0xFFFFFF);
        //matrixStack.popPose();
    }

    private void renderNpcFace(PoseStack matrixStack, int x, int y, float scale, NpcRenderData renderData) {
        ResourceLocation texture = NpcRenderer.getTextureLocation(renderData);
        matrixStack.pushPose();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(scale/8, scale/8, 1);
        RenderSystem.setShaderTexture(0, texture);
        blit(matrixStack, 0, 0, getBlitOffset(), 8, 8, 8, 8, 64, 32);
        matrixStack.popPose();
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
            if(data != null) {
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

    public boolean mouseClickedCanvasItem(double mouseX, double mouseY, int button, int index) {
        if(button != 0) return false;
        storePersistentData(index);
        OpenEncyclopedia message = new OpenEncyclopedia(relatedNpcIds.get(index));
        Messages.sendToServer(message);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseInCanvas(mouseX, mouseY)) {
            for(int i = 0; i < relatedNpcIds.size(); i++) {
                if(mouseInCanvasItem(mouseX, mouseY, i)) {
                    if(mouseClickedCanvasItem(mouseX, mouseY, button, i)) return true;
                    break;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean mouseInCanvas(double mouseX, double mouseY) {
        return mouseX >= leftPos + CANVAS_X && mouseX < leftPos + CANVAS_X + CANVAS_W
        && mouseY >= topPos + CANVAS_Y && mouseY < topPos + CANVAS_Y + CANVAS_H;
    }

    private boolean mouseInCanvasItem(double mouseX, double mouseY, int index) {
        double mouseXInCanvas = mouseX - leftPos - CANVAS_X - CANVAS_W/2 - canvasOffsetX;
        double mouseYInCanvas = mouseY - topPos - CANVAS_Y - CANVAS_H/2 - canvasOffsetY;
        int x = relatedX.get(index);
        int y = relatedY.get(index);
        int w = CANVAS_ITEM_SIZE;
        int h = CANVAS_ITEM_SIZE;
        return mouseInCanvas(mouseX, mouseY) && mouseXInCanvas >= x && mouseXInCanvas <= x+w && mouseYInCanvas >= y && mouseYInCanvas <= y+h;
    }
    
}
