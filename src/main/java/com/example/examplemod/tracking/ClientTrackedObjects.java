package com.example.examplemod.tracking;

import java.util.ArrayList;

import com.example.examplemod.networking.SyncTrackingToClient;


public class ClientTrackedObjects {
    private ArrayList<TrackedEntityData> trackedEntities;
    public static final ClientTrackedObjects instance = new ClientTrackedObjects();

    public static void set(SyncTrackingToClient message) {
        instance.trackedEntities = message.entities;
    }

    public static ArrayList<TrackedEntityData> getEntities() {
        return instance.trackedEntities;
    }
}
