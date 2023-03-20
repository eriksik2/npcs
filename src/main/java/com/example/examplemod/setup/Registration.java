package com.example.examplemod.setup;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.encyclopedia.EncyclopediaMenu;
import com.example.examplemod.networking.ServerToClientBroker;
import com.example.examplemod.networking.subscribe.SubscriptionBroker;
import com.example.examplemod.networking.NpcDataServerToClientBroker;
import com.example.examplemod.networking.NpcTeamServerToClientBroker;
import com.example.examplemod.npc.NpcDataSubscriptionBroker;
import com.example.examplemod.npc.NpcEntity;
import com.example.examplemod.npc.NpcInteractMenu;
import com.example.examplemod.npc.NpcRenderDataSerializer;
import com.example.examplemod.npc.area.AreaDesignatorItem;
import com.example.examplemod.npc.team.PlayerTeamsSubscriptionBroker;
import com.example.examplemod.npc.team.TeamEditMenu;
import com.example.examplemod.npc.team.TeamSubscriptionBroker;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ExampleMod.MODID);
    static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS.location().getNamespace());
    
    public static final DeferredRegister<ServerToClientBroker<?>> MESSAGE_BROKERS = DeferredRegister.create(new ResourceLocation(ExampleMod.MODID, "message_brokers"), ExampleMod.MODID);
    public static final RegistryObject<NpcDataServerToClientBroker> NPC_DATA_BROKER = MESSAGE_BROKERS.register("npc_data_broker", () -> NpcDataServerToClientBroker.instance);
    public static final RegistryObject<NpcTeamServerToClientBroker> NPC_TEAM_BROKER = MESSAGE_BROKERS.register("npc_team_broker", () -> NpcTeamServerToClientBroker.instance);
    
    public static final DeferredRegister<SubscriptionBroker<?>> SUBSCRIPTION_BROKERS = DeferredRegister.create(new ResourceLocation(ExampleMod.MODID, "subscription_brokers"), ExampleMod.MODID);
    public static final RegistryObject<TeamSubscriptionBroker> TEAM_SUBSCRIPTION_BROKER = SUBSCRIPTION_BROKERS.register("team_subscription_broker", () -> TeamSubscriptionBroker.INSTANCE);
    public static final RegistryObject<PlayerTeamsSubscriptionBroker> PLAYER_TEAMS_SUBSCRIPTION_BROKER = SUBSCRIPTION_BROKERS.register("player_teams_subscription_broker", () -> PlayerTeamsSubscriptionBroker.INSTANCE);
    public static final RegistryObject<NpcDataSubscriptionBroker> NPC_DATA_SUBSCRIPTION_BROKER = SUBSCRIPTION_BROKERS.register("npc_data_subscription_broker", () -> NpcDataSubscriptionBroker.INSTANCE);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        SERIALIZERS.register(bus);
        MESSAGE_BROKERS.register(bus);
        SUBSCRIPTION_BROKERS.register(bus);
    }

    public static final RegistryObject<EntityType<NpcEntity>> NPC_ENTITY = ENTITIES.register("npc_entity", () -> EntityType.Builder.of(NpcEntity::new, MobCategory.CREATURE)
        .sized(0.6f, 1.95f)
        .clientTrackingRange(8)
        .setShouldReceiveVelocityUpdates(false)
        .build("npc_entity")
    );
    public static final RegistryObject<Item> NPC_EGG = ITEMS.register("npc_egg", () -> new ForgeSpawnEggItem(NPC_ENTITY, 0xff0000, 0x00ff00, new Item.Properties()));
    public static final RegistryObject<Item> AREA_DESIGNATOR = ITEMS.register("area_designator", () -> new AreaDesignatorItem());

    public static final RegistryObject<MenuType<NpcInteractMenu>> NPC_MENU = MENUS.register("npc_menu", () -> IForgeMenuType.create((windowId, inv, data) -> new NpcInteractMenu(windowId, data.readBlockPos(), inv, inv.player)));
    public static final RegistryObject<MenuType<EncyclopediaMenu>> ENCYCLOPEDIA_MENU = MENUS.register("encyclopedia_menu", () -> IForgeMenuType.create((windowId, inv, data) -> new EncyclopediaMenu(windowId, inv.player, null)));
    public static final RegistryObject<MenuType<TeamEditMenu>> TEAM_EDIT_MENU = MENUS.register("team_edit_menu", () -> IForgeMenuType.create((windowId, inv, data) -> new TeamEditMenu(windowId)));


    public static final RegistryObject<NpcRenderDataSerializer> NPC_RENDER_DATA_SERIALIZER = SERIALIZERS.register("npc_render_data_serializer", () -> new NpcRenderDataSerializer());

}
