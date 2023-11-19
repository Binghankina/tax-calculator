package com.example.volvocar;

import com.example.volvocar.entity.VehicleEntity;
import com.example.volvocar.repository.VehicleRepository;
import com.example.volvocar.service.CongestionTaxCalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CongestionTaxCalculatorTests {

    @Mock
    VehicleRepository vehicleRepository;

    @InjectMocks
    private CongestionTaxCalculatorService congestionTaxCalculator;

    private static final int FEE = 54;

    @Test
    void testApplySingleChargeRule() {
        Date date1 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(15, 0, 0).build().getTime();
        Date date2 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(15, 31, 0).build().getTime();
        Date date3 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(15, 56, 0).build().getTime();
        Date date4 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(16, 16, 0).build().getTime();
        Date date5 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(17, 16, 0).build().getTime();
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(date1);
        dates.add(date2);
        dates.add(date3);
        dates.add(date4);
        dates.add(date5);
        Map<Calendar, Integer> actual = congestionTaxCalculator.applySingleChargeRule(dates);
        Map<Calendar, Integer> expected = new HashMap<>();
        expected.put(dateToCalendar(date3), 18);
        expected.put(dateToCalendar(date4), 18);
        assertEquals(expected, actual);
    }

    @Test
    void testRemoveFreeChargedDates() {
        Date date1 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(0, 0, 0).build().getTime();
        Date date2 = new Calendar.Builder().setDate(2013, 6,11)
                .setTimeOfDay(15, 31, 0).build().getTime();
        Date date3 = new Calendar.Builder().setDate(2013, 7,18)
                .setTimeOfDay(15, 56, 0).build().getTime();
        Date date4 = new Calendar.Builder().setDate(2013, 7,19)
                .setTimeOfDay(16, 16, 0).build().getTime();
        Date date5 = new Calendar.Builder().setDate(2013, 7,19)
                .setTimeOfDay(17, 16, 0).build().getTime();
        List<Date> dates = new ArrayList<>();
        dates.add(date1);
        dates.add(date2);
        dates.add(date3);
        dates.add(date4);
        dates.add(date5);

        ArrayList<Date> actual = congestionTaxCalculator.RemoveFreeChargedDates(dates);
        ArrayList<Date> expected = new ArrayList<>();
        expected.add(date4);
        expected.add(date5);
        assertEquals(expected, actual);
    }

    @Test
    void testGetTax() {
        Vehicle vehicle = new Vehicle();
        vehicle.setType("Car");
        VehicleEntity vehicleEntity = new VehicleEntity((long) 1, "Car", false);
        Mockito.when(vehicleRepository.findByName("Car")).thenReturn(Optional.of(vehicleEntity));

        Date date1 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(0, 0, 0).build().getTime();
        Date date2 = new Calendar.Builder().setDate(2013, 6,11)
                .setTimeOfDay(15, 31, 0).build().getTime();
        Date date3 = new Calendar.Builder().setDate(2013, 7,18)
                .setTimeOfDay(15, 56, 0).build().getTime();
        Date date4 = new Calendar.Builder().setDate(2013, 7,19)
                .setTimeOfDay(16, 16, 0).build().getTime();
        Date date5 = new Calendar.Builder().setDate(2013, 7,19)
                .setTimeOfDay(17, 16, 0).build().getTime();
        Date date6 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(15, 0, 0).build().getTime();
        Date date7 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(15, 31, 0).build().getTime();
        Date date8 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(15, 56, 0).build().getTime();
        Date date9 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(16, 16, 0).build().getTime();
        Date date10 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(17, 16, 0).build().getTime();

        List<Date> dates = new ArrayList<>();
        dates.add(date1);
        dates.add(date2);
        dates.add(date3);
        dates.add(date4);
        dates.add(date5);
        dates.add(date6);
        dates.add(date7);
        dates.add(date8);
        dates.add(date9);
        dates.add(date10);

        CongestionTax actual = congestionTaxCalculator.getTax(vehicle, dates);
        CongestionTax expected = new CongestionTax(FEE);
        assertEquals(expected, actual);
    }

    @Test
    void testGetTaxForExemptVehicles() {
        Vehicle vehicle = new Vehicle();
        vehicle.setType("Motorbike");
        VehicleEntity vehicleEntity = new VehicleEntity((long) 2, "Motorbike", true);
        Mockito.when(vehicleRepository.findByName("Motorbike")).thenReturn(Optional.of(vehicleEntity));

        Date date1 = new Calendar.Builder().setDate(2013, 2,11)
                .setTimeOfDay(0, 0, 0).build().getTime();
        Date date2 = new Calendar.Builder().setDate(2013, 6,11)
                .setTimeOfDay(15, 31, 0).build().getTime();
        Date date3 = new Calendar.Builder().setDate(2013, 7,18)
                .setTimeOfDay(15, 56, 0).build().getTime();

        List<Date> dates = new ArrayList<>();
        dates.add(date1);
        dates.add(date2);
        dates.add(date3);

        CongestionTax actual = congestionTaxCalculator.getTax(vehicle, dates);
        CongestionTax expected = new CongestionTax(0);
        assertEquals(expected, actual);
    }

    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
}
