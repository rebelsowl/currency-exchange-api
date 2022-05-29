package com.example.currencyexchange.controller;

import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.service.Interface.IExternalFXService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@Tag(name = "exchange rate api")
public class ExchangeRateController {
    private final IExternalFXService fxService;

    public ExchangeRateController(IExternalFXService externalFXService) {
        fxService = externalFXService;
    }

    @Operation(summary = "retrieves current exchange rate of the currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the exchange rate",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BigDecimal.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid currency",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping("/exchange-rate")
    public ResponseEntity<BigDecimal> getExchangeRate(@Parameter(description = "Source currency. Should be 3 letters all uppercase valid currency", example = "USD") @RequestParam String from, @Parameter(description = "Target currency. Should be 3 letters all uppercase valid currency", example = "EUR") @RequestParam String to) {
        return ResponseEntity.ok(fxService.getExchangeRate(from, to));
    }

}
