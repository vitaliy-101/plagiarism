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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<ErrorResponse> handleInvalidValueException(InvalidValueException ex) {
        return Mono.just(new ErrorResponse("Invalid value", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundResourceByIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<ErrorResponse> handleNotFoundException(NotFoundResourceByIdException ex) {
        return Mono.just(new ErrorResponse("Resource not found", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Flux<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return Flux.fromStream(
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> new ErrorResponse(error.getField(), error.getDefaultMessage()))
        );

    }

    @ExceptionHandler(ProcessGitEcxeption.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Mono<ErrorResponse> handleProcessGitEcxeption(ProcessGitEcxeption ex) {
        return Mono.just(new ErrorResponse("Git process error", ex.getMessage()));
    }
}