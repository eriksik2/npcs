package com.example.examplemod.widgets;

public class RowLayoutWidget extends ModWidget {
    private int gap = 0;
    private boolean wrap = false;
    private int rowGap = 0;

    public RowLayoutWidget(ModWidget parent) {
        super(parent);
    }

    @Override
    public void onRelayoutPost() {
        if(!getWrap()) {
            int x = 0;
            for(ModWidget child : children){
                child.setX(x);
                x += child.getWidth() + gap;
            }
            if(children.size() != 0) x -= gap;
            setInnerWidth(x);
        } else {
            int x = 0;
            int y = 0;
            int rows = 1;
            int rowHeight = 0;
            for(ModWidget child : children){
                if(x + child.getWidth() > getInnerWidth()) {
                    x = 0;
                    y += rowHeight + rowGap;
                    rowHeight = 0;
                    rows++;
                }
                child.setX(x);
                child.setY(y);
                x += child.getWidth() + gap;
                rowHeight = Math.max(rowHeight, child.getHeight());
            }
            y += rowHeight + rowGap;
            if(rows != 1) y -= rowGap;
            setInnerHeight(y);
        }
    }

    public void setGap(int gap) {
        this.gap = gap;
        setLayoutDirty();
    }

    public int getGap() {
        return gap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
        setLayoutDirty();
    }

    public boolean getWrap() {
        return wrap;
    }

    public void setRowGap(int rowGap) {
        this.rowGap = rowGap;
        setLayoutDirty();
    }

    public int getRowGap() {
        return rowGap;
    }

    @Override
    public boolean layoutBasedOnChildren() {
        return true;
    }
}
