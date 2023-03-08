package com.example.examplemod.networking;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.npc.NpcData;
import com.example.examplemod.npc.NpcManager;
import com.example.examplemod.setup.Registration;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class NpcClientDataManager extends ClientDataManager<NpcData> {

    public static final NpcClientDataManager instance = new NpcClientDataManager();

    private NpcClientDataManager() {
        super(Registration.NPC_DATA_BROKER.getId());
    }

    @Override
    public NpcData handle(Context ctx, Integer slot) {
        ServerPlayer player = ctx.getSender();
        NpcManager manager = NpcManager.get(player.level);
        return manager.getNpcData(slot);
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
