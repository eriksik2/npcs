package com.example.examplemod.npc;

import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.networking.AddNpcToPlayerTeam;
import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.NpcDataServerToClientBroker;
import com.example.examplemod.networking.NpcTeamServerToClientBroker;
import com.example.examplemod.networking.OpenEncyclopedia;
import com.example.examplemod.networking.ToggleTrackingNpc;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.tracking.ClientTrackedObjects;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;

public class NpcInteractScreen extends AbstractContainerScreen<NpcInteractMenu> {
    static final int NAME_X = 7;
    static final int NAME_Y = 7;

    static final int TRACK_BUTTON_X = 55;
    static final int TRACK_BUTTON_Y = 4;
    static final int TRACK_BUTTON_PADDING_X1 = 4;
    static final int TRACK_BUTTON_PADDING_X2 = 9;
    static final int TRACK_BUTTON_PADDING_Y = 3;
    boolean mousePressedTrackButton = false;


    static final int SCROLLBAR_W = 7;

    static final int TEXT_X = 8;
    static final int TEXT_Y = 22;
    static final int TEXT_W = 90;
    static final int TEXT_H = 156;

    static final int OPTIONS_X = 108;
    static final int OPTIONS_Y = 106;
    static final int OPTIONS_W = 153;
    static final int OPTIONS_H = 52;
    static final int OPTIONS_PADDING = 1;
    static final int OPTIONS_GAP = 2;

    static final int OPTION_PADDING = 2;

    static final String[] toptions = {"Ask him to join you.", "Ask if hes from a group.", "Ask about..."};
    private final ArrayList<String> options = new ArrayList<String>(List.of(toptions));
    private int[] optionY;
    private int[] optionH;
    private int optionsTotalH;
    private int optionsScrollbarH;
    private int optionsScrollbarY;
    private float optionsScrollFactor = 0;

    private String displayText = "Hi!";

    private final ResourceLocation GUI = new ResourceLocation(ExampleMod.MODID, "textures/gui/npc_interact_gui.png");

    private final NpcScreenRandomLookHelper lookHelper;

    private NpcData npcData;

    private NpcTeamServerToClientBroker npcTeamBroker = Registration.NPC_TEAM_BROKER.get();

    public NpcInteractScreen(NpcInteractMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.imageWidth = 276;
        this.imageHeight = 188;
        lookHelper = new NpcScreenRandomLookHelper(this.width, this.height);
    }

