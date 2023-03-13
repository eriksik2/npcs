package com.example.examplemod.npc.dialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.util.TriConsumer;

import com.google.common.base.Supplier;

public abstract class DialogueGraph<TNodeData, TTransitionData> {
    private TriConsumer<TNodeData, TTransitionData, TNodeData> onTransition = (from, transition, to) -> {};
    private ArrayList<DialogueNode<TNodeData, TTransitionData>> stack = new ArrayList<>();

    public DialogueGraph() {
        makeTransition(new DialogueTransition<TNodeData,TTransitionData>(null, buildDialogueGraph().build()));
    }

    protected abstract DialogueNodeBuilder<TNodeData, TTransitionData> buildDialogueGraph();

    public DialogueNode.Builder<TNodeData, TTransitionData> createNode() {
        return new DialogueNode.Builder<>();
    }
    public DialogueNode.Builder<TNodeData, TTransitionData> createNode(TNodeData data) {
        return new DialogueNode.Builder<TNodeData, TTransitionData>().withData(data);
    }
    public DialogueNode.Builder<TNodeData, TTransitionData> createNode(Supplier<TNodeData> getData) {
        return new DialogueNode.Builder<TNodeData, TTransitionData>().withData(getData);
    }

    private DialogueNode<TNodeData, TTransitionData> getCurrentNode() {
        if (stack.size() == 0) return null;
        return stack.get(stack.size() - 1);
    }

    public List<DialogueTransition<TNodeData, TTransitionData>> getTransitions() {
        var node = getCurrentNode();
        if (node == null) return null;
        var validTransitions = new ArrayList<DialogueTransition<TNodeData, TTransitionData>>();
        for (var transition : node.getTransitions()) {
            if (!transition.getTo().canEnter(getCurrentData(), transition.getData())) continue;
            if (!node.canLeave(transition.getData(), getCurrentData())) continue;
            validTransitions.add(transition);
        }
        return validTransitions;
    }

    public TNodeData getCurrentData() {
        final DialogueNode<TNodeData, TTransitionData> currentNode = getCurrentNode();
        if (currentNode == null) return null;
        return currentNode.getData();
    }

    public void go(int transitionIndex) {
        DialogueNode<TNodeData, TTransitionData> currentNode = getCurrentNode();
        List<DialogueTransition<TNodeData, TTransitionData>> transitions = currentNode.getTransitions();
        if (transitionIndex < 0 || transitionIndex >= transitions.size()) {
            throw new IndexOutOfBoundsException();
        }
        makeTransition(transitions.get(transitionIndex));
    }

    public void goBack() {
        if (stack.size() <= 1) {
            throw new IllegalStateException("Cannot go back from the start node");
        }
        makeTransition(new DialogueTransition<TNodeData,TTransitionData>(null, stack.get(stack.size() - 2)));
    }

    public boolean makeTransition(DialogueTransition<TNodeData, TTransitionData> transition) {
        var tranData = transition.getData();
        var node = transition.getTo();
        DialogueNode<TNodeData, TTransitionData> currentNode = getCurrentNode();
        TNodeData currentData = getCurrentData();
        if(!node.canEnter(currentData, tranData)) return false;
        if (currentNode != null) {
            if(!currentNode.canLeave(tranData, node.getData())) return false;
            currentNode.onLeave(tranData, node.getData());
        }
        int index = stack.indexOf(node);
        if(index != -1) {
            for(int i = stack.size() - 1; i >= index; i--) {
                stack.remove(i);
            }
        }
        stack.add(node);
        node.onEnter(currentData, tranData);
        onTransition(currentData, tranData, node.getData());
        this.onTransition.accept(currentData, tranData, node.getData());
        return true;
    }

    public void setOnTransition(TriConsumer<TNodeData, TTransitionData, TNodeData> onTransition) {
        this.onTransition = onTransition;
    }

    public void onTransition(TNodeData from, TTransitionData transition, TNodeData to) {
    }
}
