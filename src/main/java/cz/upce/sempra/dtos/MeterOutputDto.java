package cz.upce.sempra.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MeterOutputDto {
    private String name;

    private List<SensorOutputDto> sensors;
}