package garmoza.taskmanagement.repository;

import garmoza.taskmanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@mail.com")
                .password("pass")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();
    }

    @Test
    void findUserByEmail() {
        userRepository.save(user);

        Optional<User> optionalSavedUser = userRepository.findUserByEmail(user.getEmail());

        assertThat(optionalSavedUser).isPresent();
    }
}