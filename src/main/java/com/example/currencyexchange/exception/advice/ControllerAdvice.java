package com.example.currencyexchange.exception.advice;

import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.exception.CurrencyException;
import com.example.currencyexchange.exception.ExternalServiceFaultException;
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

    @ExceptionHandler(ExternalServiceFaultException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceExceptions(ExternalServiceFaultException e){
        log.error(e.toString());
        return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
      log.error(e.toString()); // TODO: !!!!
      return new ResponseEntity<>( new ErrorResponse(-1, "Unexpected error. Please contact support team."), HttpStatus.BAD_REQUEST);
    }



}
