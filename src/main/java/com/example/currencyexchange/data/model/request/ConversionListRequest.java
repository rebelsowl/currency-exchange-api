package com.example.currencyexchange.data.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ConversionListRequest {
    @Schema(example = "1")
    private Long transactionId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2022-05-29")
    private LocalDate transactionDate;
}
