package com.example.examplemod.widgets;

import java.util.function.Supplier;

public class ConditionalWidget extends ModWidget {

    private Supplier<Boolean> condition;

    public ConditionalWidget(ModWidget parent, Supplier<Boolean> condition) {
        super(parent);
        this.condition = condition;
    }
    
    @Override
    public boolean getActive() {
        setLayoutDirty();
        return condition.get() && super.getActive();
    }
}
