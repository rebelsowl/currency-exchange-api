package com.example.currencyexchange.controller;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.exception.CurrencyException;
import com.example.currencyexchange.service.Interface.IExternalFXService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IExternalFXService fxService;

    @Test
    void testGetExchangeRateHappyPath() throws Exception {
        BigDecimal mockRate = new BigDecimal(12);

        when(fxService.getExchangeRate(anyString(), anyString())).thenReturn(mockRate);

        MvcResult result = mockMvc.perform(
                        get("/exchange-rate").param("from", "USD").param("to", "EUR"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("12");
    }

    @Test
    void testGetExchangeRateWrongFormat() throws Exception {
        when(fxService.getExchangeRate(anyString(), anyString())).thenThrow(CurrencyException.builder().errorResponse(new ErrorResponse(ErrorCodes.CURRENCY_FORMAT_ERROR.getCode(), "exception msg")).build());

        mockMvc.perform(
                get("/exchange-rate").param("from", "123").param("to", "EUR"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

}