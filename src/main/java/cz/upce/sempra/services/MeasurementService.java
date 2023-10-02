package cz.upce.sempra.services;

import cz.upce.sempra.domains.Meter;
import cz.upce.sempra.domains.Sensor;
import cz.upce.sempra.repositories.IMeterRepository;
import cz.upce.sempra.repositories.ISensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MeasurementService {
    private final IMeterRepository meterRepository;
    private final ISensorRepository sensorRepository;

    public Meter getMeterByName(final String name) throws ResourceNotFoundException {
        Meter result = meterRepository.getMeterByName(name);

        if (result == null) throw new ResourceNotFoundException();

        return result;
    }

    public Sensor getSensorByName(final String name) throws ResourceNotFoundException {
        Sensor result = sensorRepository.getSensorByName(name);

        if (result == null) throw new ResourceNotFoundException();

        return result;
    }

    public List<Meter> getUserMeters(final String username) throws ResourceNotFoundException {
        List<Meter> result = meterRepository.getMetersByUserUsername(username);

        if (result == null) throw new ResourceNotFoundException();

        return result;
    }

    public List<Sensor> getUserSensorsByMeterName(final String name) throws ResourceNotFoundException {
        List<Sensor> result = sensorRepository.getSensorsByMeterName(name);

        if (result == null) throw new ResourceNotFoundException();

        return result;
    }

    public Meter addUserMeter(final Meter meter) {
        return meterRepository.save(meter);
    }

    public Sensor addMeterSensor(final Sensor sensor) {
        return sensorRepository.save(sensor);
    }
}