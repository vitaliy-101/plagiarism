package com.example.plagiarismapp.exception;

public class InvalidValueException extends RuntimeException {
    public <T> InvalidValueException(Class<T> clazz) {
        super(clazz.getSimpleName());
    }
}
