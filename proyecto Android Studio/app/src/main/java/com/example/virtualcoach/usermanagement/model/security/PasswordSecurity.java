package com.example.virtualcoach.usermanagement.model.security;

public interface PasswordSecurity {
    String saltAndHash(String password);

    boolean matches(String match, String password);
}
