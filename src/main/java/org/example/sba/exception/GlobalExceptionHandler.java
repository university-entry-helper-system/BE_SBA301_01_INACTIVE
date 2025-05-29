package org.example.sba.exception;

import org.example.sba.dto.response.ResponseData;
import org.example.sba.dto.response.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("; ");
        }
        log.error("Validation error: {}", errorMessage.toString());
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), errorMessage.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<?> handleJsonParseException(HttpMessageNotReadableException ex) {
        log.error("JSON parse error: {}", ex.getMessage(), ex);
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Invalid input data format. Please check your input.");
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<?> handleInvalidDataException(InvalidDataException ex) {
        log.error("Invalid data error: {}", ex.getMessage(), ex);
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseData<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        return new ResponseError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseData<?> handleAllExceptions(Exception ex) {
        log.error("Unhandled error: {}", ex.getMessage(), ex);
        return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
    }
}