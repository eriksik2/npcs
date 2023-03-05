package com.example.examplemod.npc;

import com.example.examplemod.networking.GetNpcData;
import com.example.examplemod.networking.GetTeamData;
import com.example.examplemod.networking.Messages;

public class ClientNpcTeam {
    
    public static final ClientNpcTeam instance = new ClientNpcTeam();

    public NpcTeam npcTeam = null;
    private Integer teamId = null;
    private Long lastRequest = null;

    public static void set(NpcTeam team) {
        instance.npcTeam = team;
    }

    public static NpcTeam get(int teamId) {
        if(instance.teamId != null && instance.teamId == teamId) {
            if(instance.lastRequest != null
            && System.currentTimeMillis() - instance.lastRequest >= 1000) {
                Messages.sendToServer(new GetTeamData(teamId));
                instance.lastRequest = System.currentTimeMillis();
            }
            return instance.npcTeam;
        } else {
            Messages.sendToServer(new GetTeamData(teamId));
            instance.lastRequest = System.currentTimeMillis();
            instance.npcTeam = null;
            instance.teamId = teamId;
            return instance.npcTeam;
        }
    }
}
