package com.example.examplemod.widgets;

public class DebugWidget extends ModWidget {

    private ScrollableListWidget list;
    private ModWidget target;

    public DebugWidget(ModWidget parent, ModWidget target) {
        super(parent);
        this.target = target;
    }

    @Override
    public void onInit() {
        target.addListener(this);
        list = new ScrollableListWidget(this);
    }

    @Override
    public void onDeinit() {
        target.removeListener(this);
    }

    @Override
    public void onRelayoutPre() {
        list.clearChildren();
        String debugString = target.getDebugString();
        String[] lines = debugString.split("\n");
        for(String line : lines) {
            TextWidget text = new TextWidget(list, line);
            list.addChild(text);
        }
        list.setWidth(getInnerWidth());
        list.setHeight(getInnerHeight());
    }
    
}
