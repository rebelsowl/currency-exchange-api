package com.example.currencyexchange.exception.advice;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.exception.CurrencyException;
import com.example.currencyexchange.exception.ExternalServiceFaultException;
import com.example.currencyexchange.exception.FormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    // BAD REQUEST
    @ExceptionHandler(FormatException.class)
    public ResponseEntity<ErrorResponse> handleFormatExceptions(FormatException e){
        return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrencyException.class)
    public ResponseEntity<ErrorResponse> handleCurrencyExceptions(CurrencyException e){
        return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleCurrencyExceptions(Exception e){
        return new ResponseEntity<>(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // EXTERNAL SERVICES
    @ExceptionHandler(ExternalServiceFaultException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceExceptions(ExternalServiceFaultException e){
        log.error(e.toString());
        return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.SERVICE_UNAVAILABLE);
    }


    // GENERAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
      log.error(e.toString()); // TODO: !!!!
      return new ResponseEntity<>( new ErrorResponse(ErrorCodes.UNEXPECTED_ERROR.getCode(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
