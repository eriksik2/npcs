package com.example.examplemod.npc.dialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.util.TriConsumer;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

interface DialogueNodeBuilder<TData, TTransitionData> {
    DialogueNode<TData, TTransitionData> build();
}

public interface DialogueNode<TData, TTransitionData> extends DialogueNodeBuilder<TData, TTransitionData> {

    public void onEnter(TData from, TTransitionData transition);
    public void onLeave(TTransitionData transition, TData to);

    public boolean canEnter(TData from, TTransitionData transition);
    public boolean canLeave(TTransitionData transition, TData to);

    public TData getData();

    public List<DialogueTransition<TData, TTransitionData>> getTransitions();

    @Override
    default DialogueNode<TData, TTransitionData> build() {
        return this;
    }

    static class Builder<TData, TTransitionData> implements DialogueNodeBuilder<TData, TTransitionData> {
        private ArrayList<TTransitionData> deferredTransitionsData = new ArrayList<>();
        private ArrayList<Builder<TData, TTransitionData>> deferredTransitions = new ArrayList<>();
        private ArrayList<DialogueTransition<TData, TTransitionData>> transitions = new ArrayList<>();
        private Supplier<TData> getData = () -> null;
        private TriConsumer<TData, TTransitionData, TData> onEnter = (from, transition, to) -> {};
        private TriConsumer<TData, TTransitionData, TData> onLeave = (from, transition, to) -> {};
        private TriFunction<TData, TTransitionData, TData, Boolean> canEnter = (from, transition, to) -> true;
        private TriFunction<TData, TTransitionData, TData, Boolean> canLeave = (from, transition, to) -> true;
        private DialogueNode<TData, TTransitionData> builtNode;

        public Builder() {
        }

        public Builder<TData, TTransitionData> withTransition(TTransitionData data, DialogueNode<TData, TTransitionData> node) {
            transitions.add(new DialogueTransition<TData,TTransitionData>(data, node));
            return this;
        }

        public Builder<TData, TTransitionData> withTransition(TTransitionData data, Builder<TData, TTransitionData> builder) {
            deferredTransitionsData.add(data);
            deferredTransitions.add(builder);
            return this;
        }

        public Builder<TData, TTransitionData> withData(TData data) {
            this.getData = () -> data;
            return this;
        }

        public Builder<TData, TTransitionData> withData(Supplier<TData> getData) {
            this.getData = getData;
            return this;
        }

        public Builder<TData, TTransitionData> onEnter(TriConsumer<TData, TTransitionData, TData> onEnter) {
            this.onEnter = onEnter;
            return this;
        }

        public Builder<TData, TTransitionData> onEnter(Runnable onEnter) {
            this.onEnter = (from, transition, to) -> onEnter.run();
            return this;
        }

        public Builder<TData, TTransitionData> onLeave(TriConsumer<TData, TTransitionData, TData> onLeave) {
            this.onLeave = onLeave;
            return this;
        }

        public Builder<TData, TTransitionData> onLeave(Runnable onLeave) {
            this.onLeave = (from, transition, to) -> onLeave.run();
            return this;
        }

        public Builder<TData, TTransitionData> canEnterIf(TriFunction<TData, TTransitionData, TData, Boolean> canEnter) {
            this.canEnter = canEnter;
            return this;
        }

        public Builder<TData, TTransitionData> canEnterIf(Supplier<Boolean> canEnter) {
            this.canEnter = (from, transition, to) -> canEnter.get();
            return this;
        }

        public Builder<TData, TTransitionData> canLeaveIf(TriFunction<TData, TTransitionData, TData, Boolean> canLeave) {
            this.canLeave = canLeave;
            return this;
        }

        public Builder<TData, TTransitionData> canLeaveIf(Supplier<Boolean> canLeave) {
            this.canLeave = (from, transition, to) -> canLeave.get();
            return this;
        }

        public DialogueNode<TData, TTransitionData> build() {
            if(builtNode != null) {
                return builtNode;
            }
            builtNode = new DialogueNode<TData, TTransitionData>() {
                @Override
                public void onEnter(TData from, TTransitionData transition) {
                    onEnter.accept(from, transition, getData.get());
                }

                @Override
                public void onLeave(TTransitionData transition, TData to) {
                    onLeave.accept(getData.get(), transition, to);
                }

                @Override
                public boolean canEnter(TData from, TTransitionData transition) {
                    return canEnter.apply(from, transition, getData.get());
                }

                @Override
                public boolean canLeave(TTransitionData transition, TData to) {
                    return canLeave.apply(getData.get(), transition, to);
                }

                @Override
                public TData getData() {
                    return getData.get();
                }

                @Override
                public List<DialogueTransition<TData, TTransitionData>> getTransitions() {
                    return transitions;
                }
            };
            for(int i = 0; i < deferredTransitions.size(); i++) {
                var data = deferredTransitionsData.get(i);
                var builder = deferredTransitions.get(i);
                transitions.add(new DialogueTransition<TData,TTransitionData>(data, builder.build()));
            }
            return builtNode;
        }
    }
}
