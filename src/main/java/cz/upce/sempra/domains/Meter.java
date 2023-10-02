package cz.upce.sempra.domains;

import cz.upce.sempra.dtos.MeterOutputDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "meters")
public class Meter {
    @Column(name = "meter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meter", orphanRemoval = true)
    private List<Sensor> sensors = new ArrayList<>();

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    public Meter(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public MeterOutputDto toDto() {
        return new MeterOutputDto(
                getName(),
                getSensors().stream().map(Sensor::toDto).toList()
        );
    }
}