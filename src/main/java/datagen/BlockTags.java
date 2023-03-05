package datagen;

import java.util.concurrent.CompletableFuture;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTags extends BlockTagsProvider {

    public BlockTags(PackOutput pout, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(pout, provider, ExampleMod.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Registration.MYSTERIOUS_ORE_OVERWORLD.get());
    }

    @Override
    public String getName() {
        return "Block Tags Provider";
    }
}