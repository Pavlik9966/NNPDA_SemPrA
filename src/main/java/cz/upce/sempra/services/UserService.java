package cz.upce.sempra.services;

import cz.upce.sempra.domains.User;
import cz.upce.sempra.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final IUserRepository userRepository;

    public User getUserByUsername(final String username) {
        return userRepository.getUserByUsername(username);
    }

    public User createUser(final User user) {
        return userRepository.save(user);
    }

    public User updateUser(final User user) {
        return userRepository.save(user);
    }
}