    private void recalculateOptionsOffsets() {
        int optionWidth = OPTIONS_W - 2*OPTIONS_PADDING;
        optionY = new int[options.size()];
        optionH = new int[options.size()];
        optionsTotalH = 0;
        for(int i = 0; i < options.size(); ++i) {
            String text = options.get(i);
            optionY[i] = OPTIONS_Y + OPTIONS_PADDING + optionsTotalH;
            optionH[i] = optionHeight(text, optionWidth);
            optionsTotalH += optionH[i];
            if(i < options.size() - 1) optionsTotalH += OPTIONS_GAP;
            else optionsTotalH += OPTIONS_PADDING*2;
        }

        if(optionsTotalH > OPTIONS_H) {
            optionsScrollbarY = OPTIONS_Y + (int)Math.floor(optionsScrollFactor*(OPTIONS_H - optionsScrollbarH));
            optionsScrollbarH = 20;
            int scrollOffset = (int)Math.floor(optionsScrollFactor*(optionsTotalH - OPTIONS_H));
            for(int i = 0; i < options.size(); ++i) {
                optionY[i] = optionY[i] - scrollOffset;
            }
        } else {
            optionsScrollbarY = OPTIONS_Y;
            optionsScrollbarH = OPTIONS_H;
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        npcData = menu.getNpcData();
        lookHelper.tick();
        recalculateOptionsOffsets();
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        if(npcData == null) {
            drawString(matrixStack, font, "NpcData is not synced to the client.", 7, 7, 0xffffff);
            return;
        }
        String npcName = npcData.name;
        font.draw(matrixStack, npcName, NAME_X, NAME_Y, 0x000000);
        if(mouseInName(mouseX, mouseY)) {
            hLine(matrixStack, NAME_X, NAME_X + font.width(npcName), NAME_Y + font.lineHeight, -1072689136);
        }

        renderTrackButton(matrixStack, mouseX, mouseY);
        renderTextbox(matrixStack, mouseX, mouseY);
        renderOptions(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        NpcEntity npcEntity = menu.getEntity();
        int i = this.leftPos;
        int j = this.topPos;
        Font font = Minecraft.getInstance().font;
        font.draw(matrixStack, "c", (float)(i), (float)(j), 0xff0000);
        font.draw(matrixStack, "X", lookHelper.getX(), lookHelper.getY(), 0xff0000);
        enableScissor(i + 108, j + 0, i + 108 + 164, j + 0 + 98);
        renderEntityInInventory(i + 190, j + 160, 80, lookHelper.getX(), lookHelper.getY(), npcEntity);
        disableScissor();
        
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        blit(matrixStack, relX, relY, getBlitOffset(), 0, 0, this.imageWidth, this.imageHeight, 512, 256);
    }

    private void renderTextbox(PoseStack matrixStack, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        font.drawWordWrap(FormattedText.of(displayText), TEXT_X, TEXT_Y, TEXT_W, 0xffffff);
    }

    private void renderOptions(PoseStack stack, int mouseX, int mouseY) {
        enableScissor(leftPos + OPTIONS_X, topPos + OPTIONS_Y, leftPos + OPTIONS_X + OPTIONS_W + SCROLLBAR_W, topPos + OPTIONS_Y + OPTIONS_H);
        int optionWidth = OPTIONS_W - 2*OPTIONS_PADDING;
        int optionX = OPTIONS_X + OPTIONS_PADDING;
        int scrollbarX = OPTIONS_X + 1 + OPTIONS_W;
        fillGradient(stack, scrollbarX, optionsScrollbarY, scrollbarX + SCROLLBAR_W, optionsScrollbarY + optionsScrollbarH, -1072689136, -804253680);
        for(int i = 0; i < options.size(); ++i) {
            String text = options.get(i);
            renderOption(stack, mouseX, mouseY, text, optionX, optionY[i], optionWidth);
        }
        disableScissor();
    }

    private int optionHeight(String text, int width) {
        int textWidth = width - 2*OPTION_PADDING;
        int textHeight = font.wordWrapHeight(text, textWidth);
        int height = textHeight + 2*OPTION_PADDING;
        return height;

    }
    private int renderOption(PoseStack stack, int mouseX, int mouseY, String text, int x, int y, int width) {
        Font font = Minecraft.getInstance().font;
        int textX = x + OPTION_PADDING;
        int textY = y + OPTION_PADDING;
        int textWidth = width - 2*OPTION_PADDING;
        int height = optionHeight(text, width);

        boolean mouseHover = mouseInOptions(mouseX, mouseY) && mouseX >= leftPos + x && mouseX < leftPos + x + width
            && mouseY >= topPos + y && mouseY < topPos + y + height;

        if(mouseHover) {
            fillGradient(stack, x, y, x+width, y+height, -0x194D33, -0x7B7B7B);
        } else {
            fillGradient(stack, x, y, x+width, y+height, -1072689136, -804253680);
        }
        font.drawWordWrap(FormattedText.of(text), textX, textY, textWidth, 0xffffff);
        return height;
    }

    private void renderTrackButton(PoseStack stack, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        if(npcData == null) return;
        int trackX = TRACK_BUTTON_X;
        int trackY = TRACK_BUTTON_Y;
        int trackH = 13;
        int trackW = 46;
        ResourceLocation trackButton = new ResourceLocation(ExampleMod.MODID, "textures/gui/button.png");
        if(mousePressedTrackButton) {
            RenderSystem.setShaderTexture(0, trackButton);
            blit(stack, trackX, trackY, getBlitOffset(), 0, 13, 46, 13, 92, 26);
            //fillGradient(stack, trackX, trackY, trackX+trackW, trackY+trackH, -0x194D33, -0x7B7B7B);
        } else {
            RenderSystem.setShaderTexture(0, trackButton);
            blit(stack, trackX, trackY, getBlitOffset(), 0, 0, 46, 13, 92, 26);
            //fillGradient(stack, trackX, trackY, trackX+trackW, trackY+trackH, -1072689136, -804253680);
        }
        boolean isUntrack = npcData != null && ClientTrackedObjects.isTracked(npcData.npcId);
        String text = isUntrack ? "Untrack" : "Track";
        int padding = isUntrack ? TRACK_BUTTON_PADDING_X1 : TRACK_BUTTON_PADDING_X2;
        int textX = trackX + padding;
        font.draw(stack, text, textX, trackY + TRACK_BUTTON_PADDING_Y, 0xffffff);
    }

    private boolean mouseInTrackButton(double mouseX, double mouseY) {
        if(npcData == null) return false;
        int trackX = TRACK_BUTTON_X;
        int trackY = TRACK_BUTTON_Y;
        int trackH = 13;
        int trackW = 46;
        return mouseX >= leftPos + trackX && mouseX < leftPos + trackX + trackW
        && mouseY >= topPos + trackY && mouseY < topPos + trackY + trackH;
    }

    private boolean mouseInName(double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        if(npcData == null) return false;
        int nameX = NAME_X;
        int nameY = NAME_Y;
        int nameW = font.width(FormattedText.of(npcData.name));
        int nameH = font.lineHeight;
        return mouseX >= leftPos + nameX && mouseX < leftPos + nameX + nameW
        && mouseY >= topPos + nameY && mouseY < topPos + nameY + nameH;
    }

    private boolean mouseInTextbox(double mouseX, double mouseY) {
        return mouseX >= leftPos + TEXT_X && mouseX < leftPos + TEXT_X + TEXT_W
        && mouseY >= topPos + TEXT_Y && mouseY < topPos + TEXT_Y + TEXT_H;
    }

    private boolean mouseInOptions(double mouseX, double mouseY) {
        return mouseX >= leftPos + OPTIONS_X && mouseX < leftPos + OPTIONS_X + OPTIONS_W
        && mouseY >= topPos + OPTIONS_Y && mouseY < topPos + OPTIONS_Y + OPTIONS_H;
    }

    private boolean mouseInOption(double mouseX, double mouseY, int i) {
        if(i < 0 || i >= options.size()) return false;
        int optX = OPTIONS_X + OPTIONS_PADDING;
        int optW = OPTIONS_W - 2*OPTIONS_PADDING;
        int optY = optionY[i];
        int optH = optionH[i];
        return mouseInOptions(mouseX, mouseY) && mouseX >= leftPos + optX && mouseX < leftPos + optX + optW
        && mouseY >= topPos + optY && mouseY < topPos + optY + optH;
    }

    private void clickedOption(int index) {
        if(index == 0) {
            AddNpcToPlayerTeam msg = new AddNpcToPlayerTeam(menu.getNpcId());
            Messages.sendToServer(msg);
            displayText = "I'd love to join you!";
        } else if(index == 1) {
            if(npcData == null) return;
            if(npcData.teamId == null){
                displayText = "No, I'm on my own.";
            } else {
                npcTeamBroker.get(npcData.teamId, (team) -> {
                    displayText = "Yes, I'm with " + team.getName();
                });
                //displayText = "Yes, I'm with group " + npcData.teamId;
            }
        }
    }

    private void clickedTrackButton() {
        if(npcData == null) return;
        ToggleTrackingNpc message = new ToggleTrackingNpc(npcData.npcId);
        Messages.sendToServer(message);
    }

    private void clickedName() {
        if(npcData == null) return;
        OpenEncyclopedia message = new OpenEncyclopedia(npcData.npcId);
        Messages.sendToServer(message);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseInTrackButton(mouseX, mouseY)) {
            mousePressedTrackButton = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mousePressedTrackButton = false;
        if(mouseInOptions(mouseX, mouseY)) {
            for(int i = 0; i < options.size(); ++i) {
                if(mouseInOption(mouseX, mouseY, i)) {
                    clickedOption(i);
                    return true;
                }
            }
        } else if(mouseInTrackButton(mouseX, mouseY)) {
            clickedTrackButton();
        } else if(mouseInName(mouseX, mouseY)) {
            clickedName();
        } else if(mouseInTextbox(mouseX, mouseY)) {
            displayText = "";
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(mouseInOptions(mouseX, mouseY)) {
            optionsScrollFactor += -amount*0.15;
            if(optionsScrollFactor > 1) optionsScrollFactor = 1;
            if(optionsScrollFactor < 0) optionsScrollFactor = 0;
            return true;
        }
        return false;
    }

    public static void renderEntityInInventory(int p_98851_, int p_98852_, int p_98853_, float p_98854_, float p_98855_, LivingEntity p_98856_) {
        if (p_98856_ == null) return;
        float f = (float)Math.atan((double)(p_98854_ / 40.0F));
        float f1 = (float)Math.atan((double)(p_98855_ / 40.0F));
        renderEntityInInventoryRaw(p_98851_, p_98852_, p_98853_, f, f1, p_98856_);
     }

     public static void renderEntityInInventoryRaw(int p_98851_, int p_98852_, int p_98853_, float angleXComponent, float angleYComponent, LivingEntity p_98856_) {
        if(p_98856_.isRemoved()) return;
        float f = angleXComponent;
        float f1 = angleYComponent;
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((float)p_98851_, (float)p_98852_, 1050.0F);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0F, 0.0F, 1000.0F);
        posestack1.scale((float)p_98853_, (float)p_98853_, (float)p_98853_);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        posestack1.mulPose(quaternionf);
        float f2 = p_98856_.yBodyRot;
        float f3 = p_98856_.getYRot();
        float f4 = p_98856_.getXRot();
        float f5 = p_98856_.yHeadRotO;
        float f6 = p_98856_.yHeadRot;
        p_98856_.yBodyRot = 180.0F + f * 20.0F;
        p_98856_.setYRot(180.0F + f * 40.0F);
        p_98856_.setXRot(-f1 * 20.0F);
        p_98856_.yHeadRot = p_98856_.getYRot();
        p_98856_.yHeadRotO = p_98856_.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternionf1.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(quaternionf1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
           entityrenderdispatcher.render(p_98856_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        p_98856_.yBodyRot = f2;
        p_98856_.setYRot(f3);
        p_98856_.setXRot(f4);
        p_98856_.yHeadRotO = f5;
        p_98856_.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
     }
}