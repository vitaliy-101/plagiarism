package com.example.plagiarismapp.exception;

public class NotFoundByIdException extends RuntimeException {
    public <T> NotFoundByIdException(Class<T> clazz, Long id){
        super(clazz.getSimpleName() + " by id=" + id + " not found");
    }
}

