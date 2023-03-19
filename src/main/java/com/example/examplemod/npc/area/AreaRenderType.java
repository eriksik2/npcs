package com.example.examplemod.npc.area;


import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11C;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class AreaRenderType extends RenderType {
    public AreaRenderType(String p_173178_, VertexFormat p_173179_, Mode p_173180_, int p_173181_, boolean p_173182_,
            boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    private static final RenderType SIDE = create("area_side", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, sideState(RENDERTYPE_TRANSLUCENT_SHADER));
    private static final RenderType LINES = create("area_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false, lineState());

    private static RenderType.CompositeState sideState(RenderStateShard.ShaderStateShard p_173208_) {
        return RenderType.CompositeState.builder()
            .setLightmapState(LIGHTMAP)
            .setShaderState(p_173208_)
            .setTextureState(NO_TEXTURE)
            .setDepthTestState(new DepthTestStateShard("no_depth", GL11C.GL_ALWAYS))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET)
            .setCullState(NO_CULL)
            .createCompositeState(true);
    }

    private static RenderType.CompositeState lineState() {
        return RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_LINES_SHADER)
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setDepthTestState(new DepthTestStateShard("no_depth", GL11C.GL_ALWAYS))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(ITEM_ENTITY_TARGET)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .setCullState(NO_CULL)
            .createCompositeState(false);
    }

    public static RenderType side() {
        return SIDE;
    }

    public static RenderType lines() {
        return LINES;
    }
}
