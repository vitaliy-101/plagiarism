package com.example.plagiarismapp.exception;

public class NotFoundResourceByIdException extends RuntimeException {
    public NotFoundResourceByIdException(Class<?> clazzFirst, Long idFirst, Class<?> clazzSecond, Long idSecond){
        super(clazzFirst.getSimpleName() + " by id=" + idFirst + " does not have " + clazzSecond.getSimpleName() + " by id=" + idSecond);
    }
}