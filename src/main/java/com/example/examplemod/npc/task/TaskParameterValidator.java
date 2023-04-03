package com.example.examplemod.npc.task;

import java.util.function.Function;

public class TaskParameterValidator<TValue> {
    private String message;
    private Function<TValue, Boolean> validator;

    public TaskParameterValidator(String message, Function<TValue, Boolean> validator) {
        this.message = message;
        this.validator = validator;
    }

    public String getMessage() {
        return message;
    }

    public boolean validate(TValue value) {
        return validator.apply(value);
    }
}
