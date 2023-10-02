package cz.upce.sempra.controllers;

import cz.upce.sempra.domains.Meter;
import cz.upce.sempra.domains.Sensor;
import cz.upce.sempra.domains.User;
import cz.upce.sempra.dtos.MeterInputDto;
import cz.upce.sempra.dtos.SensorInputDto;
import cz.upce.sempra.services.MeasurementService;
import cz.upce.sempra.services.ResourceNotFoundException;
import cz.upce.sempra.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Measurement")
@RequestMapping("/measurement")
@RestController
public class MeasurementController {
    protected final Log logger = LogFactory.getLog(getClass());
    @Autowired
    MeasurementService measurementService;
    @Autowired
    UserService userService;

    @ApiOperation(value = "Find meter.")
    @GetMapping("/find/meter")
    public ResponseEntity<?> getMeterByName(@RequestParam final String name) throws ResourceNotFoundException {
        var result = measurementService.getMeterByName(name);

        return ResponseEntity.ok(result.toDto());
    }

    @ApiOperation(value = "Find sensor.")
    @GetMapping("/find/sensor")
    public ResponseEntity<?> getSensorByName(@RequestParam final String name) throws ResourceNotFoundException {
        var result = measurementService.getSensorByName(name);

        return ResponseEntity.ok(result.toDto());
    }

    @ApiOperation(value = "Get user meters.")
    @GetMapping("/user/meters")
    public ResponseEntity<?> getUserMetersByUsername() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());

        if (user == null) throw new ResourceNotFoundException();

        var result = measurementService.getUserMeters(user.getUsername());

        return ResponseEntity.ok(result.stream().map(Meter::toDto).toList());
    }

    @ApiOperation(value = "Get meter sensors.")
    @GetMapping("/user/sensors")
    public ResponseEntity<?> getUserSensorsByMeterName(@RequestParam final String name) throws ResourceNotFoundException {
        var result = measurementService.getUserSensorsByMeterName(name);

        return ResponseEntity.ok(result.stream().map(Sensor::toDto).toList());
    }

    @ApiOperation(value = "Add user meter.")
    @PostMapping("/add/meter")
    public ResponseEntity<?> addMeter(@Validated @RequestBody final MeterInputDto meterInputDto) throws ResourceNotFoundException {
        var result = measurementService.addUserMeter(toEntity(meterInputDto));

        return ResponseEntity.ok(result.toDto());
    }

    @ApiOperation(value = "Add meter sensor.")
    @PostMapping("/add/sensor")
    public ResponseEntity<?> addMeterSensor(@Validated @RequestBody final SensorInputDto sensorInputDto) throws ResourceNotFoundException {
        var result = measurementService.addMeterSensor(toEntity(sensorInputDto));

        return ResponseEntity.ok(result.toDto());
    }

    private Meter toEntity(final MeterInputDto meterInputDto) throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());

        if (user == null) throw new ResourceNotFoundException();

        return new Meter(
                meterInputDto.getName(),
                user
        );
    }

    private Sensor toEntity(final SensorInputDto sensorInputDto) throws ResourceNotFoundException {
        return new Sensor(
                sensorInputDto.getName(),
                sensorInputDto.getType(),
                measurementService.getMeterByName(sensorInputDto.getMeterName())
        );
    }
}