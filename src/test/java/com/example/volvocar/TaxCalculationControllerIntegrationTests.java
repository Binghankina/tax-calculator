package com.example.volvocar;

import com.example.volvocar.entity.VehicleEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = VolvocarApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaxCalculationControllerIntegrationTests {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    public void givenTaxCalculatorPost_whenMockMVC_thenVerifyResponse() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setType("Car");
        Date date1 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(12, 0, 0).build().getTime();
        Date date2 = new Calendar.Builder().setDate(2013, 3,11)
                .setTimeOfDay(9, 31, 0).build().getTime();
        Date date3 = new Calendar.Builder().setDate(2013, 7,18)
                .setTimeOfDay(15, 56, 0).build().getTime();

        List<Date> dates = new ArrayList<>();
        dates.add(date1);
        dates.add(date2);
        dates.add(date3);
        TaxCalculatorRequest request = new TaxCalculatorRequest(vehicle, dates);
        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity("http://localhost:" + port + "/tax-calculator", request, String.class);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("{\"tax\":16}", responseEntity.getBody());
    }

    @Test
    public void getVehicle_whenMockMVC_thenVerifyResponse() {
            String name = this.restTemplate
                    .getForObject("http://localhost:" + port + "/vehicle/2", VehicleEntity.class).getName();
            assertEquals("Bus", name);
    }
}

