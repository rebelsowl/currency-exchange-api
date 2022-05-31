package com.example.currencyexchange.service;

import com.example.currencyexchange.data.model.constant.ErrorCodes;
import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionListRequest;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.data.repository.ConversionRepository;
import com.example.currencyexchange.exception.FormatException;
import com.example.currencyexchange.service.Interface.IConversionService;
import com.example.currencyexchange.service.Interface.IExternalFXService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Transactional
@Service
public class ConversionService implements IConversionService {

    private final ConversionRepository conversionRepository;
    private final IExternalFXService fxService;

    public ConversionService(ConversionRepository conversionRepository, IExternalFXService fxService) {
        this.conversionRepository = conversionRepository;
        this.fxService = fxService;
    }

    @Override
    public ConversionResponse conversion(ConversionRequest conversionRequest) {
        checkAmount(conversionRequest);

        BigDecimal conversionRate = fxService.getExchangeRate(conversionRequest.getSourceCurrency(), conversionRequest.getTargetCurrency());
//        conversionRate.setScale(6, RoundingMode.HALF_EVEN);

        Conversion conversion = new Conversion();

        conversion.setSourceCurrency(conversionRequest.getSourceCurrency());
        conversion.setTargetCurrency(conversionRequest.getTargetCurrency());
        conversion.setSourceAmount(conversionRequest.getSourceAmount());
        conversion.setTargetAmount(conversionRate.multiply(conversionRequest.getSourceAmount()));

        Conversion savedConversion = conversionRepository.save(conversion);
        ConversionResponse response = new ConversionResponse();
        response.setTransactionId(savedConversion.getId());
        response.setTargetAmount(savedConversion.getTargetAmount());
        return response;
    }

    /**
     * @return
     */
    @Override
    public Page<Conversion> getConversions(ConversionListRequest request, Pageable pageable) {
        checkRequest(request);
        Instant startDate = null;
        Instant endDate = null;
        if (request.getTransactionDate() != null) {
             startDate = request.getTransactionDate().atStartOfDay().toInstant(ZoneOffset.UTC);
             endDate = startDate.plus(1, ChronoUnit.DAYS);
        }
        return conversionRepository.findByIdAndDate(request.getTransactionId(), startDate, endDate, pageable);
    }


    /************************************* PRIVATE METHODS *************************************/
    private void checkAmount(ConversionRequest request) {
        BigDecimal limit = new BigDecimal("0.1");
        if (request.getSourceAmount() == null || request.getSourceAmount().compareTo(limit) == -1) { // amount is less than limit
            throw FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "Amount should be at least  0.1")).build();
        }
    }

    private void checkRequest(ConversionListRequest request) {
        if (request.getTransactionId() == null && request.getTransactionDate() == null ) {
            throw FormatException.builder().errorResponse(new ErrorResponse(ErrorCodes.FORMAT_ERROR.getCode(), "Please provide transaction id or transaction date.")).build();
        }
    }
}
