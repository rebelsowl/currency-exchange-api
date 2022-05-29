package com.example.currencyexchange.service.Interface;

import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionListRequest;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IConversionService {

    ConversionResponse conversion(ConversionRequest conversionRequest);

    Page<Conversion> getConversions(ConversionListRequest conversionListRequest, Pageable pageable);
}
