package com.example.currencyexchange.data.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConversionResponse {
    private long transactionId;
    private BigDecimal targetAmount;
}
