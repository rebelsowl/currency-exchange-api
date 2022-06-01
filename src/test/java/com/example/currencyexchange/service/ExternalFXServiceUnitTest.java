package com.example.currencyexchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.response.external.ConvertResponse;
import com.example.currencyexchange.exception.CurrencyException;
import com.example.currencyexchange.exception.ExternalServiceFaultException;
import com.example.currencyexchange.exception.FormatException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@SpringBootTest
public class ExternalFXServiceUnitTest {
    private static final String sourceCurrency = "USD";
    private static final String targetCurrency = "EUR";

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

        BigDecimal result = fxService.getExchangeRate(sourceCurrency, targetCurrency);

        assertThat(result).isEqualTo(new BigDecimal(17));
    }

    @Test
    void testGetExchangeRateCurrencyFormatInteger() {
        String wrongFormatCurrency = "U1D";

        FormatException exception = assertThrows(FormatException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, targetCurrency));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(exception.getErrorResponse().getMessage()).startsWith("Currency should be 3 letters long and all uppercase ex: USD, EUR");
    }

    @Test
    void testGetExchangeRateCurrencyFormatLowerCase() {
        String wrongFormatCurrency = "UsD";

        FormatException exception = assertThrows(FormatException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, targetCurrency));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
//        assertThat(exception.getErrorResponse().getMessage()).startsWith("Currency should be 3 letters long and all uppercase ex: USD, EUR");
    }

    @Test
    void testGetExchangeRateCurrencyFormatLengthLong() {
        String wrongFormatCurrency = "USDA";

        FormatException exception = assertThrows(FormatException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, targetCurrency));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
//        assertThat(exception.getErrorResponse().getMessage()).startsWith("Currency should be 3 letters long and all uppercase ex: USD, EUR");
    }

    @Test
    void testGetExchangeRateCurrencyFormatLengthShort() {
        String wrongFormatCurrency = "U";

        FormatException exception = assertThrows(FormatException.class, () -> fxService.getExchangeRate(wrongFormatCurrency, targetCurrency));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
//        assertThat(exception.getErrorResponse().getMessage()).startsWith("Currency should be 3 letters long and all uppercase ex: USD, EUR");
    }

    @Test
    void testGetExchangeRateCurrencyNotFound() {
        ConvertResponse mockResponse = new ConvertResponse();
        mockResponse.setSuccess(true);
        // result should be null -> don't set result
        String notUsedCurrency = "AAA";

        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(mockResponse);

        CurrencyException exception = assertThrows(CurrencyException.class, () -> fxService.getExchangeRate(notUsedCurrency, targetCurrency));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.CURRENCY_NOT_FOUND.getCode());
        assertThat(exception.getErrorResponse().getMessage()).startsWith("Currently we do not serve");
    }

    @Test
    void testGetExchangeRateExternalServerError() {
        ConvertResponse mockResponse = new ConvertResponse();
        mockResponse.setSuccess(false);

        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(mockResponse);

        ExternalServiceFaultException exception = assertThrows(ExternalServiceFaultException.class, () -> fxService.getExchangeRate(sourceCurrency, targetCurrency));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.EXTERNAL_SERVICE_ERROR.getCode());
        assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Our services are currently unavailable, please try again later.");
    }

    @Test
    void testGetExchangeRateExternalServerHttpError() {
        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenThrow(HttpClientErrorException.class);

        ExternalServiceFaultException exception = assertThrows(ExternalServiceFaultException.class, () -> fxService.getExchangeRate("USD", "EUR"));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.EXTERNAL_SERVICE_ERROR.getCode());
        assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Our services are currently unavailable, please try again later.");
    }

}