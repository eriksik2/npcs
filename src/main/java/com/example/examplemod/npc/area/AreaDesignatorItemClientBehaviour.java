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
            AreaHitResult hitResults = ClientEditingArea.getHitResults();
            if(hitResults != null) {
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
            AreaHitResult hitResult = ClientEditingArea.getHitResults();
            if(hitResult != null) {
                Vector3f hit = hitResult.hitPos;
                Vector3f normal = hitResult.hitNormal;
                boolean isVertical = normal.y != 0;
                float axisNormal = 0;
                int axis = 0;
                for(int i = 0; i < 3; i++) {
                    if(normal.get(i) != 0) {
                        axisNormal = normal.get(i);
                        axis = i;
                        break;
                    }
                }
                boolean isNegative = axisNormal < 0;


                Vector3f planePoint = plr;
                Vector3f planeVector1 = dir;
                Vector3f planeVector2 = isVertical
                    ? player.getViewVector(1).cross(player.getUpVector(1)).toVector3f()
                    : player.getUpVector(1).toVector3f();

                if(isNegative) {
                    if(axis == 1) {
                        planeVector2.mul(-1);
                    }
                    //planeVector2.mul(-1);
                }
                
                Vector3f planeNormal = planeVector1.cross(planeVector2).normalize();
                // Flip planeNormal if it's pointing away from the hit point
                if(planeNormal.dot(normal) < 0) {
                    planeNormal.mul(-1);
                }

                // for vertical case:
                // n1 * (x - p1) + n2 * (y - p2) + n3 * (z - p3) = 0
                // -n1 * (x - p1) - n3 * (z - p3) = n2 * (y - p2)
                // -(n1*(x - p1) + n3*(z - p3))/n2 + p2 = y
                float dividend = 0;
                float divisor = 0;
                float constant = 0;
                for(int i = 0; i < 3; i++) {
                    if(normal.get(i) != 0) {
                        divisor = normal.get(i);
                        constant = planePoint.get(i);
                        continue;
                    } else {
                        dividend += planeNormal.get(i) * (hit.get(i) - planePoint.get(i));
                    }
                }
                float newAxisPoint = -(dividend / divisor) + constant;
                AABB newAabb = setSidePos(area.toAABB(), hitResult.side, newAxisPoint);

                //float dist = hit.distance(plr);
                //Vector3f dragToPoint = plr.add(dir.mul(dist)).mul(hitResult.hitNormal);
                //float dragDist = dragToPoint.length();
                //AABB newAabb = setSidePos(area.toAABB(), hitResult.side, dragDist);
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
