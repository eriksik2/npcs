package com.example.examplemod.widgets;

import com.example.examplemod.npc.role.NpcRole;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class RoleWidget extends ModWidget {

    private Font font;
    public NpcRole role;

    public RoleWidget(ModWidget parent, NpcRole role) {
        super(parent);
        this.font = Minecraft.getInstance().font;
        this.role = role;
        setHeight(font.lineHeight);
    }

    @Override
    public void onInit() {
        setHeight(font.lineHeight);
    }

    @Override
    public void onRender(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        fill(stack, 0, 0, getWidth(), getHeight(), -1072689136);
        font.draw(stack, role.getName(), 0, 0, 0xFFFFFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        // TODO
    }
    
}
