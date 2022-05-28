package com.example.currencyexchange.exception.advice;

import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.exception.CurrencyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(CurrencyException.class)
    public ResponseEntity<ErrorResponse> handleCurrencyExceptions(CurrencyException e){
        log.error(e.toString());
        return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
      log.error(e.toString()); // TODO: !!!!
      return new ResponseEntity<>( new ErrorResponse(1, "asdf"), HttpStatus.BAD_REQUEST);
    }



}
