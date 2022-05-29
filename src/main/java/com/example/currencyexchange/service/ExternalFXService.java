package com.example.currencyexchange.service;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.data.model.response.external.ConvertResponse;
import com.example.currencyexchange.exception.CurrencyException;
import com.example.currencyexchange.exception.ExternalServiceFaultException;
import com.example.currencyexchange.exception.FormatException;
import com.example.currencyexchange.service.Interface.IExternalFXService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class ExternalFXService implements IExternalFXService {

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalFXService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * gets exchange rate from external fx api
     *
     * @param sourceCurrency base currency
     * @param targetCurrency target currency
     * @return exchange rate
     */
    @Override
    public BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency) {
        checkCurrencyFormat(sourceCurrency);
        checkCurrencyFormat(targetCurrency);
        try {
            String url = String.format("https://api.exchangerate.host/convert?from=%s&to=%s", sourceCurrency, targetCurrency);
            ConvertResponse response = restTemplate.getForObject(url, ConvertResponse.class);

            if (response.isSuccess()) {
                if (response.getResult() == null) {

                    ErrorResponse errorResponse = new ErrorResponse(ErrorCodes.CURRENCY_NOT_FOUND.getCode(), "Currently we do not serve " + sourceCurrency + " - " + targetCurrency + " currency pair.");
                    throw CurrencyException.builder().errorResponse(errorResponse).build();
                }
                return response.getResult();
            } else
                throw ExternalServiceFaultException.builder().errorResponse(new ErrorResponse(ErrorCodes.EXTERNAL_SERVICE_ERROR.getCode(), "Our services are currently unavailable, please try again later.")).build();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            // TODO: not found alinca da
            throw e;
        }

    }

    private void checkCurrencyFormat(String currency) {
        if (currency == null || currency.length() != 3 || !StringUtils.isAllUpperCase(currency)) {
            throw FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "Currency should be 3 letters long and all uppercase ex: USD, EUR")).build();
        }
    }
}
