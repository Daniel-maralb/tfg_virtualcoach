package com.example.virtualcoach.database.data.source;

import android.database.sqlite.SQLiteConstraintException;

import com.example.virtualcoach.database.data.dao.UserDao;
import com.example.virtualcoach.database.data.model.User;
import com.example.virtualcoach.usermanagement.di.UserManagementModule.PasswordSecurities.SHASecurity;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.example.virtualcoach.usermanagement.model.security.PasswordSecurity;
import com.example.virtualcoach.usermanagement.modules.register.RegisterFormValidator;
import com.google.common.util.concurrent.ListenableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@Singleton
public class UserDataSource {
    public static class EmailAlreadyExistsException extends Exception {
    }

    public static class UsernameDoesNotExistException extends Exception {
    }

    public static class PasswordDoesNotMatchException extends Exception {
    }

    private final UserDao userDao;
    private final PasswordSecurity passwordSecurity;
    private final RegisterFormValidator validator;

    @Inject
    UserDataSource(UserDao userDao
            , @SHASecurity PasswordSecurity passwordSecurity
            , RegisterFormValidator validator
    ) {
        this.userDao = userDao;
        this.passwordSecurity = passwordSecurity;
        this.validator = validator;
    }

    public DisplayUser register(String email, String password) throws EmailAlreadyExistsException {
        User user = new User(getUnusedUUID(), email, passwordSecurity.saltAndHash(password));

        try {
            userDao.insert(user);
        } catch (SQLiteConstraintException e) {
            throw new EmailAlreadyExistsException();
        }

        return new DisplayUser(user);
    }

    public DisplayUser login(String username, String password) throws UsernameDoesNotExistException, PasswordDoesNotMatchException {
        User user = userDao.getByEmail(username);

        if (isNull(user))
            throw new UsernameDoesNotExistException();

        if (!passwordSecurity.matches(password, user.password))
            throw new PasswordDoesNotMatchException();

        return new DisplayUser(user);
    }

    public User getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    public ListenableFuture<DisplayUser> getById(String id) {
        return userDao.getById(id);
    }


    private String getUnusedUUID() {
        //TODO ensure unused
        return java.util.UUID.randomUUID().toString();
    }

    public void update(DisplayUser currentUser) {
        userDao.updateUserData(currentUser.id,
                currentUser.email,
                currentUser.age,
                currentUser.weight,
                currentUser.height);
    }

}
