package datagen;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.setup.Registration;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

    public ItemModels(PackOutput pout, ExistingFileHelper existingFileHelper) {
        super(pout, ExampleMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(Registration.NPC_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        //withExistingParent(Registration.MYSTERIOUS_ORE_OVERWORLD_ITEM.getId().getPath(), modLoc("block/mysterious_ore_overworld"));

        singleTexture(Registration.AREA_DESIGNATOR.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/area_designator"));
    }
}
