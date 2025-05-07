package com.example.plagiarismapp.exception;

public class NotFoundResourceByIdException extends RuntimeException {
    public <T, U> NotFoundResourceByIdException(Class<T> clazzFirst, Long idFirst, Class<U> clazzSecond, Long idSecond){
        super(clazzFirst.getSimpleName() + " by id=" + idFirst + " does not have " + clazzSecond.getSimpleName() + " by id=" + idSecond);
    }
}