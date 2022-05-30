package com.example.currencyexchange.controller;

import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import com.example.currencyexchange.exception.FormatException;
import com.example.currencyexchange.service.ConversionService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
class ConversionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
    void postConversionHappyPath() throws Exception {
        ConversionResponse mockResponse = new ConversionResponse();
        mockResponse.setTargetAmount(sourceAmount.multiply(exchangeRate));
        mockResponse.setTransactionId(2L);

        when(conversionService.conversion(mockRequest)).thenReturn(mockResponse);

        MvcResult result = mockMvc.perform(
                post("/conversion")
                        .content(new Gson().toJson(mockRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ConversionResponse conversionResponse = new Gson().fromJson(result.getResponse().getContentAsString(), ConversionResponse.class);
        assertThat(conversionResponse.getTransactionId()).isEqualTo(2L);
        assertThat(conversionResponse.getTargetAmount()).isEqualTo(sourceAmount.multiply(exchangeRate));
    }

    @Test
    void postConversionAmountFormat() throws Exception {
        mockRequest.setSourceAmount(new BigDecimal(0));

        when(conversionService.conversion(mockRequest)).thenThrow(FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "exception msg")).build());

        MvcResult result = mockMvc.perform(
                        post("/conversion")
                                .content(new Gson().toJson(mockRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = new Gson().fromJson(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(response.getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(response.getMessage()).isInstanceOfAny(String.class);
    }

    @Test
    void postConversionCurrencyFormat() throws Exception {
        mockRequest.setSourceCurrency("1Ac");

        when(conversionService.conversion(mockRequest)).thenThrow(FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "exception msg")).build());

        MvcResult result = mockMvc.perform(
                        post("/conversion")
                                .content(new Gson().toJson(mockRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = new Gson().fromJson(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(response.getCode()).isEqualTo(ErrorCodes.FORMAT_ERROR.getCode());
        assertThat(response.getMessage()).isInstanceOfAny(String.class);
    }

    @Test
    void postConversionCurrencyNotFound() throws Exception {
        mockRequest.setSourceCurrency("III");

        when(conversionService.conversion(mockRequest)).thenThrow(FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.CURRENCY_NOT_FOUND.getCode(), "exception msg")).build());

        MvcResult result = mockMvc.perform(
                        post("/conversion")
                                .content(new Gson().toJson(mockRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = new Gson().fromJson(result.getResponse().getContentAsString(), ErrorResponse.class);

        assertThat(response.getCode()).isEqualTo(ErrorCodes.CURRENCY_NOT_FOUND.getCode());
        assertThat(response.getMessage()).isInstanceOfAny(String.class);
    }




//    page ve size negatif testi, null testi
//  {
//    "code": 1,
//    "message": "Page size must not be less than one!"
//}



}