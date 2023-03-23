package com.example.examplemod.widgets;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.networking.OpenEncyclopedia;
import com.example.examplemod.networking.subscribe.ServerSubscription;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcDataSubscriptionBroker;
import com.example.examplemod.npc.NpcRenderData;
import com.example.examplemod.npc.NpcRenderer;
import com.example.examplemod.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;

public class NpcPreviewWidget extends ModWidget {

    private NpcData npcData;
    private TextureWidget textureWidget;
    private TextWidget textWidget;

    private NpcDataSubscriptionBroker subscriptionBroker = Registration.NPC_DATA_SUBSCRIPTION_BROKER.get();
    private ServerSubscription<NpcData> subscription;

    private boolean showFace = true;

    public NpcPreviewWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onInit() {
        textureWidget = new TextureWidget(this);
        textureWidget.setTextureBlit(8, 8, 8, 8);
        
        textWidget = new TextWidget(this, "");
    }

    @Override
    public void onRelayoutPre() {
        this.setHeight(10);

        textureWidget.setPosition(0, 0);
        if(showFace) {
            textureWidget.setSize(getInnerHeight(), getInnerHeight());
        } else {
            textureWidget.setSize(0, 0);
        }
        int textPos = showFace ? textureWidget.getWidth() + 4 : 0;
        textWidget.setPosition(textPos, 0);
        textWidget.layoutCenterY();

        setWidth(textWidget.getX() + textWidget.getWidth());
    }

    @Override
    public void onDeinit() {
        if(subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public void setShowFace(boolean showFace) {
        this.showFace = showFace;
        setLayoutDirty();
    }

    public void setColor(int color) {
        textWidget.setColor(color);
    }

    public void setNpcId(Integer npcId) {
        if(subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        subscription = subscriptionBroker.subscribe(npcId, this::onNpcDataChanged);
    }

    public void onNpcDataChanged(NpcData data) {
        this.npcData = data;
        textureWidget.setTexture(NpcRenderer.getTextureLocation(new NpcRenderData(npcData)), 64, 32);
        textWidget.setText(npcData.getName());
        setWidth(textWidget.getX() + textWidget.getWidth());
        setLayoutDirty();
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(isMouseOver(mouseX, mouseY)) {
            fillGradient(stack, textWidget.getX(), getHeight()-1, textWidget.getX()+textWidget.getWidth(), getHeight(), -0x7B7B7B, -0x194D33);
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int button) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        if(button == 0) {
            if(npcData != null) {
                Messages.sendToServer(new OpenEncyclopedia(npcData.getId()));
            }
            return true;
        }
        return false;
    }
    
}
