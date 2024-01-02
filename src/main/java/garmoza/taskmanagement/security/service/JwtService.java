package garmoza.taskmanagement.security.service;

import java.util.List;

public interface JwtService {

    String generateTokenWithAuthorities(String username, List<String> authorities);

    String extractUsername(String token);

    List<String> extractAuthorities(String token);

    boolean isTokenExpired(String token);
}
