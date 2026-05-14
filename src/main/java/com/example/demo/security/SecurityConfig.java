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
                .requestMatchers(HttpMethod.POST, "/api/customers").hasRole("AGENT")
                .requestMatchers(HttpMethod.GET, "/api/customers").hasRole("AGENT")
                .requestMatchers(HttpMethod.PUT, "/api/agents/me").hasRole("AGENT")
                .requestMatchers(HttpMethod.GET, "/api/tickets/agent").hasRole("AGENT")
                // Customer only
                .requestMatchers(HttpMethod.GET, "/api/customers/me").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/api/customers/me").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/tickets").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/tickets").hasRole("CUSTOMER")
                // Admin can do anything
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