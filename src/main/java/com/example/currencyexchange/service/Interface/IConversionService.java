package com.example.currencyexchange.service.Interface;

import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionListRequest;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IConversionService {

    /**
     *  Converts source currency to target currency with given amount, gets the exchange rate from external api, returns the amount and the transaction id.
     */
    ConversionResponse conversion(ConversionRequest conversionRequest);

    /**
     *  Gets the conversion list with pagination
     * @param conversionListRequest at least one of the inputs should be provided
     * @param pageable  {@link Pageable}
     *
     * @return paged list of queried conversions
     */
    Page<Conversion> getConversions(ConversionListRequest conversionListRequest, Pageable pageable);
}
