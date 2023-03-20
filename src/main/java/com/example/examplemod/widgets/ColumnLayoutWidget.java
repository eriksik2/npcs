package com.example.examplemod.widgets;

public class ColumnLayoutWidget extends ModWidget {

    private int gap = 0;

    public ColumnLayoutWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onRelayoutPost() {
        int y = 0;
        for(ModWidget child : children){
            child.setY(y);
            y += child.getHeight() + gap;
        }
        if(children.size() != 0) y -= gap;
        setInnerHeight(y);
    }

    public void setGap(int gap) {
        this.gap = gap;
        setLayoutDirty();
    }

    public int getGap() {
        return gap;
    }
    
}
