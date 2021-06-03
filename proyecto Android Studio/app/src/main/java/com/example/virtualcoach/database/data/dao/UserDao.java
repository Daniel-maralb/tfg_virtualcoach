package com.example.virtualcoach.database.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.virtualcoach.database.data.model.User;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM user WHERE email=:email")
    User getByEmail(String email);

    @Query("SELECT id,email,age,weight,height FROM user WHERE id=:id")
    ListenableFuture<DisplayUser> getById(String id);

    @Query("UPDATE User SET" +
            " email=:email," +
            " age=:age," +
            " weight=:weight," +
            " height=:height " +
            " WHERE id=:id")
    ListenableFuture<Void> updateUserData(String id, String email, Integer age, Integer weight, Integer height);
}