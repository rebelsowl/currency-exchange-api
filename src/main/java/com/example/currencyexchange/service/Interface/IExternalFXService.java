package com.example.currencyexchange.service.Interface;

import java.math.BigDecimal;

public interface IExternalFXService {

    public BigDecimal getExchangeRate(String sourceCurrency, String targetCurrency);

}
