package com.example.examplemod.tracking;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class TrackingOverlay {
    public static final IGuiOverlay HUD_TRACKING = (gui, poseStack, partialTicks, width, height) -> {
        Font font = gui.getFont();
        List<TrackedEntityData> entities = ClientTrackedObjects.getEntities();

        String text = "Tracking " + entities.size() + " entities";
        int x = width / 2 - font.width(text)/2;
        int y = 20;
        if (x >= 0 && y >= 0) {
            font.draw(poseStack, text, x, y, 0xffffff);
        }
    };
}
