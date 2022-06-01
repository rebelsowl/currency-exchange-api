package com.example.currencyexchange.data.model.response.external;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConvertResponse {
    private boolean success;
    private BigDecimal result;

}
