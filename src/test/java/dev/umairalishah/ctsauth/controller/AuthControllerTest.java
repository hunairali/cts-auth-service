package dev.umairalishah.ctsauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.umairalishah.ctsauth.dto.AuthResponse;
import dev.umairalishah.ctsauth.dto.LoginRequest;
import dev.umairalishah.ctsauth.dto.RegisterRequest;
import dev.umairalishah.ctsauth.dto.UserResponse;
import dev.umairalishah.ctsauth.model.Role;
import dev.umairalishah.ctsauth.security.JwtAuthenticationFilter;
import dev.umairalishah.ctsauth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_returns201_onValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("Umair Ali Shah", "umair@example.com", "password123", Role.ADMIN);
        UserResponse userResponse = new UserResponse(1L, "Umair Ali Shah", "umair@example.com", Role.ADMIN, Instant.now());
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(AuthResponse.of("mock-jwt-token", userResponse));

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void register_returns400_onInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest("Umair Ali Shah", "not-an-email", "password123", Role.ADMIN);

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returns200_onValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("umair@example.com", "password123");
        UserResponse userResponse = new UserResponse(1L, "Umair Ali Shah", "umair@example.com", Role.ADMIN, Instant.now());
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(AuthResponse.of("mock-jwt-token", userResponse));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void me_returns200_forAuthenticatedUser() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "Umair Ali Shah", "umair@example.com", Role.ADMIN, Instant.now());
        when(authService.getCurrentUser("umair@example.com")).thenReturn(userResponse);

        var principal = new UsernamePasswordAuthenticationToken("umair@example.com", null, Collections.emptyList());

        mockMvc.perform(get("/api/auth/me").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("umair@example.com"));
    }
}
