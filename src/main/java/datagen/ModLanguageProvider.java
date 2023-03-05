package datagen;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.setup.Registration;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput pout, String locale) {
        super(pout, ExampleMod.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        //add("itemGroup." + TAB_NAME, "Tutorial");
        add(Registration.NPC_EGG.get(), "Human egg");
    }
}