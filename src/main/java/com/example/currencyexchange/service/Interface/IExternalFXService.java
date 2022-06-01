package com.example.currencyexchange.service.Interface;

import java.math.BigDecimal;

public interface IExternalFXService {

    /**
     * gets exchange rate from external fx api
     *
     * @param sourceCurrency base currency
     * @param targetCurrency target currency
     * @return exchange rate of the input currencies
     */
    BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency);

}
