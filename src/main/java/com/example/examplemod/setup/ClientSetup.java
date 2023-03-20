package com.example.examplemod.setup;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.KeyBindings;
import com.example.examplemod.KeyInputHandler;
import com.example.examplemod.encyclopedia.EncyclopediaScreen;
import com.example.examplemod.npc.NpcInteractScreen;
import com.example.examplemod.npc.NpcModel;
import com.example.examplemod.npc.NpcRenderer;
import com.example.examplemod.npc.area.EditingAreaClientEvents;
import com.example.examplemod.npc.area.EditingAreaEvents;
import com.example.examplemod.npc.team.TeamEditScreen;
import com.example.examplemod.tracking.TrackingEvents;
import com.example.examplemod.tracking.TrackingOverlay;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.NPC_MENU.get(), NpcInteractScreen::new);
            MenuScreens.register(Registration.ENCYCLOPEDIA_MENU.get(), EncyclopediaScreen::new);
            MenuScreens.register(Registration.TEAM_EDIT_MENU.get(), TeamEditScreen::new);
        });
        MinecraftForge.EVENT_BUS.addListener(KeyInputHandler::onKeyInput);
        MinecraftForge.EVENT_BUS.addListener(ClientSetup::onRenderLevelStage);
    }

    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        TrackingEvents.clientSetup();
        EditingAreaClientEvents.clientSetup();
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "trackingoverlay", TrackingOverlay.HUD_TRACKING);
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        EditingAreaClientEvents.onRenderLevelStage(event);
    }

    @SubscribeEvent
    public static void onKeyBindRegister(RegisterKeyMappingsEvent event) {
        KeyBindings.init(event);
    }


    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NpcModel.NPC_LAYER, NpcModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.NPC_ENTITY.get(), NpcRenderer::new);
    }
}
