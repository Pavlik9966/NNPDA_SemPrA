package cz.upce.sempra.repositories;

import cz.upce.sempra.domains.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMeterRepository extends JpaRepository<Meter, Long> {
    Meter getMeterByName(String name);

    List<Meter> getMetersByUserUsername(String username);
}