package com.example.examplemod.generator;

import java.util.Map;

public class UniformGenerator extends Generator<Double> {

    private double min;
    private double max;

    public UniformGenerator(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    protected Double build(Map<String, Object> subvalues) {
        double t = Math.random();
        return (max - min)*t + min;
    }
    
}
