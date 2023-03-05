package com.example.examplemod.generator;

import java.util.Map;

public class ValueGenerator<T> extends Generator<T> {
    private T value;
    public ValueGenerator(T value) {
        this.value = value;
    }
    @Override
    protected T build(Map<String, Object> subvalues) {
        return value;
    }
}
