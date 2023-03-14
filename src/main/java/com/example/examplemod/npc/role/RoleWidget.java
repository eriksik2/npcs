package com.example.examplemod.npc.role;

import java.util.function.Consumer;

import com.example.examplemod.widgets.ModWidget;
import com.example.examplemod.widgets.PopupManagerWidget;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class RoleWidget extends ModWidget {

    private Font font;
    public NpcRole role;

    private PopupManagerWidget popupManager;

    private Consumer<NpcRole> onClick = (role) -> {};

    public RoleWidget(ModWidget parent, PopupManagerWidget popupManager, NpcRole role, Consumer<NpcRole> onClick) {
        super(parent);
        this.font = Minecraft.getInstance().font;
        this.role = role;
        this.popupManager = popupManager;
        this.onClick = onClick;
        setHeight(font.lineHeight);
    }

    @Override
    public void onInit() {
        setHeight(font.lineHeight);
    }

    @Override
    public void onRelayoutPre() {
        if(parent != null) {
            setX(0);
            setWidth(parent.getInnerWidth());
        }
    }

    @Override
    public boolean sizeBasedOnParent() {
        return true;
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        fill(stack, 0, 0, getWidth(), getHeight(), -1072689136);
        font.draw(stack, role.getName(), 0, 0, 0xFFFFFF);
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int button) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        onClick.accept(role);
        return false;
    }
    
}
