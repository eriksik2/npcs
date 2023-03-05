package com.example.examplemod.npc;

import com.example.examplemod.ExampleMod;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

public class NpcModel extends HumanoidModel<NpcEntity> {

    public static final String BODY = "body";

    public static ModelLayerLocation NPC_LAYER = new ModelLayerLocation(new ResourceLocation(ExampleMod.MODID, "npc"), BODY);

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = createMesh(CubeDeformation.NONE, 0.6f);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public NpcModel(ModelPart part) {
        super(part);
    }
}
