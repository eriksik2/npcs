package com.example.examplemod.networking;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.RegistryObject;

class Slot<TData> {
    public TData data = null;
    public Long lastRequest = 0l;
}

public abstract class ClientDataManager<TData> {
    private HashMap<Integer, Slot<TData>> slots = new HashMap<Integer, Slot<TData>>();

    private ResourceLocation id;

    public ClientDataManager(ResourceLocation id) {
        this.id = id;
    }

    // Only call from the client as a response to server data
    public void unsafeSet(Integer handle, Object data) {
        Slot<TData> slot = slots.get(handle);
        if(slot == null) {
            slot = new Slot<TData>();
            slots.put(handle, slot);
        }
        slot.data = (TData)data;
    }

    public TData get(Integer slotNum) {
        Slot<TData> slot = slots.get(slotNum);
        if(slot == null) {
            slot = new Slot<TData>();
            slots.put(slotNum, slot);
        }
        if(slot.data == null) {
            if(slot.lastRequest == null
            || System.currentTimeMillis() - slot.lastRequest >= 1000) {
                Messages.sendToServer(new ClientDataManagerMessageToServer(slotNum, id));
                slot.lastRequest = System.currentTimeMillis();
                return null;
            }
        }
        return slot.data;
    }

    public void unsafeWrite(Object data, FriendlyByteBuf buf) {
        toBytes((TData)data, buf);
    }

    public abstract TData handle(NetworkEvent.Context ctx, Integer slot);
    public abstract void toBytes(TData data, FriendlyByteBuf buf);
    public abstract TData fromBytes(FriendlyByteBuf buf);
}
