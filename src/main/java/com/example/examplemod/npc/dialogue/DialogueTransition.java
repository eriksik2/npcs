package com.example.examplemod.npc.dialogue;

public class DialogueTransition<TData, TTransitionData> {
    
    private DialogueNodeBuilder<TData, TTransitionData> to;
    private TTransitionData data;
    
    public DialogueTransition(TTransitionData data, DialogueNodeBuilder<TData, TTransitionData> to) {
        this.to = to;
        this.data = data;
    }
    
    public DialogueNode<TData, TTransitionData> getTo() {
        return to.build();
    }
    
    public TTransitionData getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DialogueTransition) {
            DialogueTransition<TData, TTransitionData> other = (DialogueTransition<TData, TTransitionData>) obj;
            return other.to == to && other.data == data;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int toHc = to == null ? 0 : to.hashCode();
        int dataHc = data == null ? 0 : data.hashCode();
        return toHc ^ dataHc;
    }
}
