package com.example.currencyexchange.exception;

import com.example.currencyexchange.data.model.response.ErrorResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FormatException extends RuntimeException {
    private ErrorResponse errorResponse;
}
