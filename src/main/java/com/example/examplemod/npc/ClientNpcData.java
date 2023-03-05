package com.example.examplemod.npc;

import com.example.examplemod.networking.GetNpcData;
import com.example.examplemod.networking.Messages;

public class ClientNpcData {
    
    public static final ClientNpcData instance = new ClientNpcData();

    public NpcData npcData = null;
    private Integer npcId = null;
    private Long lastRequest = null;

    public static void set(NpcData data) {
        instance.npcData = data;
    }

    public static NpcData get(int npcId) {
        if(instance.npcId != null && instance.npcId == npcId) {
            if(instance.lastRequest != null
            && System.currentTimeMillis() - instance.lastRequest >= 1000) {
                Messages.sendToServer(new GetNpcData(npcId));
                instance.lastRequest = System.currentTimeMillis();
            }
            return instance.npcData;
        } else {
            Messages.sendToServer(new GetNpcData(npcId));
            instance.lastRequest = System.currentTimeMillis();
            instance.npcData = null;
            instance.npcId = npcId;
            return instance.npcData;
        }
    }
}
