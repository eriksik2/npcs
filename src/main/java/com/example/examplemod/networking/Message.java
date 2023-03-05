package com.example.examplemod.networking;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface Message {
    public void toBytes(FriendlyByteBuf buf);
    public boolean handle(Supplier<NetworkEvent.Context> supplier);
}
