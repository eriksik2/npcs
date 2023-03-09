package com.example.examplemod.networking;

import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.npc.team.NpcTeam;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class NpcTeamServerToClientBroker extends ServerToClientBroker<NpcTeam> {

    // This instance is registered in the Registration class
    public static final NpcTeamServerToClientBroker instance = new NpcTeamServerToClientBroker();

    private NpcTeamServerToClientBroker() {
        super(Registration.NPC_TEAM_BROKER.getId());
    }

    @Override
    public NpcTeam handle(Context ctx, Integer slot) {
        ServerPlayer player = ctx.getSender();
        NpcManager manager = NpcManager.get(player.level);
        return manager.getTeam(slot);
    }

    @Override
    public int hashCode(NpcTeam data) {
        return data.hashCode();
    }

    @Override
    public void toBytes(NpcTeam data, FriendlyByteBuf buf) {
        data.toBytes(buf);
    }

    @Override
    public NpcTeam fromBytes(FriendlyByteBuf buf) {
        return new NpcTeam(buf);
    }
    
}