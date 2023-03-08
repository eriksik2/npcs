package com.example.examplemod.tracking;

import java.util.ArrayList;

import com.example.examplemod.networking.SyncTrackingToClient;


public class ClientTrackedObjects {
    private ArrayList<NpcTrackingData> trackedNpcs;
    public static final ClientTrackedObjects instance = new ClientTrackedObjects();

    public static void set(SyncTrackingToClient message) {
        instance.trackedNpcs = message.npcs;
    }

    public static ArrayList<NpcTrackingData> getTrackedNpcs() {
        return instance.trackedNpcs;
    }

    public static boolean isTracked(int npcId) {
        for (NpcTrackingData npc : instance.trackedNpcs) {
            if (npc.npcId == npcId) {
                return true;
            }
        }
        return false;
    }
}
