package com.example.currencyexchange.controller;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionListRequest;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.exception.FormatException;
import com.example.currencyexchange.service.ConversionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
class ConversionControllerUnitTest {

    static String sourceCurrency;
    static String targetCurrency;
    static BigDecimal sourceAmount;
    static BigDecimal exchangeRate;
    static ConversionRequest mockRequest;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ConversionService conversionService;

    @BeforeAll
    public static void setUp() {
        sourceCurrency = "USD";
        targetCurrency = "EUR";
        sourceAmount = new BigDecimal("12.1");
        exchangeRate = new BigDecimal("2.1");

        mockRequest = new ConversionRequest();
        mockRequest.setSourceCurrency(sourceCurrency);
        mockRequest.setTargetCurrency(targetCurrency);
        mockRequest.setSourceAmount(sourceAmount);
    }

    @Test
    void testPostConversionHappyPath() throws Exception {
        ConversionResponse mockResponse = new ConversionResponse();
        mockResponse.setTargetAmount(sourceAmount.multiply(exchangeRate));
        mockResponse.setTransactionId(2L);

        when(conversionService.conversion(mockRequest)).thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(
                        post("/conversion")
                                .content(objectMapper.writeValueAsString(mockRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ConversionResponse conversionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ConversionResponse.class);
        assertThat(conversionResponse.getTransactionId()).isEqualTo(2L);
        assertThat(conversionResponse.getTargetAmount()).isEqualTo(sourceAmount.multiply(exchangeRate));
    }

    @Test
    void testPostConversionAmountFormat() throws Exception {
        mockRequest.setSourceAmount(new BigDecimal(0));

        when(conversionService.conversion(mockRequest)).thenThrow(FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "exception msg")).build());

        MvcResult result = mockMvc.perform(
                        post("/conversion")
                                .content(objectMapper.writeValueAsString(mockRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(response.getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(response.getMessage()).isInstanceOfAny(String.class);
    }

    @Test
    void testPostConversionCurrencyFormat() throws Exception {
        mockRequest.setSourceCurrency("1Ac");

        when(conversionService.conversion(mockRequest)).thenThrow(FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "exception msg")).build());

        MvcResult result = mockMvc.perform(
                        post("/conversion")
                                .content(objectMapper.writeValueAsString(mockRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(response.getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(response.getMessage()).isInstanceOfAny(String.class);
    }

    @Test
    void testPostConversionCurrencyNotFound() throws Exception {
        mockRequest.setSourceCurrency("III");

        when(conversionService.conversion(mockRequest)).thenThrow(FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.CURRENCY_NOT_FOUND.getCode(), "exception msg")).build());

        MvcResult result = mockMvc.perform(
                        post("/conversion")
                                .content(objectMapper.writeValueAsString(mockRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(response.getCode()).isEqualTo(ErrorCodes.CURRENCY_NOT_FOUND.getCode());
        assertThat(response.getMessage()).isInstanceOfAny(String.class);
    }

    /**************************  getConversions  **************************/
    @Test
    void testGetConversionsHappyPath() throws Exception {
        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(1L);
        conversions.add(foundConversion);

        when(conversionService.getConversions(any(ConversionListRequest.class), any(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        MvcResult result = mockMvc.perform(
                        get("/conversions")
                                .param("transactionId", "1")
                                .param("transactionDate", "2022-06-01")
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Conversion> conversionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertThat(conversionResponse.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testGetConversionsQueryWithoutId() throws Exception {
        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(5L);
        conversions.add(foundConversion);

        when(conversionService.getConversions(any(ConversionListRequest.class), any(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        MvcResult result = mockMvc.perform(
                        get("/conversions")
                                .param("transactionDate", "2022-06-01")
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Conversion> conversionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Conversion>>() {
        });
        assertThat(conversionResponse.get(0).getId()).isEqualTo(5L);
    }

    @Test
    void testGetConversionsQueryWithoutDate() throws Exception {
        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(1L);
        conversions.add(foundConversion);

        when(conversionService.getConversions(any(ConversionListRequest.class), any(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        MvcResult result = mockMvc.perform(
                        get("/conversions")
                                .param("transactionId", "1")
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Conversion> conversionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Conversion>>() {
        });
        assertThat(conversionResponse.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testGetConversionsNoQuery() throws Exception {
        when(conversionService.getConversions(nullable(ConversionListRequest.class), any(Pageable.class))).thenThrow(FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "Please provide transaction id or transaction date.")).build());

        MvcResult result = mockMvc.perform(
                        get("/conversions")
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(response.getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(response.getMessage()).isInstanceOfAny(String.class);
    }

    @Test
    void testGetConversionsNoPagination() throws Exception {
        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(1L);
        conversions.add(foundConversion);

        when(conversionService.getConversions(any(ConversionListRequest.class), nullable(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        MvcResult result = mockMvc.perform(
                        get("/conversions")
                                .param("transactionId", "1")
                                .param("transactionDate", "2022-06-01")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Conversion> conversionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Conversion>>() {
        });
        assertThat(conversionResponse.get(0).getId()).isEqualTo(1L);
    }


    @Test
    void testGetConversionsWrongPagination() throws Exception {
        List<Conversion> conversions = new ArrayList<>();
        Conversion foundConversion = new Conversion();
        foundConversion.setId(1L);
        conversions.add(foundConversion);

        when(conversionService.getConversions(any(ConversionListRequest.class), nullable(Pageable.class))).thenReturn(new PageImpl<>(conversions));

        MvcResult result = mockMvc.perform(
                        get("/conversions")
                                .param("transactionId", "1")
                                .param("transactionDate", "2022-06-01")
                                .param("page", "-1")
                                .param("size", "-20")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Conversion> conversionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Conversion>>() {
        });
        assertThat(conversionResponse.get(0).getId()).isEqualTo(1L);
    }



}