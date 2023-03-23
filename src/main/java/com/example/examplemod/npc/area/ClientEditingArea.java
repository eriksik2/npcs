package com.example.examplemod.npc.area;

import java.util.ArrayList;
import java.util.List;

import com.example.examplemod.networking.subscribe.ServerSubscription;
import com.example.examplemod.npc.area.NpcAreaRenderer.AreaHitResult;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.npc.team.TeamSubscriptionBroker;
import com.example.examplemod.setup.Registration;

public class ClientEditingArea {
    private static TeamSubscriptionBroker teamSubscriptionBroker;
    private static ServerSubscription<NpcTeam> teamSubscription;
    private static Integer areaId;
    private static Integer teamId;

    public static NpcArea area;
    private static boolean doHitTests = true;
    private static List<AreaHitResult> hitResults = new ArrayList<AreaHitResult>();

    public static void setEditingArea(Integer teamId, Integer areaId) {
        if(teamSubscriptionBroker == null) teamSubscriptionBroker = Registration.TEAM_SUBSCRIPTION_BROKER.get();

        if(teamSubscription != null) {
            teamSubscription.unsubscribe();
            teamSubscription = null;
        }
        ClientEditingArea.teamId = teamId;
        ClientEditingArea.areaId = areaId;
        teamSubscription = teamSubscriptionBroker.subscribe(teamId, ClientEditingArea::onTeamUpdate);
    }

    private static synchronized void onTeamUpdate(NpcTeam team) {
        if(team == null) return;
        area = team.getArea(areaId);
    }

    public static Integer getTeamId() {
        return teamId;
    }

    public static Integer getAreaId() {
        return areaId;
    }

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
