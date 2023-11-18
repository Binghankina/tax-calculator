# tax-calculator

### Bug fixed

The CongestionTax Method does not consider following UC:
1. if the car passed several tolls in one hour after the start date, the car will be overcharged.
It violates the singleChargeRule.
2. It assumes the dates are all in one day. If the dates spread over several days. the total fee will be
the upper-limit for the whole year.
```
public CongestionTax getTax(Vehicle vehicle, Date[] dates) {
        Date intervalStart = dates[0];
        int totalFee = 0;

        for (Date date : dates) {
            int nextFee = getTollFee(date);
            int tempFee = getTollFee(intervalStart);

            long diffInMillies = date.getTime() - intervalStart.getTime();
            long minutes = diffInMillies / 1000 / 60;

            if (minutes <= 60) {
                if (totalFee > 0) totalFee -= tempFee;
                if (nextFee >= tempFee) tempFee = nextFee;
                totalFee += tempFee;
            } else {
                totalFee += nextFee;
            }
        }

        if (totalFee > 60) totalFee = 60;
        return new CongestionTax(totalFee);
    }
```

