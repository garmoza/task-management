package garmoza.taskmanagement.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtServiceImpl jwtService;

    @Test
    void generateTokenWithAuthorities() {
        String username = "test-username";
        List<String> authorities = List.of("ROLE_FIRST", "ROLE_SECOND");

        String jwt = jwtService.generateTokenWithAuthorities(username, authorities);

        String usernameJwt = jwtService.extractUsername(jwt);
        List<String> authoritiesJwt = jwtService.extractAuthorities(jwt);
        boolean isExpired = jwtService.isTokenExpired(jwt);

        assertThat(usernameJwt).isEqualTo(username);
        assertThat(authoritiesJwt).isEqualTo(authorities);
        assertFalse(isExpired);
    }
}