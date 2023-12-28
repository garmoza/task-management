package garmoza.taskmanagement.security.service;

import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.repository.UserRepository;
import garmoza.taskmanagement.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // message that will be received in case of unsuccessful authentication
        Supplier<UsernameNotFoundException> s = () -> new UsernameNotFoundException("Problem during authentication!");

        User user = userRepository.findUserByEmail(username).orElseThrow(s);

        return new SecurityUser(user);
    }
}
