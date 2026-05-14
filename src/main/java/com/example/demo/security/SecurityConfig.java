package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                // Agent only
                .requestMatchers(HttpMethod.POST, "/api/customers").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/customers").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/customers/paged").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/agents/me").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tickets/agent").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tickets/agent/paged").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/status").hasAnyRole("AGENT", "ADMIN")
                // Customer only
                .requestMatchers(HttpMethod.GET, "/api/customers/me").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/customers/me").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tickets").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tickets/paged").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tickets/*").hasAnyRole("CUSTOMER", "ADMIN")
                // Everything else admin only
                .anyRequest().hasRole("ADMIN")
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}