package com.example.examplemod.npc.area;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11C;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public class NpcAreaRenderer {
    
    public List<AreaHitResult> hitResults = new ArrayList<>();
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
    aabb = aabb.inflate(1d/16d);

    //if(!frustum.isVisible(aabb)) return;
    poseStack.pushPose();
    poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
    BufferSource bsource = Minecraft.getInstance().renderBuffers().bufferSource();
    BufferBuilder buffer = (BufferBuilder)bsource.getBuffer(AreaRenderType.lines());
    RenderSystem.lineWidth(5.0F);
    RenderSystem.disableDepthTest();
    LevelRenderer.renderLineBox(poseStack, buffer, aabb, 1,0.25f,0.25f,1);
    
    //renderTopSide(aabb);
    //renderBottomSide(aabb);
    //renderFrontSide(aabb);
    //renderBackSide(aabb);
    //renderLeftSide(aabb);
    //renderRightSide(aabb);
    if(ClientEditingArea.doHitTests()) {
        hitResults = ClientEditingArea.setHitResults(doHitTestSides(aabb, camera, player.isShiftKeyDown()));
    } else {
        hitResults = ClientEditingArea.getHitResults();
    }
    for(AreaHitResult result : hitResults) {
        //renderAABB(result.sideAABB);
        renderSide(aabb, result.side);
    }
    //Integer lookingAt = getSideLookingAt(aabb);
    //if(lookingAt != null) {
    //       renderSide(aabb, lookingAt);
    //}

    //renderVector(poseStack, new Vector3f((float)(aabb.minX + aabb.maxX) / 2, (float)(aabb.minY + aabb.maxY) / 2, (float)(aabb.minZ + aabb.maxZ) / 2), new Vector3f(0, 1, 0));
     
    poseStack.popPose();
    }

    public void renderAABB(AABB aabb) {
    renderTopSide(aabb);
    renderBottomSide(aabb);
    renderFrontSide(aabb);
    renderBackSide(aabb);
    renderLeftSide(aabb);
    renderRightSide(aabb);
    }

    public void renderTopSide(AABB aabb) {
    BufferBuilder buffer = (BufferBuilder)Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(AreaRenderType.side());
    RenderSystem.disableDepthTest();
    RenderSystem.disableTexture();
    RenderSystem.disableCull();

     Matrix4f matrix4f = poseStack.last().pose();
     Matrix3f matrix3f = poseStack.last().normal();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 0)
         .uv2(0, 0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(1f, 0)
         .uv2(0, 0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(1f, 1f)
         .uv2(0, 0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 1f)
         .uv2(0, 0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
    }

    public void renderBottomSide(AABB aabb) {
     VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(AreaRenderType.side());
    RenderSystem.disableDepthTest();
    RenderSystem.disableCull();

     Matrix4f matrix4f = poseStack.last().pose();
     Matrix3f matrix3f = poseStack.last().normal();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
    }

    public void renderLeftSide(AABB aabb) {
     VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(AreaRenderType.side());
    RenderSystem.disableDepthTest();
    RenderSystem.disableCull();

     Matrix4f matrix4f = poseStack.last().pose();
     Matrix3f matrix3f = poseStack.last().normal();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
    }

    public void renderRightSide(AABB aabb) {
     VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(AreaRenderType.side());
    RenderSystem.disableDepthTest();
    RenderSystem.disableCull();

     Matrix4f matrix4f = poseStack.last().pose();
     Matrix3f matrix3f = poseStack.last().normal();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
    }

    public void renderFrontSide(AABB aabb) {
     VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(AreaRenderType.side());
    RenderSystem.disableDepthTest();
    RenderSystem.disableCull();

     Matrix4f matrix4f = poseStack.last().pose();
     Matrix3f matrix3f = poseStack.last().normal();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.minZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
    }

    public void renderBackSide(AABB aabb) {
     VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(AreaRenderType.side());
    RenderSystem.disableDepthTest();
    RenderSystem.disableCull();

     Matrix4f matrix4f = poseStack.last().pose();
     Matrix3f matrix3f = poseStack.last().normal();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.minY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.minX, (float)aabb.maxY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 0)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(1, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
     buffer.vertex(matrix4f, (float)aabb.maxX, (float)aabb.minY, (float)aabb.maxZ)
         .color(1, 1, 1, 0.5f)
         .uv(0, 1)
         .uv2(0)
         .normal(matrix3f, 0, 1, 0)
         .endVertex();
    }

    public static AABB[] splitIntoSides(AABB aabb, double thickness) {
        double minX = aabb.minX;
        double minY = aabb.minY;
        double minZ = aabb.minZ;
        double maxX = aabb.maxX;
        double maxY = aabb.maxY;
        double maxZ = aabb.maxZ;
        double thickness2 = Math.max(0, -thickness);
        AABB[] sides = new AABB[6];
        sides[0] = new AABB(minX - thickness2, minY, minZ - thickness2, maxX + thickness2, minY + thickness, maxZ + thickness2);
        sides[1] = new AABB(minX - thickness2, maxY - thickness, minZ - thickness2, maxX + thickness2, maxY, maxZ + thickness2);
        sides[2] = new AABB(minX, minY - thickness2, minZ - thickness2, minX + thickness, maxY + thickness2, maxZ + thickness2);
        sides[3] = new AABB(maxX - thickness, minY - thickness2, minZ - thickness2, maxX, maxY + thickness2, maxZ + thickness2);
        sides[4] = new AABB(minX - thickness2, minY - thickness2, minZ, maxX + thickness2, maxY + thickness2, minZ + thickness);
        sides[5] = new AABB(minX - thickness2, minY - thickness2, maxZ - thickness, maxX + thickness2, maxY + thickness2, maxZ);

        return sides;
    }

    public void renderSide(AABB aabb, int sideI) {
        switch(sideI) {
            case 0: renderBottomSide(aabb); break;
            case 1: renderTopSide(aabb); break;
            case 2: renderLeftSide(aabb); break;
            case 3: renderRightSide(aabb); break;
            case 4: renderFrontSide(aabb); break;
            case 5: renderBackSide(aabb); break;
            default: break;
        }
        AABB side = splitIntoSides(aabb, 0.0)[sideI];
        Vector3f normal = getSideNormals()[sideI];
        Vector3f center = side.getCenter().toVector3f();
        renderVector(poseStack, center, normal);
        }

        public static Vector3f[] getSideNormals() {
        Vector3f[] normals = new Vector3f[6];
        normals[0] = new Vector3f(0, -1, 0);
        normals[1] = new Vector3f(0, 1, 0);
        normals[2] = new Vector3f(-1, 0, 0);
        normals[3] = new Vector3f(1, 0, 0);
        normals[4] = new Vector3f(0, 0, -1);
        normals[5] = new Vector3f(0, 0, 1);
        return normals;
    }

    public static class AreaHitResult {
        public final AABB sideAABB;
        public final int side;
        public final Vector3f hitPos;
        public final Vector3f hitNormal;
        public AreaHitResult(AABB sideAABB, int side, Vector3f hitPos, Vector3f hitNormal) {
            this.sideAABB = sideAABB;
            this.side = side;
            this.hitPos = hitPos;
            this.hitNormal = hitNormal;
        }
    }

    public static List<AreaHitResult> doHitTestSides(AABB aabb, Camera camera, boolean skipFirstHit) {
        Vec3 origin = camera.getPosition(); 
        Vector3f look = camera.getLookVector();
        AABB[] sides = NpcAreaRenderer.splitIntoSides(aabb, 0.1);
        Vector3f[] normals = NpcAreaRenderer.getSideNormals();
        ArrayList<AreaHitResult> sidesHit = new ArrayList<AreaHitResult>();
        for(int sideIndex = 0; sideIndex < sides.length; sideIndex++) {
            AABB side = sides[sideIndex];
            Vector3f normal = normals[sideIndex];
            boolean isTangetial = false;
            boolean isBehind = false;
            float lambda = 0;
            for(int i = 0; i < 3; i++) {
                if(normal.get(i) == 0) continue;
                if(look.get(i) == 0) {
                    isTangetial = true;
                    break;
                }
                lambda = (side.getCenter().toVector3f().get(i) - (float)origin.toVector3f().get(i)) / look.get(i);
                if(lambda < 0) {
                    isBehind = true;
                    break;
                }
                break;
            }
            if(isTangetial || isBehind) continue;
            Vector3f hitPos = origin.toVector3f().add(look.mul(lambda));
            if(side.contains(new Vec3(hitPos))) {
                sidesHit.add(new AreaHitResult(side, sideIndex, hitPos, normal));
            }
        }
        return sidesHit;
    }
    

    static public void renderVector(PoseStack stack, Vector3f origin, Vector3f dir) {
    VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
    RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
    RenderSystem.disableCull();
    RenderSystem.lineWidth(5.0F);

    Matrix4f matrix4f = stack.last().pose();
    Matrix3f matrix3f = stack.last().normal();
    buffer.vertex(matrix4f, origin.x(), origin.y(), origin.z())
        .color(1, 1, 1, 0.5f)
        .uv(0, 0)
        .uv2(0)
        .normal(matrix3f, 0, 1, 0)
        .endVertex();
    buffer.vertex(matrix4f, origin.x() + dir.x(), origin.y() + dir.y(), origin.z() + dir.z())
        .color(1, 1, 1, 0.5f)
        .uv(0, 0)
        .uv2(0)
        .normal(matrix3f, 0, 1, 0)
        .endVertex();
    }
}
