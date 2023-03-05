package datagen;

import com.example.examplemod.ExampleMod;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProvider {

    public BlockStates(PackOutput pOutput, ExistingFileHelper helper) {
        super(pOutput, ExampleMod.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        //simpleBlock(Registration.MYSTERIOUS_ORE_OVERWORLD.get());
    }
}
