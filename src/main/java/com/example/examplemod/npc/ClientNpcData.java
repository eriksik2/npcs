package com.example.examplemod.npc;

import com.example.examplemod.networking.GetNpcData;
import com.example.examplemod.networking.Messages;

public class ClientNpcData {
    
    public static final ClientNpcData instance = new ClientNpcData();

    public NpcData npcData = null;

    public static void set(NpcData data) {
        instance.npcData = data;
    }

    public static ClientNpcData get(int npcId) {
        Messages.sendToServer(new GetNpcData(npcId));
        instance.npcData = null;
        return instance;
    }
}
