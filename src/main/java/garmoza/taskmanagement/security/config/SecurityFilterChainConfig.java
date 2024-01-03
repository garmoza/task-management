package garmoza.taskmanagement.security.config;

import garmoza.taskmanagement.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CorsConfigurationSource source = request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.applyPermitDefaultValues();
            config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:81"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
            config.setAllowCredentials(true);

            return config;
        };

        http.cors(corsCustomizer -> corsCustomizer.configurationSource(source));
        http.csrf(AbstractHttpConfigurer::disable);

        http.addFilterAt(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                .requestMatchers(HttpMethod.GET, "/users").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/users").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/users/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/tasks").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/tasks").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/tasks").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/tasks/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/tasks/**").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/tasks/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated());

        return http.build();
    }
}