package cz.upce.sempra.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SensorInputDto {
    @NotBlank
    @NotNull
    @Size(max = 256)
    private String name;

    @NotBlank
    @NotNull
    @Size(max = 256)
    private String type;

    @NotBlank
    @NotNull
    @Size(max = 256)
    private String meterName;
}