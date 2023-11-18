package com.example.volvocar;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaxCalculatorRequest {
    @NotNull(message = "Vehicle should not be null")
    private Vehicle vehicle;
    @NotNull(message = "Dates should not be null")
    private List<Date> dates;
}
