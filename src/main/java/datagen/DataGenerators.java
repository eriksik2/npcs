package datagen;

import com.example.examplemod.ExampleMod;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput pout = generator.getPackOutput();
        generator.addProvider(event.includeServer(), new Recipes(pout));
        

        BlockTags blockTags = new BlockTags(pout, event.getLookupProvider(), event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ItemTags(pout, event.getLookupProvider(), blockTags, event.getExistingFileHelper()));

        generator.addProvider(event.includeClient(), new BlockStates(pout, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ItemModels(pout, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ModLanguageProvider(pout, "en_us"));
    }
}