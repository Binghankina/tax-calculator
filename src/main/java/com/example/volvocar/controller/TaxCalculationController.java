package com.example.volvocar.controller;

import com.example.volvocar.CongestionTax;
import com.example.volvocar.service.CongestionTaxCalculatorService;
import com.example.volvocar.TaxCalculatorRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaxCalculationController {
    private CongestionTaxCalculatorService congestionTaxCalculator;
    @Autowired
    public TaxCalculationController(CongestionTaxCalculatorService congestionTaxCalculator) {
        this.congestionTaxCalculator = congestionTaxCalculator;
    }

    @PostMapping(value = "/tax-calculator",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CongestionTax> calculateCongestionTax(@Valid @RequestBody TaxCalculatorRequest taxCalculatorRequest) {
        CongestionTax result = congestionTaxCalculator.getTax(taxCalculatorRequest.getVehicle(),
                taxCalculatorRequest.getDates());
        return ResponseEntity.ok(result);
    }

}
