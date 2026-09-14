package dev.umairalishah.ctsauth.dto;

import dev.umairalishah.ctsauth.model.Role;
import dev.umairalishah.ctsauth.model.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}

