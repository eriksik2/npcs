package com.example.examplemod;

import com.example.examplemod.npc.area.EditingAreaEvents;
import com.example.examplemod.setup.ClientSetup;
import com.example.examplemod.setup.ModSetup;
import com.example.examplemod.setup.Registration;
import com.example.examplemod.tracking.TrackingEvents;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "examplemod";
    public ExampleMod()
    {
        ModSetup.setup();
        TrackingEvents.setup();
        EditingAreaEvents.setup();
        Registration.init();
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        modbus.addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientSetup.setup();
            modbus.addListener(ClientSetup::init);
        });
    }

}
