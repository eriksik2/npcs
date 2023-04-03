package com.example.examplemod.npc.task;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.npc.task.taskTypes.KillMobTask;
import com.example.examplemod.npc.task.taskTypes.WoodcutTask;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class TaskRegistration {
    public static final DeferredRegister<TaskType> TASK_TYPES = DeferredRegister.create(new ResourceLocation(ExampleMod.MODID, "npc_task_types"), ExampleMod.MODID);


    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TASK_TYPES.register(bus);
    }

    public static void onNewRegistryEvent(NewRegistryEvent event) {
        event.create(new RegistryBuilder<TaskType>()
            .setName(new ResourceLocation(ExampleMod.MODID, "npc_task_types")));
    }

    public static final RegistryObject<TaskType> WOODCUT_TASK = TASK_TYPES.register("woodcut_task", () -> new WoodcutTask());
    public static final RegistryObject<TaskType> KILL_MOB_TASK = TASK_TYPES.register("kill_mob_task", () -> new KillMobTask());

}
