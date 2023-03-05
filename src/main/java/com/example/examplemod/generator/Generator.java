package com.example.examplemod.generator;

import java.util.HashMap;
import java.util.Map;

public abstract class Generator<T> {

    private T value = null;

    public Generator() {}

    static <T> ValueGenerator<T> value(T value) {
        return new ValueGenerator<T>(value);
    }

    protected Map<String, Generator<? extends Object>> getSubgenerators() {
        return new HashMap<String, Generator<? extends Object>>();
    }

    protected abstract T build(Map<String, Object> subvalues);

    public T generate() {
        if(value != null) {
            return value;
        }
        Map<String, Generator<? extends Object>> sub = getSubgenerators();
        HashMap<String, Object> subValues = new HashMap<String, Object>();
        for(String key : sub.keySet()) {
            subValues.put(key, sub.get(key).generate());
        }
        value = build(subValues);
        return value;
    }

}
