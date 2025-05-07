package com.example.plagiarismapp.handler;


import com.example.plagiarismapp.exception.InvalidValueException;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.exception.ProcessGitEcxeption;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundByIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFoundException(NotFoundByIdException ex) {
        return new ErrorResponse("Entity not found", ex.getMessage());
    }

    @ExceptionHandler(InvalidValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidValueException(InvalidValueException ex) {
        return new ErrorResponse("Invalid value", ex.getMessage());
    }

    @ExceptionHandler(NotFoundResourceByIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleNotFoundException(NotFoundResourceByIdException ex) {
        return new ErrorResponse("Resource not found", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();
    }

    @ExceptionHandler(ProcessGitEcxeption.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleProcessGitEcxeption(ProcessGitEcxeption ex) {
        return new ErrorResponse("Git process error", ex.getMessage());
    }
}