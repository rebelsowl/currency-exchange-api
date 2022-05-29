package com.example.currencyexchange.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.when;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import com.example.currencyexchange.data.model.response.external.ConvertResponse;
import com.example.currencyexchange.data.repository.ConversionRepository;
import com.example.currencyexchange.exception.CurrencyException;
import com.example.currencyexchange.exception.ExternalServiceFaultException;
import com.example.currencyexchange.exception.FormatException;
import com.example.currencyexchange.service.Interface.IExternalFXService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@SpringBootTest
class ConversionServiceUnitTest {

    @Mock
    private IExternalFXService fxService;

    @Mock
    private ConversionRepository conversionRepository;

    @InjectMocks
    private ConversionService conversionService;

    static String sourceCurrency;
    static String targetCurrency;
    static BigDecimal sourceAmount;
    static BigDecimal exchangeRate;
    static ConversionRequest mockRequest;


    @BeforeAll
    public static void setUp(){
        sourceCurrency = "USD";
        targetCurrency = "EUR";
        sourceAmount = new BigDecimal(12.1);
        exchangeRate = new BigDecimal(2.1);

        mockRequest = new ConversionRequest();
        mockRequest.setSourceCurrency(sourceCurrency);
        mockRequest.setTargetCurrency(targetCurrency);
        mockRequest.setSourceAmount(sourceAmount);
    }

    @Test
    public void testConversionHappyPath() {
        Conversion mockSavedConversion = new Conversion();
        mockSavedConversion.setId(1L);
        mockSavedConversion.setSourceCurrency(sourceCurrency);
        mockSavedConversion.setTargetCurrency(targetCurrency);
        mockSavedConversion.setSourceAmount(sourceAmount);
        mockSavedConversion.setTargetAmount(sourceAmount.multiply(exchangeRate));

        when(fxService.getExchangeRate(any(String.class), any(String.class))).thenReturn(exchangeRate);
        when(conversionRepository.save(any(Conversion.class))).thenReturn(mockSavedConversion);


        ConversionResponse response = conversionService.conversion(mockRequest);

        assertThat(response.getTransactionId()).isEqualTo(1L);
        assertThat(response.getTargetAmount()).isEqualTo(sourceAmount.multiply(exchangeRate));
    }

    @Test
    public void testConversionNegativeAmount() {
        mockRequest.setSourceAmount(new BigDecimal(-999999999));

        FormatException exception = assertThrows(FormatException.class, () -> conversionService.conversion(mockRequest));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
    }

    // TODO: source - target currency tests


}