package com.example.examplemod.networking.subscribe;

import java.util.ArrayList;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class ServerSubscription<TData extends Versionable> {

    private final SubscriptionBroker<TData> manager;
    private final Integer dataId;
    private DataVersion dataVersion;
    private final Consumer<TData> consumer;
    
    public ServerSubscription(SubscriptionBroker<TData> manager, Integer dataId, Consumer<TData> consumer) {
        this.manager = manager;
        this.dataId = dataId;
        this.consumer = consumer;
    }

    public void unsubscribe() {
        manager.unsubscribe(this);
    }

    public void publish(TData data) {
        DataVersion version = data.getVersion();
        if(dataVersion != null && dataVersion.equals(version)) return;
        dataVersion = version;
        consumer.accept(data);
    }

    public Integer getDataId() {
        return dataId;
    }

}
