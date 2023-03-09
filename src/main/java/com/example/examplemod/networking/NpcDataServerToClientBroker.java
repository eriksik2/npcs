package com.example.examplemod.networking;

import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class NpcDataServerToClientBroker extends ServerToClientBroker<NpcData> {

    // This instance is registered in the Registration class
    public static final NpcDataServerToClientBroker instance = new NpcDataServerToClientBroker();

    private NpcDataServerToClientBroker() {
        super(Registration.NPC_DATA_BROKER.getId());
    }

    @Override
    public NpcData handle(Context ctx, Integer slot) {
        ServerPlayer player = ctx.getSender();
        NpcManager manager = NpcManager.get(player.level);
        return manager.getNpcData(slot);
    }

    @Override
    public int hashCode(NpcData data) {
        return data.hashCode();
    }

    @Override
    public void toBytes(NpcData data, FriendlyByteBuf buf) {
        data.toBytes(buf);
    }

    @Override
    public NpcData fromBytes(FriendlyByteBuf buf) {
        return new NpcData(buf);
    }
    
}
