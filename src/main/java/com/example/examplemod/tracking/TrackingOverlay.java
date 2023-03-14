package com.example.examplemod.tracking;

import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.example.examplemod.networking.NpcDataServerToClientBroker;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcRenderData;
import com.example.examplemod.npc.NpcRenderer;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class TrackingOverlay {
    public static final IGuiOverlay HUD_TRACKING = (gui, stack, partialTicks, width, height) -> {
        height = height - 40;
        Font font = gui.getFont();
        List<NpcTrackingData> trackedNpcs = ClientTrackedObjects.getTrackedNpcs();
        if(trackedNpcs == null) return;
        //Matrix4f matrix = renderer.getProjectionMatrix(0);
        Matrix3f matrix = RenderSystem.getInverseViewRotationMatrix();
        Player player = Minecraft.getInstance().player;
        
        for (int i = 0; i < trackedNpcs.size(); i++) {
            NpcTrackingData trackedNpc = trackedNpcs.get(i);
            double vrmX = trackedNpc.x - player.getX();
            double vrmY = trackedNpc.y - player.getY();
            double vrmZ = trackedNpc.z - player.getZ();
            double screenX = matrix.m00 * vrmX + matrix.m01 * vrmY + matrix.m02 * vrmZ;
            double screenY = matrix.m10 * vrmX + matrix.m11 * vrmY + matrix.m12 * vrmZ;
            double screenZ = matrix.m20 * vrmX + matrix.m21 * vrmY + matrix.m22 * vrmZ;
            
            double x2 = width / 2 - screenX / screenZ * 100;
            double y2 = height / 2 + screenY / screenZ * 100;
            double z2 = screenZ;
            if(z2 >= 0) {
                if(Math.abs(x2 - width/2) > Math.abs(y2 - height/2)) {
                    x2 = x2 >= width/2 ? -10 : width + 10;
                } else {
                    y2 = y2 >= height/2 ? -10 : height + 10;
                }
            }
            boolean outsideLeft = x2 < 0;
            boolean outsideRight = x2 > width;
            boolean outsideTop = y2 < 0;
            boolean outsideBottom = y2 > height;
            double edgePadding = 10;
            x2 = outsideLeft ? edgePadding : outsideRight ? (width - edgePadding) : x2;
            y2 = outsideTop ? edgePadding : outsideBottom ? (height - edgePadding) : y2;
            if (x2 >= 0 && y2 >= 0) {
                String ttext = "";
                if(outsideLeft && outsideTop)
                    ttext = "↖";
                else if(outsideLeft && outsideBottom)
                    ttext = "↙";
                else if(outsideRight && outsideTop)
                    ttext = "↗";
                else if(outsideRight && outsideBottom)
                    ttext = "↘";
                else if(outsideLeft)
                    ttext = "←";
                else if(outsideRight)
                    ttext = "→";
                else if(outsideTop)
                    ttext = "↑";
                else if(outsideBottom)
                    ttext = "↓";

                NpcData npcData = NpcDataServerToClientBroker.instance.get(trackedNpcs.get(i).npcId);
                if(npcData != null) {
                    int nameWidth = font.width(npcData.name);
                    double textPos = x2 + 7;
                    if(textPos + nameWidth > width)
                        textPos = x2 - nameWidth - 7;

                    double bgX = Math.min(x2 - 5, textPos) - 1;
                    double bgY = y2 + 10 - 1;
                    double bgW = Math.max(x2 + 5, textPos + nameWidth) - bgX + 2*1;
                    double bgH = 10 + 1*1;
                    ForgeGui.fill(stack, (int)bgX, (int)bgY, (int)(bgX+bgW), (int)(bgY+bgH), -1072689136);

                    ResourceLocation texture = NpcRenderer.getTextureLocation(new NpcRenderData(npcData));
                    stack.pushPose();
                    stack.scale(10/8, 10/8, 1);
                    RenderSystem.setShaderTexture(0, texture);
                    ForgeGui.blit(stack, (int)x2-5, (int)y2+10, gui.getBlitOffset(), 8, 8, 8, 8, 64, 32);
                    stack.popPose();
                    
                    font.draw(stack, npcData.name, (int)textPos, (int)y2+10, 0xffffff);
                }

                font.draw(stack, ttext, (int)x2-1, (int)y2+1, 0x0);
                font.draw(stack, ttext, (int)x2+1, (int)y2-1, 0x0);
                font.draw(stack, ttext, (int)x2-1, (int)y2-1, 0x0);
                font.draw(stack, ttext, (int)x2+1, (int)y2+1, 0x0);
                font.draw(stack, ttext, (int)x2, (int)y2, 0xffffff);
            }

        }
    };
}
