package garmoza.taskmanagement;

import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User user1 = User.builder()
                .email("admin@mail.com")
                .password(passwordEncoder.encode("admin"))
                .authorities(Set.of("ROLE_ADMIN"))
                .build();
        User user2 = User.builder()
                .email("user@mail.com")
                .password(passwordEncoder.encode("user"))
                .authorities(Set.of("ROLE_USER"))
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
    }
}
