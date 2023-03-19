package com.example.examplemod.npc.area;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.example.examplemod.npc.area.NpcAreaRenderer.AreaHitResult;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ClientEditingArea {
    public static NpcArea area;
    private static boolean doHitTests = true;
    private static List<AreaHitResult> hitResults = new ArrayList<AreaHitResult>();


    public static synchronized void stopHitTests() {
        doHitTests = false;
    }

    public static synchronized void startHitTests() {
        doHitTests = true;
    }

    public static synchronized boolean doHitTests() {
        return doHitTests;
    }

    public static synchronized List<AreaHitResult> setHitResults(List<AreaHitResult> hitResults) {
        if(!doHitTests()) return ClientEditingArea.hitResults;
        ClientEditingArea.hitResults.clear();
        ClientEditingArea.hitResults.addAll(hitResults);
        return ClientEditingArea.hitResults;
    }

    public static synchronized List<AreaHitResult> getHitResults() {
        return new ArrayList<AreaHitResult>(ClientEditingArea.hitResults);
    }
}
