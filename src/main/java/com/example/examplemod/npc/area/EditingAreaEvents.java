package com.example.examplemod.npc.area;

import java.util.List;

import org.joml.Quaterniond;
import org.joml.Quaternionf;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcModel;
import com.example.examplemod.tracking.PlayerTrackedObjects;
import com.example.examplemod.tracking.PlayerTrackingProvider;
import com.example.examplemod.tracking.TrackingManager;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class EditingAreaEvents {
    
    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addGenericListener(Entity.class, EditingAreaEvents::onAttachCapabilitiesPlayer);
        bus.addListener(EditingAreaEvents::onPlayerCloned);
        bus.addListener(EditingAreaEvents::onRegisterCapabilities);
        bus.addListener(EditingAreaEvents::onWorldTick);
    }

    public static void clientSetup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        //bus.addListener(EditingAreaEvents::onRenderLiving);
    }

    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        // Don't do anything client side
        if (event.level.isClientSide) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        // Get the mana manager for this level
        TrackingManager manager = TrackingManager.get(event.level);
        manager.tick(event.level);
    }

    // Whenever a new object of some type is created the AttachCapabilitiesEvent will fire. In our case we want to know
    // when a new player arrives so that we can attach our capability here
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(EditingAreaProvider.EDITING_AREA).isPresent()) {
                // The player does not already have this capability so we need to add the capability provider here
                event.addCapability(new ResourceLocation(ExampleMod.MODID, "editingarea"), new EditingAreaProvider());
            }
        }
    }

    // When a player dies or teleports from the end capabilities are cleared. Using the PlayerEvent.Clone event
    // we can detect this and copy our capability from the old player to the new one
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            event.getOriginal().getCapability(EditingAreaProvider.EDITING_AREA).ifPresent(oldStore -> {
                event.getEntity().getCapability(EditingAreaProvider.EDITING_AREA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    // Finally we need to register our capability in a RegisterCapabilitiesEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(EditingArea.class);
    }
}
