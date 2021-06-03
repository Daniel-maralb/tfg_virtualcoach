package com.example.virtualcoach.usermanagement.model.security;

import android.util.Base64;

import com.google.common.base.Strings;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import javax.inject.Inject;

public class SHA512PasswordSecurity implements PasswordSecurity {
    private static final char SEPARATOR = ':';
    private static final char FILLER = '0';

    private final Random random;
    private final int saltLength;
    private final int passwordLength;
    private final MessageDigest md;

    @Inject
    public SHA512PasswordSecurity() {
        this.random = new SecureRandom();
        this.saltLength = 32;
        this.passwordLength = 32;
        try {
            this.md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private byte[] hash(byte[] password, byte[] salt) {
        md.update(salt);
        md.update(password);

        return md.digest();
    }

    private byte[] fill(String password) {
        int toFill = passwordLength - password.length();

        if (toFill <= 0)
            return password.getBytes(StandardCharsets.UTF_8);

        return password.concat(Strings.repeat(String.valueOf(FILLER), toFill)).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String saltAndHash(String password) {
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        byte[] filledPassword = fill(password);
        byte[] hashedPassword = hash(filledPassword, salt);

        return Base64.encodeToString(salt, Base64.DEFAULT) +
                SEPARATOR +
                Base64.encodeToString(hashedPassword, Base64.DEFAULT);
    }

    @Override
    public boolean matches(String match, String saltAndPassword) {
        int pos_separator = saltAndPassword.indexOf(SEPARATOR);

        byte[] salt = Base64.decode(saltAndPassword.substring(0, pos_separator), Base64.DEFAULT);

        byte[] hashedMatch = hash(fill(match), salt);
        byte[] hashedPassword = Base64.decode(saltAndPassword.substring(pos_separator + 1), Base64.DEFAULT);

        return Arrays.equals(hashedMatch, hashedPassword);
    }

}
