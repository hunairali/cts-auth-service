package dev.umairalishah.ctsauth.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String email) {
        super("An account with email '%s' already exists".formatted(email));
    }
}

