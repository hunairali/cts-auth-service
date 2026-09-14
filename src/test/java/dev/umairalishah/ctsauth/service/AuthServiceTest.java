package dev.umairalishah.ctsauth.service;

import dev.umairalishah.ctsauth.dto.AuthResponse;
import dev.umairalishah.ctsauth.dto.LoginRequest;
import dev.umairalishah.ctsauth.dto.RegisterRequest;
import dev.umairalishah.ctsauth.exception.EmailAlreadyInUseException;
import dev.umairalishah.ctsauth.exception.InvalidCredentialsException;
import dev.umairalishah.ctsauth.model.Role;
import dev.umairalishah.ctsauth.model.User;
import dev.umairalishah.ctsauth.repository.UserRepository;
import dev.umairalishah.ctsauth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("Umair Ali Shah", "umair@example.com", "hashed-password", Role.ADMIN);
    }

    @Test
    void register_savesNewUser_whenEmailNotTaken() {
        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password123", Role.CONTRIBUTOR);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("mock-jwt-token");
        assertThat(response.user().email()).isEqualTo("umair@example.com");
    }

    @Test
    void register_throws_whenEmailAlreadyInUse() {
        RegisterRequest request = new RegisterRequest("Dup User", "umair@example.com", "password123", Role.CONTRIBUTOR);
        when(userRepository.existsByEmail("umair@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyInUseException.class);
    }

    @Test
    void login_returnsToken_whenCredentialsValid() {
        LoginRequest request = new LoginRequest("umair@example.com", "password123");
        when(userRepository.findByEmail("umair@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("mock-jwt-token");
    }

    @Test
    void login_throws_whenPasswordWrong() {
        LoginRequest request = new LoginRequest("umair@example.com", "wrong-password");
        when(userRepository.findByEmail("umair@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_throws_whenEmailUnknown() {
        LoginRequest request = new LoginRequest("nobody@example.com", "password123");
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
