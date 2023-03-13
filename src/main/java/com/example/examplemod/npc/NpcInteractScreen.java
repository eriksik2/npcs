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
import com.example.examplemod.npc.dialogue.DialogueTransition;
import com.example.examplemod.npc.dialogue.NpcDialogue;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.tracking.ClientTrackedObjects;
import com.example.examplemod.widgets.ButtonWidget;
import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.ModWidgetContainerScreen;
import com.example.examplemod.widgets.ScrollableListWidget;
import com.example.examplemod.widgets.ScrollableWidget;
import com.example.examplemod.widgets.TextWidget;
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
import net.minecraft.client.gui.components.Button;

public class NpcInteractScreen extends ModWidgetContainerScreen<NpcInteractMenu> {

    static final int NAME_X = 7;
    static final int NAME_Y = 7;

    static final int TRACK_BUTTON_X = 55;
    static final int TRACK_BUTTON_Y = 4;

    static final int DIALOGUE_LIST_X = 6;
    static final int DIALOGUE_LIST_Y = 20;
    static final int DIALOGUE_LIST_W = 94;
    static final int DIALOGUE_LIST_H = 160;
    static final int DIALOGUE_LIST_GAP = 2;

    static final int RESPONSE_PADDING = 2;
    static final int RESPONSE_X = 108 + RESPONSE_PADDING;
    static final int RESPONSE_Y = 106;
    static final int RESPONSE_W = 153 - 2*RESPONSE_PADDING;
    static final int RESPONSE_H = 74;

    private final NpcScreenRandomLookHelper lookHelper;
    private final NpcDialogue npcDialogue;
    private ArrayList<DialogueTransition<String, String>> dialogueTransitions = new ArrayList<>();

    private final ResourceLocation GUI = new ResourceLocation(ExampleMod.MODID, "textures/gui/npc_interact_gui.png");

    private final NpcTeamServerToClientBroker npcTeamBroker = Registration.NPC_TEAM_BROKER.get();
    private final NpcDataServerToClientBroker npcDataBroker = Registration.NPC_DATA_BROKER.get();
    private NpcData npcData;
    private NpcTeam teamData;

    
    private TextWidget npcName;
    private ButtonWidget trackButton;
    private ScrollableListWidget dialogueList;
    private ScrollableWidget responseScrollable;
    private TextWidget responseText;

    public NpcInteractScreen(NpcInteractMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.imageWidth = 276;
        this.imageHeight = 188;
        lookHelper = new NpcScreenRandomLookHelper(this.width, this.height);
        npcDialogue = new NpcDialogue();
        npcDialogue.setOnTransition(this::onDialogueTransition);
    }
    
    @Override
    protected void registerWidgets(ModWidget root) {

        npcName = new TextWidget(root, "");
        npcName.setPosition(leftPos + NAME_X, topPos + NAME_Y);

        // TODO - make the track button text change to untrack if the npc is already tracked.
        // TODO - use the track button graphic.
        trackButton = new ButtonWidget(root, "Track");
        trackButton.setWidth(46);
        trackButton.setPosition(leftPos + TRACK_BUTTON_X, topPos + TRACK_BUTTON_Y);
        trackButton.setOnClick(() -> {
            if(npcData == null) return;
            ToggleTrackingNpc message = new ToggleTrackingNpc(npcData.npcId);
            Messages.sendToServer(message);
        });


        dialogueList = new ScrollableListWidget(root);
        dialogueList.setPosition(leftPos + DIALOGUE_LIST_X, topPos + DIALOGUE_LIST_Y);
        dialogueList.setSize(DIALOGUE_LIST_W, DIALOGUE_LIST_H);
        dialogueList.setGap(DIALOGUE_LIST_GAP);
        dialogueList.setPadding(1);

        responseScrollable = new ScrollableWidget(root);
        responseScrollable.setPosition(leftPos + RESPONSE_X, topPos + RESPONSE_Y);
        responseScrollable.setSize(RESPONSE_W, RESPONSE_H);

        responseText = new TextWidget(responseScrollable, "");
        responseText.setPosition(0, 0);
        responseText.setWidth(responseScrollable.getWidth());
        responseText.setWrap(true);
        onDialogueTransition(null, null, npcDialogue.getCurrentData());
    }

    public void onDialogueTransition(String from, String transition, String to) {
        responseText.setText(to);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if(button == 1) {
            //toggleDebug();
        }
        return super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

        var transitions = new ArrayList<DialogueTransition<String, String>>(npcDialogue.getTransitions());
        if(dialogueTransitions == null
        || transitions.stream().map(a -> a.getData()).reduce((a, b) -> a + b).orElse("")
        != dialogueTransitions.stream().map(a -> a.getData()).reduce((a, b) -> a + b).orElse("")) {
            dialogueTransitions = transitions;
            dialogueList.clearChildren();
            for(var tran : dialogueTransitions) {
                String optText = tran.getData();
                ButtonWidget btn = new ButtonWidget(dialogueList, optText);
                btn.setPadding(1);
                btn.setWidth(dialogueList.getInnerWidth());
                btn.setOnClick(() -> {
                    npcDialogue.makeTransition(tran);
                });
            }
        }

        var newNpcData = npcDataBroker.get(menu.getNpcId());
        if(npcData == null || npcData != newNpcData) {
            npcData = newNpcData;
            npcDialogue.setNpcData(npcData);
            if(npcData != null) {
                npcName.setText(npcData.name);
            }
        }

        var newTeamData = npcTeamBroker.get(menu.getTeamId());
        if(teamData == null || teamData != newTeamData) {
            teamData = newTeamData;
            npcDialogue.setTeamData(teamData);
        }

        lookHelper.tick();

        NpcEntity npcEntity = menu.getEntity();
        int i = this.leftPos;
        int j = this.topPos;
        enableScissor(i + 108, j + 0, i + 108 + 164, j + 0 + 98);
        renderEntityInInventory(i + 190, j + 160, 80, lookHelper.getX(), lookHelper.getY(), npcEntity);
        disableScissor();
        
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        blit(stack, relX, relY, getBlitOffset(), 0, 0, this.imageWidth, this.imageHeight, 512, 256);
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
