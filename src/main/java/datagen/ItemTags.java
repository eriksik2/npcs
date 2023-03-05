package datagen;

import java.util.concurrent.CompletableFuture;

import com.example.examplemod.ExampleMod;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTags extends ItemTagsProvider {

    public ItemTags(PackOutput pout, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(pout, provider, blockTags, ExampleMod.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //tag(Tags.Items.ORES).add(Registration.MYSTERIOUS_ORE_OVERWORLD_ITEM.get());
    }

    @Override
    public String getName() {
        return "Item Tags Provider";
    }
}