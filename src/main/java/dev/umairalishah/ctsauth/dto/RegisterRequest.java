package dev.umairalishah.ctsauth.dto;

import dev.umairalishah.ctsauth.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull @Size(min = 2, max = 120) String fullName,
        @NotNull @Email String email,
        @NotNull @Size(min = 8, max = 72) String password,
        @NotNull Role role
) {
}

