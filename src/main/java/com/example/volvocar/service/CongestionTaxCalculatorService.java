package com.example.volvocar.service;

import com.example.volvocar.CongestionTax;
import com.example.volvocar.Vehicle;
import com.example.volvocar.entity.VehicleEntity;
import com.example.volvocar.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CongestionTaxCalculatorService {
    @Autowired(required = false)
    private final VehicleRepository vehicleRepository;

    @Autowired
    public CongestionTaxCalculatorService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    private static final int interval = 3600000;

    public ArrayList<Date> RemoveFreeChargedDates(List<Date> dates) {
        ArrayList<Date> chargedDates = new ArrayList<>();
        for(Date date: dates) {
            if (getTollFee(dateToCalendar(date)) != 0) {
                chargedDates.add(date);
            }
        }
        return chargedDates;
    }
    public Map<Calendar, Integer> applySingleChargeRule(ArrayList<Date> dates) {
        Deque<Date> oneHourSlidingWindow = new ArrayDeque<>();
        Map<Calendar, Integer> fees = new HashMap<>();
        for(Date date: dates) {
            if (oneHourSlidingWindow.size() != 0) {
                if (date.getTime() - oneHourSlidingWindow.peekFirst().getTime() > interval) {
                    findHighestFee(oneHourSlidingWindow, fees);
                    while (!oneHourSlidingWindow.isEmpty() &&
                            date.getTime() - oneHourSlidingWindow.peekFirst().getTime() > interval) {
                        oneHourSlidingWindow.pollFirst();
                    }
                }
            }
            oneHourSlidingWindow.offerLast(date);
        }
        if(!oneHourSlidingWindow.isEmpty()) {
            findHighestFee(oneHourSlidingWindow, fees);
        }
        return fees;
    }
    private void findHighestFee(Deque<Date> oneHourSlidingWindow, Map<Calendar, Integer> fees) {
        Map<Integer, Calendar> feesInOneHour = new HashMap<>();
        for (Date date : oneHourSlidingWindow) {
           feesInOneHour.put(getTollFee(dateToCalendar(date)), dateToCalendar(date));
        }
        Optional<Integer> max = feesInOneHour.keySet().stream().max(Integer::compare);
        max.ifPresent(integer -> fees.put(feesInOneHour.get(integer), integer));
    }
    public CongestionTax getTax(Vehicle vehicle, List<Date> dates)
    {
        ArrayList<Date> chargedDates = RemoveFreeChargedDates(dates);
        Map<Calendar, Integer> datesAndFees = applySingleChargeRule(new ArrayList<>(chargedDates.stream()
                .sorted(Date::compareTo)
                .collect(Collectors.toList())));
        Calendar initialDate = new Calendar.Builder().setDate(0, 0,0).build();;
        int totalFee = 0;
        int totalFeeOfSameDay = 0;
        if (isTollFreeVehicle(vehicle)) {
            return new CongestionTax(totalFee);
        }
        for(Calendar date: datesAndFees.keySet()) {
            if (date.get(Calendar.MONTH) == initialDate.get(Calendar.MONTH) &&
                    date.get(Calendar.DAY_OF_MONTH) == initialDate.get(Calendar.DAY_OF_MONTH)) {
                totalFeeOfSameDay += datesAndFees.get(date);
                if (totalFeeOfSameDay >= 60) {
                    totalFeeOfSameDay = 60;
                }
            } else {
                initialDate = date;
                totalFee += datesAndFees.get(date);
                totalFee += totalFeeOfSameDay;
                totalFeeOfSameDay = 0;
            }
        }
        return new CongestionTax(totalFee);
    }

    protected boolean isTollFreeVehicle(Vehicle vehicle) {
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findByName(vehicle.getType());
        if (vehicleEntity.isPresent()) {
            return vehicleEntity.get().getIs_toll_free();
        } else {
            return false;
        }

    }

    private int getTollFee(Calendar date)
    {
        if (isTollFreeDate(date)) return 0;

        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        if (hour == 6 && minute <= 29) return 8;
        else if (hour == 6) return 13;
        else if (hour == 7) return 18;
        else if (hour == 8 && minute <= 29) return 13;
        else if (hour >= 8 && hour <= 14) return 8;
        else if (hour == 15 && minute <= 29) return 13;
        else if (hour >= 15 && hour <= 16) return 18;
        else if (hour == 17) return 13;
        else if (hour == 18 && minute <= 29) return 8;
        else return 0;
    }

    private Boolean isTollFreeDate(Calendar date)
    {
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);

        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) return true;

        if (year == 2013)
        {
            return (month == 1 && dayOfMonth == 1) ||
                    (month == 3 && (dayOfMonth == 28 || dayOfMonth == 29)) ||
                    (month == 4 && (dayOfMonth == 1 || dayOfMonth == 30)) ||
                    (month == 5 && (dayOfMonth == 1 || dayOfMonth == 8 || dayOfMonth == 9)) ||
                    (month == 6 && (dayOfMonth == 5 || dayOfMonth == 6 || dayOfMonth == 21)) ||
                    (month == 7) ||
                    (month == 11 && dayOfMonth == 1) ||
                    (month == 12 && (dayOfMonth == 24 || dayOfMonth == 25 || dayOfMonth == 26 || dayOfMonth == 31));
        }
        return false;
    }

    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
}
