package com.example.currencyexchange.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionListRequest;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import com.example.currencyexchange.data.repository.ConversionRepository;
import com.example.currencyexchange.exception.FormatException;
import com.example.currencyexchange.service.Interface.IExternalFXService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        sourceAmount = new BigDecimal("12.1");
        exchangeRate = new BigDecimal("2.1");

        mockRequest = new ConversionRequest();
        mockRequest.setSourceCurrency(sourceCurrency);
        mockRequest.setTargetCurrency(targetCurrency);
        mockRequest.setSourceAmount(sourceAmount);
    }

    /**************************  conversion  **************************/
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
        assertThat(response.getTargetAmount()).isEqualTo(exchangeRate.multiply(sourceAmount));
    }

    @Test
    public void testConversionNegativeAmount() {
        mockRequest.setSourceAmount(new BigDecimal(-999999999));

        FormatException exception = assertThrows(FormatException.class, () -> conversionService.conversion(mockRequest));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Amount should be at least  0.1");
    }

    @Test
    public void testConversionAmount0() {
        mockRequest.setSourceAmount(new BigDecimal(0));

        FormatException exception = assertThrows(FormatException.class, () -> conversionService.conversion(mockRequest));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Amount should be at least  0.1");
    }

    @Test
    public void testConversionAmount001() {
        mockRequest.setSourceAmount(new BigDecimal("0.01"));

        FormatException exception = assertThrows(FormatException.class, () -> conversionService.conversion(mockRequest));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Amount should be at least  0.1");
    }

    @Test
    public void testConversionWrongCurrencyFormat() {
        Conversion mockSavedConversion = new Conversion();
        mockSavedConversion.setId(1L);
        mockSavedConversion.setSourceCurrency("a1Z");
        mockSavedConversion.setTargetCurrency("AAAA");
        mockSavedConversion.setSourceAmount(sourceAmount);
        mockSavedConversion.setTargetAmount(sourceAmount.multiply(exchangeRate));

        when(fxService.getExchangeRate(any(String.class), any(String.class))).thenThrow(FormatException.class);

        assertThrows(FormatException.class, () -> conversionService.conversion(mockRequest));
    }

    /**************************  getConversions  **************************/

    @Test
    public void testGetConversionsHappyPath() {
        ConversionListRequest request = new ConversionListRequest();
        request.setTransactionId(1L);
        request.setTransactionDate(LocalDate.now());

        Pageable page = PageRequest.of(0,10);

        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(1L);

        conversions.add(foundConversion);

        when(conversionRepository.findByIdAndDate(any(Long.class), any(Instant.class), any(Instant.class), any(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        Page<Conversion> response = conversionService.getConversions(request, page);

        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    public void testGetConversionsNoTransaction() {
        ConversionListRequest mockRequest = new ConversionListRequest();
        mockRequest.setTransactionDate(LocalDate.now());

        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(5L);

        conversions.add(foundConversion);

        when(conversionRepository.findByIdAndDate(nullable(Long.class), any(Instant.class), any(Instant.class), any(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        Page<Conversion> response = conversionService.getConversions(mockRequest, PageRequest.of(0,10));

        assertThat(response.getContent().get(0).getId()).isEqualTo(5L);
    }

    @Test
    public void testGetConversionsNoDate() {
        ConversionListRequest mockRequest = new ConversionListRequest();
        mockRequest.setTransactionId(2L);

        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(2L);

        conversions.add(foundConversion);

        when(conversionRepository.findByIdAndDate(any(Long.class), nullable(Instant.class), nullable(Instant.class), any(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        Page<Conversion> response = conversionService.getConversions(mockRequest, PageRequest.of(0,10));

        assertThat(response.getContent().get(0).getId()).isEqualTo(2L);
    }

    @Test
    public void testGetConversionsNoConversionList() {
        ConversionListRequest mockRequest = new ConversionListRequest();

        FormatException exception = assertThrows(FormatException.class, () -> conversionService.getConversions(mockRequest, PageRequest.of(0,10)));

        assertThat(exception.getErrorResponse().getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Please provide transaction id or transaction date.");

    }

    @Test
    public void testGetConversionsNoPageable() {
        ConversionListRequest mockRequest = new ConversionListRequest();
        mockRequest.setTransactionId(2L);
        mockRequest.setTransactionDate(LocalDate.now());

        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(2L);

        conversions.add(foundConversion);

        when(conversionRepository.findByIdAndDate(any(Long.class), any(Instant.class), any(Instant.class), nullable(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        Page<Conversion> response = conversionService.getConversions(mockRequest, null);

        assertThat(response.getContent().get(0).getId()).isEqualTo(2L);
    }

    @Test
    public void testGetConversionsWrongPagination() { // Should return default pagination result
        ConversionListRequest mockRequest = new ConversionListRequest();
        mockRequest.setTransactionId(2L);
        mockRequest.setTransactionDate(LocalDate.now());

        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(2L);

        conversions.add(foundConversion);

        when(conversionRepository.findByIdAndDate(any(Long.class), any(Instant.class), any(Instant.class), nullable(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        Page<Conversion> response = conversionService.getConversions(mockRequest, null);

        assertThat(response.getContent().get(0).getId()).isEqualTo(2L);
    }


}