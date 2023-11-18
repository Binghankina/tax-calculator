package com.example.volvocar;

import com.example.volvocar.entity.VehicleEntity;
import com.example.volvocar.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class VehicleRepositoryTests {

    @Autowired
    VehicleRepository vehicleRepository;
    @Test
    void testFindByName() {
        VehicleEntity expected = new VehicleEntity((long) 7, "Car", false);
        Optional<VehicleEntity> actual = vehicleRepository.findByName("Car");
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }
}
