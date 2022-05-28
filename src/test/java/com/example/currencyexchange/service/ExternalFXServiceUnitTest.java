package com.example.currencyexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.response.external.ConvertResponse;
import com.example.currencyexchange.exception.CurrencyException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@SpringBootTest
class ExternalFXServiceUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalFXService fxService;


    @Test
    void testGetExchangeRateHappyPath() {
        ConvertResponse mockResponse = new ConvertResponse();
        mockResponse.setSuccess(true);
        mockResponse.setResult(new BigDecimal(17));

        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(mockResponse);

        BigDecimal result = fxService.getExchangeRate("USD", "EUR");

        assertThat(result).isEqualTo(new BigDecimal(17));
    }

    @Test
    void testGetExchangeRateCurrencyFormatInteger() {
        String wrongFormatCurrency = "U1D";

        CurrencyException exception = assertThrows(CurrencyException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, "EUR"));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.CURRENCY_FORMAT_ERROR.getCode());
    }

    @Test
    void testGetExchangeRateCurrencyFormatLowerCase() {
        String wrongFormatCurrency = "UsD";

        CurrencyException exception = assertThrows(CurrencyException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, "EUR"));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.CURRENCY_FORMAT_ERROR.getCode());
    }

    @Test
    void testGetExchangeRateCurrencyFormatLengthLong() {
        String wrongFormatCurrency = "USDA";

        CurrencyException exception = assertThrows(CurrencyException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, "EUR"));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.CURRENCY_FORMAT_ERROR.getCode());
    }

    @Test
    void testGetExchangeRateCurrencyFormatLengthShort() {
        String wrongFormatCurrency = "U";

        CurrencyException exception = assertThrows(CurrencyException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, "EUR"));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.CURRENCY_FORMAT_ERROR.getCode());
    }

    @Test
    void testGetExchangeRateCurrencyNotFound() {
        ConvertResponse mockResponse = new ConvertResponse();
        mockResponse.setSuccess(true);
        // result should be null
        String notUsedCurrency = "AAA";

        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(mockResponse);

        CurrencyException exception = assertThrows(CurrencyException.class, () -> fxService.getExchangeRate(notUsedCurrency, "EUR"));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.CURRENCY_NOT_FOUND.getCode());
    }

    @Test
    void testGetExchangeRateExternalServerError() {
        ConvertResponse mockResponse = new ConvertResponse();
        mockResponse.setSuccess(false);

        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(mockResponse);

        CurrencyException exception = assertThrows(CurrencyException.class, () -> fxService.getExchangeRate("USD", "EUR"));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.EXTERNAL_SERVICE_ERROR.getCode());
    }

}