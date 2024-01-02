package garmoza.taskmanagement.security.service;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void emptyStringJwt() {
        String jwt = "";

        assertThrows(IllegalArgumentException.class, () -> jwtService.extractUsername(jwt));
    }

    @Test
    void notValidJwt() {
        String username = "test-username";
        List<String> authorities = List.of("ROLE_FIRST", "ROLE_SECOND");

        String jwt = jwtService.generateTokenWithAuthorities(username, authorities);

        String[] parts = jwt.split("\\.");

        // creates wrong parts
        String notValidPart1 = (parts[0].charAt(0) + 1) + parts[0].substring(1);
        String notValidPart2 = (parts[1].charAt(1) + 1) + parts[1].substring(1);
        String notValidPart3 = (parts[2].charAt(2) + 1) + parts[2].substring(1);

        String notValid1 = notValidPart1 + "." + parts[1] + "." + parts[2];
        String notValid2 = parts[0] + "." + notValidPart2 + "." + parts[2];
        String notValid3 = parts[0] + "." + parts[1] + "." + notValidPart3;
        String notValid4 = jwt + "abc";

        assertThrows(MalformedJwtException.class, () -> jwtService.extractUsername(notValid1));
        assertThrows(SignatureException.class, () -> jwtService.extractUsername(notValid2));
        assertThrows(SignatureException.class, () -> jwtService.extractUsername(notValid3));
        assertThrows(SignatureException.class, () -> jwtService.extractUsername(notValid4));
    }
}