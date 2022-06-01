package com.example.currencyexchange.controller;

import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionListRequest;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.service.ConversionService;
import com.example.currencyexchange.service.Interface.IConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "conversion api")
public class ConversionController {

    private final IConversionService conversionService;

    @Autowired
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Operation(summary = "converts source currency to target currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "conversion successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConversionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping("/conversion")
    public ResponseEntity<ConversionResponse> postConversion(@RequestBody ConversionRequest conversionRequest) {
        return ResponseEntity.ok(conversionService.conversion(conversionRequest));
    }


    @Operation(summary = "retrieves the conversion list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Conversion.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping("/conversions")
    public ResponseEntity<List<Conversion>> getConversions(@Parameter(description = "Query request, at least one of the inputs shall be provided.") ConversionListRequest request, @Parameter(description = "Pagination request. \n ", schema = @Schema(implementation = Pageable.class)) Pageable pageable) {
        return ResponseEntity.ok(conversionService.getConversions(request, pageable).getContent());
    }

}
