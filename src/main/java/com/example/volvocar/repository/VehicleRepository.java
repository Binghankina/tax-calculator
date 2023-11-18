package com.example.volvocar.repository;

import com.example.volvocar.entity.VehicleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "vehicle", path = "vehicle")
public interface VehicleRepository extends CrudRepository<VehicleEntity,Long> {
    Optional<VehicleEntity> findByName(@Param("name") String name);
}
