package com.example.examplemod.networking.subscribe;

import java.util.ArrayList;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class ServerSubscription<TData> {

    private final SubscriptionBroker<TData> manager;
    private final Integer dataId;
    private final ArrayList<Consumer<TData>> consumers = new ArrayList<>();
    
    public ServerSubscription(SubscriptionBroker<TData> manager, Integer dataId) {
        this.manager = manager;
        this.dataId = dataId;
    }

    public void deinit() {
        consumers.clear();
        manager.unsubscribe(this);
    }

    public void publish(TData data) {
        for(Consumer<TData> consumer : consumers) {
            consumer.accept(data);
        }
    }

    public Integer getDataId() {
        return dataId;
    }

    public void addListener(Consumer<TData> consumer) {
        consumers.add(consumer);
    }

    public void removeListener(Consumer<TData> consumer) {
        consumers.remove(consumer);
    }

}
