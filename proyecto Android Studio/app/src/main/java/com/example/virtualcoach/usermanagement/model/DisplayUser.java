package com.example.virtualcoach.usermanagement.model;

import androidx.room.Ignore;

import com.example.virtualcoach.database.data.model.User;

/**
 * Data class that captures user information for logged in users retrieved from UserRepository
 */
public class DisplayUser {

    public final String id;
    public String email;
    //Years
    public Integer age;
    //kg
    public Integer weight;
    //cm
    public Integer height;

    @Ignore
    public String displayName;

    @Ignore
    public DisplayUser(User user) {
        this(user.id, user.email);
    }

    public DisplayUser(String id, String email) {
        this.id = id;
        this.email = email;
        this.displayName = email.substring(0, email.indexOf('@'));
    }
}