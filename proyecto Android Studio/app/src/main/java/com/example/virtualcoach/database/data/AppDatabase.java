package com.example.virtualcoach.database.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.virtualcoach.database.data.dao.ActivityDataDao;
import com.example.virtualcoach.database.data.dao.ExerciseDao;
import com.example.virtualcoach.database.data.dao.TrainingSessionDao;
import com.example.virtualcoach.database.data.dao.TrainingSessionLogDao;
import com.example.virtualcoach.database.data.dao.UserDao;
import com.example.virtualcoach.database.data.model.ActivityData;
import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.database.data.model.TrainingSessionLog;
import com.example.virtualcoach.database.data.model.User;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionExerciseXRef;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionLogExerciseXRef;


@Database(entities = {
        ActivityData.class
        , User.class
        , TrainingSession.class
        , Exercise.class
        , TrainingSessionExerciseXRef.class
        , TrainingSessionLog.class
        , TrainingSessionLogExerciseXRef.class
}, version = 16)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "virtualcoach_database";

    public static AppDatabase create(Context appContext) {
        return Room.databaseBuilder(
                appContext,
                AppDatabase.class,
                DB_NAME)
                .createFromAsset("database/initial.db")
                .addMigrations(Migrations.MIGRATION_1_2
                        , Migrations.MIGRATION_2_3
                        , Migrations.MIGRATION_3_4
                        , Migrations.MIGRATION_4_5
                        , Migrations.MIGRATION_5_6
                        , Migrations.MIGRATION_6_7
                        , Migrations.MIGRATION_7_8
                        , Migrations.MIGRATION_8_9
                        , Migrations.MIGRATION_9_10
                        , Migrations.MIGRATION_10_11
                        , Migrations.MIGRATION_11_12
                        , Migrations.MIGRATION_12_13
                        , Migrations.MIGRATION_13_14
                        , Migrations.MIGRATION_14_15
                        , Migrations.MIGRATION_15_16
                )
                .build();
    }

    public abstract ActivityDataDao activityDataDao();

    public abstract UserDao userDao();

    public abstract TrainingSessionDao trainingSessionDao();

    public abstract TrainingSessionLogDao trainingSessionLogDao();

    public abstract ExerciseDao exerciseDao();
}
