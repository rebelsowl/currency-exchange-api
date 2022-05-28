package com.example.currencyexchange.controller;

import com.example.currencyexchange.service.Interface.IExternalFXService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class ExchangeRateController {
    private final IExternalFXService fxService;

    public ExchangeRateController(IExternalFXService externalFXService) {
        fxService = externalFXService;
    }

    // TODO: outputu icin dto lar olusturulucak
    @GetMapping("/exchange-rate")
    public ResponseEntity<BigDecimal> getExchangeRate(@RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(fxService.getExchangeRate(from, to));
    }

}
