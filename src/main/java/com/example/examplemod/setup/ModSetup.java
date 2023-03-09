package com.example.examplemod.setup;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.networking.ServerToClientBroker;
import com.example.examplemod.networking.Messages;
import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.tracking.TrackingEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(
        new CreativeModeTab.Builder(Row.BOTTOM, 0)
            .title(Component.literal("Npcs"))
    ) {
        @Override
        public ItemStack getIconItem() {
            return new ItemStack(Items.DIAMOND);
        }
    };

    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
    }

    public static void init(final FMLCommonSetupEvent event) {
        Messages.register();
    }

    @SubscribeEvent
    public static void onNewRegistryEvent(NewRegistryEvent event) {
        event.create(new RegistryBuilder<ServerToClientBroker<?>>()
            .setName(new ResourceLocation(ExampleMod.MODID, "message_brokers")));
    }

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(Registration.NPC_ENTITY.get(), NpcEntity.prepareAttributes().build());
    }
}
