package com.example.examplemod.networking.subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

class ClientSideSubscriptions<TData> {
    private final SubscriptionBroker<TData> manager;
    private final Integer dataId;
    private final ArrayList<ServerSubscription<TData>> subscribers = new ArrayList<>();

    public ClientSideSubscriptions(SubscriptionBroker<TData> manager, Integer dataId) {
        this.manager = manager;
        this.dataId = dataId;
    }

    public void publish(TData data) {
        for(ServerSubscription<TData> subscriber : subscribers) {
            subscriber.publish(data);
        }
    }
    
    public ServerSubscription<TData> subscribe() {
        ServerSubscription<TData> subscription = new ServerSubscription<TData>(manager, dataId);
        subscribers.add(subscription);
        return subscription;
    }
    
    public void unsubscribe(ServerSubscription<TData> subscription) {
        subscribers.remove(subscription);
    }

    public boolean isEmpty() {
        return subscribers.isEmpty();
    }
}

class ServerSideSubscriptions<TData> {
    private TData data;
    private HashSet<ServerPlayer> subscribers = new HashSet<>();
    
    public boolean subscribe(ServerPlayer subscriberId) {
        return subscribers.add(subscriberId);
    }
    
    public void unsubscribe(ServerPlayer subscriberId) {
        subscribers.remove(subscriberId);
    }

    public boolean isEmpty() {
        return subscribers.isEmpty();
    }

    public void setData(TData data) {
        this.data = data;
    }

    public TData getData() {
        return data;
    }

    public Set<ServerPlayer> getSubscribers() {
        return subscribers;
    }
}

public abstract class SubscriptionBroker<TData> {

    private ResourceLocation id;
    private HashMap<Integer, ClientSideSubscriptions<TData>> clientSideSubscriptions = new HashMap<>();

    private HashMap<Integer, ServerSideSubscriptions<TData>> serverSideSubscriptions = new HashMap<>();
    
    public SubscriptionBroker(ResourceLocation id) {
        this.id = id;
    }

    public void serverRegisterSubscriber(ServerPlayer subscriber, Integer dataId) {
        ServerSideSubscriptions<TData> subscriptions = serverSideSubscriptions.get(dataId);
        if (subscriptions == null) {
            subscriptions = new ServerSideSubscriptions<TData>();
            serverSideSubscriptions.put(dataId, subscriptions);
        }
        subscriptions.subscribe(subscriber);
        TData data = subscriptions.getData();
        if(data == null) {
            subscriptions.setData(getData(subscriber, dataId));
            data = subscriptions.getData();
        }
        if(data != null) {
            SubscriptionMessages.sendToPlayer(new SubscriptionPayload(id, dataId, data), subscriber);
        }
    }

    public void serverUnregisterSubscriber(ServerPlayer subscriber, Integer dataId) {
        ServerSideSubscriptions<TData> subscriptions = serverSideSubscriptions.get(dataId);
        if (subscriptions == null) return;
        subscriptions.unsubscribe(subscriber);
        if (subscriptions.isEmpty()) {
            serverSideSubscriptions.remove(dataId);
        }
    }
    
    // Only call from client.
    public ServerSubscription<TData> subscribe(Integer dataId) {
        ClientSideSubscriptions<TData> subscriptions = clientSideSubscriptions.get(dataId);
        if (subscriptions == null) {
            subscriptions = new ClientSideSubscriptions<TData>(this, dataId);
            clientSideSubscriptions.put(dataId, subscriptions);
        }
        SubscriptionMessages.sendToServer(new SubscribeRequest(dataId, id));
        return subscriptions.subscribe();
    }
    
    // Only call from client.
    public void unsubscribe(ServerSubscription<TData> subscription) {
        ClientSideSubscriptions<TData> subscriptions = clientSideSubscriptions.get(subscription.getDataId());
        if (subscriptions == null) return;
        subscriptions.unsubscribe(subscription);
        if (subscriptions.isEmpty()) {
            clientSideSubscriptions.remove(subscription.getDataId());
            SubscriptionMessages.sendToServer(new UnsubscribeRequest(subscription.getDataId(), id));
        }
    }

    public void unsafeClientReceivePayload(Integer dataId, Object data) {
        clientReceivePayload(dataId, (TData) data);
    }

    public void clientReceivePayload(Integer dataId, TData data) {
        ClientSideSubscriptions<TData> subscriptions = clientSideSubscriptions.get(dataId);
        if (subscriptions == null) return;
        subscriptions.publish(data);
    }
    
    // Only call from server.
    public void publish(Integer dataId, TData data) {
        ServerSideSubscriptions<TData> subscriptions = serverSideSubscriptions.get(dataId);
        if(subscriptions == null) return;
        subscriptions.setData(data);
        for(ServerPlayer player : subscriptions.getSubscribers()) {
            SubscriptionMessages.sendToPlayer(new SubscriptionPayload(id, dataId, data), player);
        }
    }

    public void unsafeToBytes(Object data, FriendlyByteBuf buf) {
        toBytes((TData)data, buf);
    }

    public abstract TData getData(ServerPlayer player, Integer dataId);
    public abstract void toBytes(TData data, FriendlyByteBuf buf);
    public abstract TData fromBytes(FriendlyByteBuf buf);
}
