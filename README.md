# tax-calculator
The project is an implementation of the [assignment](https://github.com/volvo-cars/congestion-tax-calculator/blob/main/ASSIGNMENT.md)

## Prerequists
- [tilt](https://docs.tilt.dev/install.html)
- A running k8s cluster

## Deploy tax-calculator with tilt
### Update tilt-settings.json
Update the following configuration with your k8s context and registry
```
{
"allow_k8s_contexts": "",
"default_registry": ""
}
```
### Login the docker registry
```
docker login -u <user> -p <password> <registry>
```
### Start Deploy 
run
```bash
cd tax-calculator
tilt up 
```
## Stop tax-calculator and clean up

```bash
cd tax-calculator
tilt down
```
## Testing
1. Check all the available vehicles
```
curl http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle
```
Example output
```
curl http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle
{
"_embedded" : {
"vehicle" : [ {
"name" : "Motorbike",
"is_toll_free" : true,
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/1"
},
"vehicleEntity" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/1"
}
}
}, {
"name" : "Bus",
"is_toll_free" : true,
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/2"
},
"vehicleEntity" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/2"
}
}
}, {
"name" : "Emergency",
"is_toll_free" : true,
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/3"
},
"vehicleEntity" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/3"
}
}
}, {
"name" : "Diplomat",
"is_toll_free" : true,
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/4"
},
"vehicleEntity" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/4"
}
}
}, {
"name" : "Foreign",
"is_toll_free" : true,
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/5"
},
"vehicleEntity" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/5"
}
}
}, {
"name" : "Military",
"is_toll_free" : true,
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/6"
},
"vehicleEntity" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/6"
}
}
}, {
"name" : "Car",
"is_toll_free" : false,
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/7"
},
"vehicleEntity" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/7"
}
}
} ]
},
"_links" : {
"self" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle"
},
"profile" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/profile/vehicle"
},
"search" : {
"href" : "http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/vehicle/search"
}
}
}
```
2. call tax calculator to get tax
```
curl -X POST -H 'Content-Type: application/json' -d '{"vehicle": {"type":"Car"}, "dates":["2013-01-01T09:00:00.594Z", "2013-08-30T09:00:00.594Z"]}' http://tax-calculator-service.tax-calculator.svc.cluster.local:8080/tax-calculator
```
Example output
```
{"tax":8}
```
## Assignment attacking strategy
### Total time spent
3 hours 50 minutes
### Task time allocation
1. Add a way to call the calculation with different inputs, preferably over HTTP.
- Solution: Implemented Rest API with spring boot
- Time: 1 hour
2. There may be bugs in the code, try to find and fix them.
- Solution: Implemented and debugged algorithm for tax calculation with related TCs
- Time: 2 hours
3. There are several improvements that can be made.
- Solution: Wrote Dockerfile, helm chart, tiltfile for k8s deployment and README
- Time: 50 minutes

## Additional work with more time
1. Implement Actuator endpoints
- Reason: production ready for k8s deployment
- Estimated time: 2 hours
2. Integration of Swagger
- Reason: Swagger is a good tool for creating API doc, frontend can easily read it for integration test.
- Estimated time: 1 hours
3. Test corner case for tax calculation
- Reason: covered all the user cases for tax calculation. However, I want to spend 1 hour to ensure the algorithm robustness.
- Estimated time: 1 hours
4. Improve VehicleRepository support CRUD operation completely.
- Reason: the VehicleEntity Class only support getByName with current version
- Estimated time: 2 hours
5. Implement bonus scenario
- Reason: bonus scenario is support other cities. However, for business of point view, the Goteborg scenario should be
implemented as production ready, then other city can use it as a reference
- Estimated time: Uncertain
6. Expand the scope of years
- Reason: the application only support 2013. For free of charge days, a common algorithm can be implemented.
- Estimated time: Uncertain

## Bug fixed

### Issues
1. The CongestionTax Method below does not consider following UCs:
- if the car passed several tolls in one hour after the start date, the car will be overcharged.
It violates the singleChargeRule. 
- It assumes the dates are all in one day. If the dates spread over several days. the total fee will be
the upper-limit for the whole year.
```
public int getTax(Vehicle vehicle, Date[] dates)
    {
        Date intervalStart = dates[0];
        int totalFee = 0;

        for (int i = 0; i < dates.length ; i++) {
            Date date = dates[i];
            int nextFee = GetTollFee(date, vehicle);
            int tempFee = GetTollFee(intervalStart, vehicle);

            long diffInMillies = date.getTime() - intervalStart.getTime();
            long minutes = diffInMillies/1000/60;

            if (minutes <= 60)
            {
                if (totalFee > 0) totalFee -= tempFee;
                if (nextFee >= tempFee) tempFee = nextFee;
                totalFee += tempFee;
            }
            else
            {
                totalFee += nextFee;
            }
        }                
      
        if (totalFee > 60) totalFee = 60;
        return totalFee;
    }
```
2. Tax Exempt vehicles should include bus and exclude tractor. 
```
static {
    tollFreeVehicles.put("Motorcycle", 0);
    tollFreeVehicles.put("Tractor", 1);
    tollFreeVehicles.put("Emergency", 2);
    tollFreeVehicles.put("Diplomat", 3);
    tollFreeVehicles.put("Foreign", 4);
    tollFreeVehicles.put("Military", 5);
}
```
### Solutions
1. Implemented new algorithm with sliding window
- Remove all the free dates from the dates arrayList
- Sort the dates with ascending order.
- Use a one-hour sliding window with deque, by using offerLast and pollFirst to move the window to apply the single 
charge rule. Then store the dates and toll fees in a hashmap.
```
  Time complexity: O(n)
  Space complexity: O(k), where k is the number of distinctive dates present in the deque.
```
- Apply the maximum amount per day rule by sum the toll fess in the same date.
2. Replace tractor with bus, and a car type.  And store the in memory data to DB with a new boolean field
is_toll_free.
```
INSERT INTO vehicle (id, name, is_toll_free) VALUES (1, 'Motorbike', true);
INSERT INTO vehicle (id, name, is_toll_free) VALUES (2, 'Bus', true);
INSERT INTO vehicle (id, name, is_toll_free) VALUES (3, 'Emergency', true);
INSERT INTO vehicle (id, name, is_toll_free) VALUES (4, 'Diplomat', true);
INSERT INTO vehicle (id, name, is_toll_free) VALUES (5, 'Foreign', true);
INSERT INTO vehicle (id, name, is_toll_free) VALUES (6, 'Military', true);
INSERT INTO vehicle (id, name, is_toll_free) VALUES (7, 'Car', false);
```
## Questions
1. A bit uncertain about the single charge rule. For example, for the following dates
- 2013-02-16 10:30:11
- 2013-02-16 10:38:11
- 2013-02-16 10:59:11
- 2013-02-16 11:38:11
- 2013-02-16 11:59:11

  With current algorithm. The car will be charge three times, since there are three one-hour time windows.

Window1:
- 2013-02-16 10:30:11
- 2013-02-16 10:38:11
- 2013-02-16 10:59:11

Window2:
- 2013-02-16 10:38:11
- 2013-02-16 10:59:11
- 2013-02-16 11:38:11

Window3:
- 2013-02-16 10:59:11
- 2013-02-16 11:38:11
- 2013-02-16 11:59:11

Is the above right understanding?