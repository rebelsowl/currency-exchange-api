package com.example.currencyexchange.data.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class ConversionRequest {
    @Schema(example = "USD")
    @Pattern(regexp = "^[A-Z]{3}$")
    private String sourceCurrency;

    @DecimalMin("0.1")
    private BigDecimal sourceAmount;

    @Schema(example = "EUR")
    @Pattern(regexp = "^[A-Z]{3}$")
    private String targetCurrency;

}
