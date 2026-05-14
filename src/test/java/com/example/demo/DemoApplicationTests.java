package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private User agent;
    private User customer;

    @BeforeEach
    void setUp() {
        agent = new User();
        agent.setId(1L);
        agent.setUsername("agent1");
        agent.setPassword("encoded_password");
        agent.setEmail("agent1@example.com");
        agent.setRole(Role.AGENT);

        customer = new User();
        customer.setId(2L);
        customer.setUsername("customer1");
        customer.setPassword("encoded_password");
        customer.setEmail("customer1@example.com");
        customer.setRole(Role.CUSTOMER);
        customer.setAgent(agent);
    }

    // ✅ Test 1: Agent can create a customer
    @Test
    void agentCanCreateCustomer() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newcustomer");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        when(userRepository.existsByUsername("newcustomer")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(customer);

        var response = userService.createCustomer(request, agent);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ✅ Test 2: Cannot create customer with duplicate username
    @Test
    void cannotCreateCustomerWithDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("customer1");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        when(userRepository.existsByUsername("customer1")).thenReturn(true);

        assertThrows(ApiException.class, () -> 
            userService.createCustomer(request, agent));
    }

    // ✅ Test 3: Agent can get their customers
    @Test
    void agentCanGetTheirCustomers() {
        when(userRepository.findByAgentId(1L)).thenReturn(List.of(customer));

        var customers = userService.getMyCustomers(agent);

        assertEquals(1, customers.size());
        assertEquals("customer1", customers.get(0).getUsername());
    }

    // ✅ Test 4: Login with wrong password fails
    @Test
    void loginWithWrongPasswordFails() {
        LoginRequest request = new LoginRequest();
        request.setUsername("agent1");
        request.setPassword("wrongpassword");

        when(userRepository.findByUsername("agent1")).thenReturn(Optional.of(agent));
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        assertThrows(ApiException.class, () -> authService.login(request));
    }

    // ✅ Test 5: Login with correct credentials returns token (SECURITY TEST)
    @Test
    void loginWithCorrectCredentialsReturnsToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("agent1");
        request.setPassword("agent123");

        when(userRepository.findByUsername("agent1")).thenReturn(Optional.of(agent));
        when(passwordEncoder.matches("agent123", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken(agent)).thenReturn("mock_jwt_token");

        String token = authService.login(request);

        assertNotNull(token);
        assertEquals("mock_jwt_token", token);
        verify(jwtService, times(1)).generateToken(agent);
    }
}