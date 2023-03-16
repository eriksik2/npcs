package com.example.examplemod.npc.area;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.example.examplemod.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public class NpcAreaRenderer {
    
    private NpcArea area;

    private LevelRenderer levelRenderer;
    private PoseStack poseStack;
    private Matrix4f projectionMatrix;
    private int renderTick;
    private float partialTick;
    private Camera camera;
    private Frustum frustum;

    public NpcAreaRenderer(NpcArea area) {
        this.area = area;
    }

    public void render(RenderLevelStageEvent event) {
        levelRenderer = event.getLevelRenderer();
        poseStack = event.getPoseStack();
        projectionMatrix = event.getProjectionMatrix();
        renderTick = event.getRenderTick();
        partialTick = event.getPartialTick();
        camera = event.getCamera();
        frustum = event.getFrustum();
        render();
    }

    private void render() {
        if(area == null) return;
        BlockPos corner1 = area.getCorner1();
        BlockPos corner2 = area.getCorner2();
        int color = area.getColor();
        if(corner1 == null || corner2 == null) return;
        Player player = Minecraft.getInstance().player;
        if(player == null || !player.isHolding(Registration.AREA_DESIGNATOR.get())) return;

        Vec3 camPos = camera.getPosition();

        int minX = Math.min(corner1.getX(), corner2.getX());
        int minY = Math.min(corner1.getY(), corner2.getY());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());

        int maxX = Math.max(corner1.getX(), corner2.getX());
        int maxY = Math.max(corner1.getY(), corner2.getY());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());

        AABB aabb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        aabb = aabb.inflate(0.1D);

        //if(!frustum.isVisible(aabb)) return;
        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(5.0F);
        LevelRenderer.renderLineBox(poseStack, buffer, aabb, 1,1,1,1);

        renderTopSide(aabb);
        renderBottomSide(aabb);
        renderFrontSide(aabb);
        renderBackSide(aabb);
        renderLeftSide(aabb);
        renderRightSide(aabb);
        
        poseStack.popPose();
    }

    public void renderTopSide(AABB aabb) {
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
        RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
        RenderSystem.disableCull();

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
            .endVertex();
    }

    public void renderBottomSide(AABB aabb) {
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
        RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
        RenderSystem.disableCull();

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
            .endVertex();
    }

    public void renderLeftSide(AABB aabb) {
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
        RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
        RenderSystem.disableCull();

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
    }

    public void renderRightSide(AABB aabb) {
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
        RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
        RenderSystem.disableCull();

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
    }

    public void renderFrontSide(AABB aabb) {
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
        RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
        RenderSystem.disableCull();

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.minZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
    }

    public void renderBackSide(AABB aabb) {
        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
        RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
        RenderSystem.disableCull();

        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
        buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.maxZ)
               .color(0.5F, 0.5F, 0.5F, 0.5F)
               .uv(0, 0)
               .uv2(0)
               .normal(matrix3f, 0, 1, 0)
               .endVertex();
    }

    //public String getSideLookingAt() {
    //    Vec3 origin = camera.getPosition();
    //    Vector3f look = camera.getLookVector();
    //}
}
