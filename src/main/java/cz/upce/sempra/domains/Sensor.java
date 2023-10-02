package cz.upce.sempra.domains;

import cz.upce.sempra.dtos.SensorOutputDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "sensors")
public class Sensor {
    @Column(name = "sensor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @JoinColumn(name = "meter_id")
    @ManyToOne
    private Meter meter;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Meter> meters = new ArrayList<>();

    public Sensor(String name, String type, Meter meter) {
        this.name = name;
        this.type = type;
        this.meter = meter;
    }

    public SensorOutputDto toDto() {
        return new SensorOutputDto(
                getName(),
                getType()
        );
    }
}