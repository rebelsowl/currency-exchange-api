package com.example.currencyexchange.controller;

import com.example.currencyexchange.data.model.entity.Conversion;
import com.example.currencyexchange.data.model.request.ConversionListRequest;
import com.example.currencyexchange.data.model.request.ConversionRequest;
import com.example.currencyexchange.data.model.response.ConversionResponse;
import com.example.currencyexchange.data.model.response.ErrorResponse;
import com.example.currencyexchange.service.ConversionService;
import com.example.currencyexchange.service.Interface.IConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/conversions")
    public ResponseEntity<List<Conversion>> getConversions(@RequestBody ConversionListRequest request, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(conversionService.getConversions(request, PageRequest.of(page, size)).getContent());
    }

}
