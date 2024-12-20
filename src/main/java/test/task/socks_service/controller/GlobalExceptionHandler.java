package test.task.socks_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import test.task.socks_service.exception.NoEnoughSocksException;
import test.task.socks_service.exception.NoSocksFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSocksFoundException.class)
    public ResponseEntity<String> handleNoSocksFound(NoSocksFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoEnoughSocksException.class)
    public ResponseEntity<String> handleNoEnoughSocks(NoEnoughSocksException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка: " + ex.getMessage());
    }
}