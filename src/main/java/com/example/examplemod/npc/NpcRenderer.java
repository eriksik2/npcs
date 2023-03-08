package com.example.examplemod.npc;

import javax.annotation.Nonnull;

import com.example.examplemod.ExampleMod;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NpcRenderer extends HumanoidMobRenderer<NpcEntity, NpcModel> {

    private static final ResourceLocation[] SKIN_TEXTURE = {
        null, // First one is null so that we can have no skin
        new ResourceLocation(ExampleMod.MODID, "textures/entity/skin1.png"),
    };
    private static final ResourceLocation[] PANTS_TEXTURE = {
        null, // First one is null so that we can have no skin
        new ResourceLocation(ExampleMod.MODID, "textures/entity/pants1.png"),
        new ResourceLocation(ExampleMod.MODID, "textures/entity/pants2.png"),
    };
    private static final ResourceLocation[] SHIRT_TEXTURE = {
        null, // First one is null so that we can have no skin
        new ResourceLocation(ExampleMod.MODID, "textures/entity/shirt1.png"),
    };

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
        NpcRenderData data = entity.getRenderData();
        return NpcRenderer.getTextureLocation(data);
    }

    @Nonnull
    public static ResourceLocation getTextureLocation(NpcRenderData data) {
        switch (data.gender) {
            case MALE: return MALE_TEXTURE;
            case FEMALE: return FEMALE_TEXTURE;
            default: return MALE_TEXTURE;
        }
    }
}