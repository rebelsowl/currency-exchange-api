package com.example.currencyexchange.data.model.response.external;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ConvertResponse {
    private boolean success;
    private BigDecimal result;

}
