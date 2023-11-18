package com.example.volvocar;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Vehicle {
    @NotNull(message = "Vehicle type should not be null")
    private String type;
}

