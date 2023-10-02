package cz.upce.sempra.repositories;

import cz.upce.sempra.domains.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISensorRepository extends JpaRepository<Sensor, Long> {
    Sensor getSensorByName(String name);

    List<Sensor> getSensorsByMeterName(String name);
}