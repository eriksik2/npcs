package com.example.examplemod.npc;

import javax.annotation.Nonnull;

import com.example.examplemod.ExampleMod;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NpcRenderer extends HumanoidMobRenderer<NpcEntity, NpcModel> {

    @Nonnull
    private static final ResourceLocation MALE_TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/entity/male1.png");
    @Nonnull
    private static final ResourceLocation FEMALE_TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/entity/female1.png");

    public NpcRenderer(EntityRendererProvider.Context context) {
        super(context, new NpcModel(context.bakeLayer(NpcModel.NPC_LAYER)), 1f);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(NpcEntity entity) {
        NpcData data = entity.getNpcData();
        if(!data.isInitialized()) {
            return MALE_TEXTURE;
        }
        switch (data.gender) {
            case MALE: return MALE_TEXTURE;
            case FEMALE: return FEMALE_TEXTURE;
            default: return MALE_TEXTURE;
        }
    }
}