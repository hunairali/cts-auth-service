package dev.umairalishah.ctsauth.service;

import dev.umairalishah.ctsauth.dto.AuthResponse;
import dev.umairalishah.ctsauth.dto.LoginRequest;
import dev.umairalishah.ctsauth.dto.RegisterRequest;
import dev.umairalishah.ctsauth.dto.UserResponse;
import dev.umairalishah.ctsauth.exception.EmailAlreadyInUseException;
import dev.umairalishah.ctsauth.exception.InvalidCredentialsException;
import dev.umairalishah.ctsauth.model.User;
import dev.umairalishah.ctsauth.repository.UserRepository;
import dev.umairalishah.ctsauth.security.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application-layer logic for registration, login, and "who am I" lookups. Kept independent of
 * the web layer so it can be unit tested with plain Mockito mocks (see AuthServiceTest).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }

        User user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role()
        );
        User saved = userRepository.save(user);

        String token = jwtService.generateToken(toUserDetails(saved));
        return AuthResponse.of(token, UserResponse.from(saved));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(toUserDetails(user));
        return AuthResponse.of(token, UserResponse.from(user));
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        return UserResponse.from(user);
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name())))
                .build();
    }
}
