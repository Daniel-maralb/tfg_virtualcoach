package com.example.virtualcoach.usermanagement.di;

import androidx.annotation.Nullable;

import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.example.virtualcoach.usermanagement.model.security.PasswordSecurity;
import com.example.virtualcoach.usermanagement.model.security.SHA512PasswordSecurity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

public class UserManagementModule {
    @Module
    @InstallIn(SingletonComponent.class)
    public abstract static class PasswordSecurities {
        @Qualifier
        @Retention(RetentionPolicy.RUNTIME)
        public @interface SHASecurity {
        }

        @Binds
        @SHASecurity
        abstract PasswordSecurity bindSHA512Security(SHA512PasswordSecurity impl);
    }

    @Module
    @InstallIn(SingletonComponent.class)
    public static class UserManagement {
        @Qualifier
        @Retention(RetentionPolicy.RUNTIME)
        public @interface CurrentUser {
        }

        @Provides
        @Nullable
        @CurrentUser
        public DisplayUser provideCurrentUser(UserRepository userRepository) {
            return userRepository.getCurrentUser();
        }
    }
}
