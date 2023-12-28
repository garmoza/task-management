package garmoza.taskmanagement.security.service;

import garmoza.taskmanagement.security.auth.UsernamePasswordAuthentication;
import garmoza.taskmanagement.security.controller.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthentication(request.getEmail(), request.getPassword())
        );

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return jwtService.generateToken(Map.of("authorities", authorities), authentication.getName());
    }
}
