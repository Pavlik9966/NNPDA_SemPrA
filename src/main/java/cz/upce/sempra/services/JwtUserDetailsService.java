package cz.upce.sempra.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import cz.upce.sempra.repositories.IUserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        cz.upce.sempra.domains.User user = userRepository.getUserByUsername(username);

        if (user == null) throw new UsernameNotFoundException(username);

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("USER_ROLE"));
        return new User(user.getUsername(), user.getPassword(), authorityList);
    }
}