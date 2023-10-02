package cz.upce.sempra.repositories;

import cz.upce.sempra.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username);
}