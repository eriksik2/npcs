package com.example.examplemod.npc.area;

import java.util.List;

import org.joml.Vector3f;

import com.example.examplemod.networking.Messages;
import com.example.examplemod.npc.area.NpcAreaRenderer.AreaHitResult;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.InputEvent;

public class AreaDesignatorItemClientBehaviour {

    private static boolean isResizingArea = false;


    public static void onMouseEvent(InputEvent.MouseButton event, Player player) {
        if(event.getButton() == 1) {
            List<AreaHitResult> hitResults = ClientEditingArea.getHitResults();
            if(hitResults.size() != 0) {
                if(event.getAction() == 1) {
                    isResizingArea = true;
                    ClientEditingArea.stopHitTests();
                } else if(event.getAction() == 0) {
                    isResizingArea = false;
                    Integer teamId = ClientEditingArea.getTeamId();
                    Integer areaId = ClientEditingArea.getAreaId();
                    NpcArea area = ClientEditingArea.area;
                    Messages.sendToServer(new UpdateAreaPositionMsg(teamId, areaId, area.getCorner1(), area.getCorner2()));
                    ClientEditingArea.startHitTests();
                }
            } else {
                if(event.getAction() == 1) {
                    Minecraft.getInstance().setScreen(new AreaDesignatorScreen(player));
                }
            }
        }
    }

    public static void tickClient(Player player) {
        if(isResizingArea) {
            NpcArea area = ClientEditingArea.area;
            if(area == null) return;
            //System.out.println("hitResults.size() = " + hitResults.size());

            Vector3f plr = new Vector3f((float)player.getX(), (float)player.getY() + player.getEyeHeight(), (float)player.getZ());
            Vector3f dir = new Vector3f((float)player.getViewVector(1).x, (float)player.getViewVector(1).y, (float)player.getViewVector(1).z);
            for(AreaHitResult hitResult : ClientEditingArea.getHitResults()) {
                Vector3f hit = hitResult.hitPos;
                float dist = hit.distance(plr);
                Vector3f dragToPoint = plr.add(dir.mul(dist)).mul(hitResult.hitNormal);
                float dragDist = dragToPoint.length();
                AABB newAabb = setSidePos(area.toAABB(), hitResult.side, dragDist);
                area.fromAAAB(newAabb);
            }
        }
    }

    private static AABB setSidePos(AABB aabb, int side, float pos) {
        // side = 0 : bottom
        // side = 1 : top
        // side = 2 : left
        // side = 3 : right
        // side = 4 : front
        // side = 5 : back
        double x1 = aabb.minX;
        double y1 = aabb.minY;
        double z1 = aabb.minZ;
        double x2 = aabb.maxX;
        double y2 = aabb.maxY;
        double z2 = aabb.maxZ;

        switch(side) {
            case 0:
                y1 = pos;
                break;
            case 1:
                y2 = pos;
                break;
            case 2:
                x1 = pos;
                break;
            case 3:
                x2 = pos;
                break;
            case 4:
                z1 = pos;
                break;
            case 5:
                z2 = pos;
                break;
        }

        return new AABB(x1, y1, z1, x2, y2, z2);
    }

}
