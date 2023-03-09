package com.example.examplemod.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.RegistryObject;

class Slot<TData> {
    public TData data = null;
    public Integer dataHash = null;
    public Long lastRequest = 0l;
    public HashSet<Consumer<TData>> callbacks = new HashSet<>();
}

public abstract class ServerToClientBroker<TData> {
    private HashMap<Integer, Slot<TData>> slots = new HashMap<Integer, Slot<TData>>();

    private ResourceLocation id;

    public ServerToClientBroker(ResourceLocation id) {
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
        slot.dataHash = hashCode(slot.data);
        for(Consumer<TData> callback : slot.callbacks) {
            callback.accept(slot.data);
        }
        slot.callbacks.clear();
    }

    public void resolveCallbacks(Integer handle) {
        Slot<TData> slot = slots.get(handle);
        if(slot == null) return;
        for(Consumer<TData> callback : slot.callbacks) {
            callback.accept(slot.data);
        }
        slot.callbacks.clear();
    }

    public TData get(Integer slotNum, Consumer<TData> callback) {
        Slot<TData> slot = slots.get(slotNum);
        if(slot == null) {
            slot = new Slot<TData>();
            slots.put(slotNum, slot);
        }
        if(slot.lastRequest == null
        || System.currentTimeMillis() - slot.lastRequest >= 1000) {
            // Data is stale, request new data from server
            Messages.sendToServer(new ServerToClientBrokerRequest(slotNum, slot.dataHash, id));
            slot.lastRequest = System.currentTimeMillis();
            if(callback != null) slot.callbacks.add(callback);
            return slot.data;
        } else {
            // Data is fresh
            if(slot.data == null) {
                // Data is not yet available
                if(callback != null) slot.callbacks.add(callback);
            } else {
                // Data is available
                if(callback != null) callback.accept(slot.data);
            }
        }
        return slot.data;
    }

    public TData get(Integer slotNum) {
        return get(slotNum, null);
    }

    public void unsafeWrite(Object data, FriendlyByteBuf buf) {
        toBytes((TData)data, buf);
    }

    public abstract TData handle(NetworkEvent.Context ctx, Integer slot);
    public abstract int hashCode(TData data);
    public abstract void toBytes(TData data, FriendlyByteBuf buf);
    public abstract TData fromBytes(FriendlyByteBuf buf);
}
