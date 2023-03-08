package com.example.examplemod.tracking;

import java.util.List;

import org.joml.Quaterniond;
import org.joml.Quaternionf;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcModel;

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

public class TrackingEvents {
    
    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addGenericListener(Entity.class, TrackingEvents::onAttachCapabilitiesPlayer);
        bus.addListener(TrackingEvents::onPlayerCloned);
        bus.addListener(TrackingEvents::onRegisterCapabilities);
        bus.addListener(TrackingEvents::onWorldTick);
    }

    public static void clientSetup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        //bus.addListener(TrackingEvents::onRenderLiving);
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
            if (!event.getObject().getCapability(PlayerTrackingProvider.TRACKED_OBJECTS).isPresent()) {
                // The player does not already have this capability so we need to add the capability provider here
                event.addCapability(new ResourceLocation(ExampleMod.MODID, "playertracking"), new PlayerTrackingProvider());
            }
        }
    }

    // When a player dies or teleports from the end capabilities are cleared. Using the PlayerEvent.Clone event
    // we can detect this and copy our capability from the old player to the new one
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            event.getOriginal().getCapability(PlayerTrackingProvider.TRACKED_OBJECTS).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerTrackingProvider.TRACKED_OBJECTS).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    // Finally we need to register our capability in a RegisterCapabilitiesEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerTrackedObjects.class);
    }

    /*public static void onRenderLiving(RenderLivingEvent<NpcEntity, NpcModel> event) {
        List<NpcTrackingData> npcs = ClientTrackedObjects.getTrackedNpcs();

        //Player player = Minecraft.getInstance().player;
        if(npcs == null) {
            return;
        }
        for(NpcTrackingData npc : npcs) {
            if (npc.npcId == event.getEntity().getId()) {
                event.getPoseStack().pushPose();
                Quaternionf rotation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
                event.getPoseStack().mulPose(rotation);
                event.getPoseStack().translate(0, 2, 0);
                event.getPoseStack().scale(0.02f, 0.02f, -0.02f);
                event.getRenderer().getFont().draw(event.getPoseStack(), "Tracked Entity", 0, 0, 0xffffff);
                event.getPoseStack().popPose();
            }
        }
    }*/
}
