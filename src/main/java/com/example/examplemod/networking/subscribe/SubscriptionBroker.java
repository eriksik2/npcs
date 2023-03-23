package com.example.examplemod.networking.subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

// TODO
// This class could hold the latest copy of the data.
// Whenever a subscribe request is sent to the server, we could send with it the DataVersion of the data the client has.
// If the server has a newer version, it could send the new data to the client.
// New subscribers on the client could then use the data that is immediately available, and the server would send the latest
// data soon after, if needed.
class ClientSideSubscriptions<TData extends Versionable> {
    private final SubscriptionBroker<TData> manager;
    private final Integer dataId;
    private TData data;
    private final ArrayList<ServerSubscription<TData>> subscribers = new ArrayList<>();
    private final ArrayList<ServerSubscription<TData>> pendingRemoval = new ArrayList<>();

    public ClientSideSubscriptions(SubscriptionBroker<TData> manager, Integer dataId) {
        this.manager = manager;
        this.dataId = dataId;
    }

    public void publish(TData data) {
        this.data = data;
        for(ServerSubscription<TData> subscription : pendingRemoval) {
            subscribers.remove(subscription);
        }
        pendingRemoval.clear();
        for(ServerSubscription<TData> subscriber : subscribers) {
            subscriber.publish(data);
        }
    }
    
    public ServerSubscription<TData> subscribe(Consumer<TData> consumer) {
        ServerSubscription<TData> subscription = new ServerSubscription<TData>(manager, dataId, consumer);
        subscribers.add(subscription);
        if(data != null) subscription.publish(data);
        return subscription;
    }
    
    public void unsubscribe(ServerSubscription<TData> subscription) {
        pendingRemoval.add(subscription);
    }

    public boolean isEmpty() {
        return subscribers.isEmpty();
    }
}

class ServerSideSubscriptions<TData extends Versionable> {
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

    public Set<ServerPlayer> getSubscribers() {
        return subscribers;
    }
}

public abstract class SubscriptionBroker<TData extends Versionable> {

    private ResourceLocation id;
    private HashMap<Integer, ClientSideSubscriptions<TData>> clientSideSubscriptions = new HashMap<>();

    private HashMap<Integer, ServerSideSubscriptions<TData>> serverSideSubscriptions = new HashMap<>();

    private final Logger logger;
    
    public SubscriptionBroker(ResourceLocation id) {
        this.id = id;
        this.logger = LogUtils.getLogger();
    }

    public void serverRegisterSubscriber(ServerPlayer subscriber, Integer dataId) {
        ServerSideSubscriptions<TData> subscriptions = serverSideSubscriptions.get(dataId);
        TData initialData = null;
        if (subscriptions == null) {
            subscriptions = new ServerSideSubscriptions<TData>();
            serverSideSubscriptions.put(dataId, subscriptions);
            initialData = getData(subscriber, dataId);
        }
        subscriptions.subscribe(subscriber);
        if(initialData != null) {
            SubscriptionMessages.sendToPlayer(new SubscriptionPayload(id, dataId, initialData), subscriber);
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
    public ServerSubscription<TData> subscribe(Integer dataId, Consumer<TData> consumer) {
        ClientSideSubscriptions<TData> subscriptions = clientSideSubscriptions.get(dataId);
        if (subscriptions == null) {
            subscriptions = new ClientSideSubscriptions<TData>(this, dataId);
            clientSideSubscriptions.put(dataId, subscriptions);
            SubscriptionMessages.sendToServer(new SubscribeRequest(dataId, id));
        }
        return subscriptions.subscribe(consumer);
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
        if (subscriptions == null) {
            logger.warn("Received data for unsubscribed dataId: " + dataId + " in SubscriptionBroker with id " + id + ".");
            return;
        }
        subscriptions.publish(data);
    }
    
    // Only call from server.
    public void publish(Integer dataId, TData data) {
        ServerSideSubscriptions<TData> subscriptions = serverSideSubscriptions.get(dataId);
        if(subscriptions == null) return;
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
