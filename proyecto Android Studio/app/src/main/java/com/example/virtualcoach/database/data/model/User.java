package com.example.virtualcoach.database.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Data class that captures user information for logged in users retrieved from UserRepository
 */
@Entity(indices = {@Index(value = "email", unique = true)})
public class User {
    @PrimaryKey
    @NonNull
    public final String id;

    public String email;
    public String password;

    //Years
    public Integer age;
    //kg
    public Integer weight;
    //cm
    public Integer height;

    public User(@NonNull String id) {
        this.id = id;
    }

    @Ignore
    public User(@NonNull String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
}