package com.example.examplemod.npc.area;

import com.example.examplemod.setup.Registration;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class EditingAreaClientEvents {
    public static void clientSetup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EditingAreaClientEvents::onMouseInput);
        bus.addListener(EditingAreaClientEvents::onWorldTick);
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            if(event.getCamera().getEntity() instanceof Player player) {
                if(!player.level.isClientSide) return;
                NpcArea area = ClientEditingArea.area;
                if(area == null) return;
                NpcAreaRenderer renderer = new NpcAreaRenderer(area);
                renderer.render(event);
            }
        }
    }

    public static void onMouseInput(InputEvent.MouseButton event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        if(!player.level.isClientSide) return;
        if(player.isHolding(Registration.AREA_DESIGNATOR.get())) {
            AreaDesignatorItemClientBehaviour.onMouseEvent(event, player);
        }
    }

    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide && event.phase == TickEvent.Phase.START) {
            Player player = Minecraft.getInstance().player;
            if(player == null) return;
            if(player.isHolding(Registration.AREA_DESIGNATOR.get())) {
                AreaDesignatorItemClientBehaviour.tickClient(player);
            }
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
    }
}
