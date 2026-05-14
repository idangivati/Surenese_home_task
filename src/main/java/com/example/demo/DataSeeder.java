package com.example.demo;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Admin user created!");
        }

        if (userRepository.findByUsername("agent1").isEmpty()) {
            User agent = new User();
            agent.setUsername("agent1");
            agent.setPassword(passwordEncoder.encode("agent123"));
            agent.setEmail("agent1@example.com");
            agent.setRole(Role.AGENT);
            userRepository.save(agent);
            System.out.println("✅ Agent user created!");
        }
    }
}