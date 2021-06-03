package com.example.virtualcoach.database.data;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE User (" +
                    "id TEXT PRIMARY KEY NOT NULL," +
                    "email TEXT," +
                    "password TEXT" +
                    ")");
            database.execSQL("CREATE UNIQUE INDEX index_user_email ON user (email)");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE TrainingSession (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                    ")");
        }
    };
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSession",
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "start_time INTEGER",
                    "*,null"
            );
        }
    };
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSession",
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "start_time INTEGER," +
                            "repetition TEXT",
                    "*,null"
            );
        }
    };
    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE exercise (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "name TEXT" +
                    ")");
            database.execSQL("CREATE TABLE TrainingSessionExerciseXRef (" +
                    "trainingSessionId INTEGER NOT NULL," +
                    "exerciseId INTEGER NOT NULL," +
                    "PRIMARY KEY (trainingSessionId, exerciseId)" +
                    ")");
        }
    };
    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSessionExerciseXRef",
                    "trainingSessionId INTEGER NOT NULL," +
                            "exerciseId INTEGER NOT NULL," +
                            "position INTEGER NOT NULL," +
                            "PRIMARY KEY (trainingSessionId, exerciseId)," +
                            "FOREIGN KEY (trainingSessionId) REFERENCES TrainingSession (id)," +
                            "FOREIGN KEY (exerciseId) REFERENCES Exercise (id)",
                    "trainingSessionId,exerciseId,0");
        }
    };
    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "Exercise",
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "name TEXT," +
                            "difficulty TEXT," +
                            "duration INTEGER," +
                            "avgHR INTEGER," +
                            "steps INTEGER",
                    "*,'fácil',0,0,0");
        }
    };
    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSession",
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "userId INTEGER NOT NULL REFERENCES User(id)," +
                            "start_time INTEGER," +
                            "repetition TEXT",
                    "id,1,start_time,repetition");

            createIndex(database, "trainingSession", "userId");

            createIndex(database, "TrainingSessionExerciseXRef", "trainingSessionId");
            createIndex(database, "TrainingSessionExerciseXRef", "exerciseId");
        }
    };
    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSession",
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "userId TEXT NOT NULL REFERENCES User(id)," +
                            "start_time INTEGER," +
                            "repetition TEXT",
                    "id,1,start_time,repetition");

            createIndex(database, "trainingSession", "userId");
        }
    };
    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "User",
                    "id TEXT PRIMARY KEY NOT NULL," +
                            "email TEXT," +
                            "password TEXT," +
                            "age INTEGER," +
                            "weight INTEGER," +
                            "height INTEGER",
                    "*,NULL,NULL,NULL");
            database.execSQL("CREATE UNIQUE INDEX index_user_email ON user (email)");
        }
    };
    public static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSessionExerciseXRef",
                    "trainingSessionId INTEGER NOT NULL REFERENCES TrainingSession (id) ON DELETE CASCADE," +
                            "exerciseId INTEGER NOT NULL REFERENCES Exercise (id)," +
                            "position INTEGER NOT NULL," +
                            "PRIMARY KEY (trainingSessionId, exerciseId)",
                    "trainingSessionId,exerciseId,0");
            createIndex(database, "TrainingSessionExerciseXRef", "trainingSessionId");
            createIndex(database, "TrainingSessionExerciseXRef", "exerciseId");
        }
    };
    public static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSession",
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "userId TEXT NOT NULL REFERENCES User(id)," +
                            "start_time INTEGER," +
                            "modification_time INTEGER NOT NULL," +
                            "repetition TEXT",
                    "id,userId,start_time,1,repetition");

            createIndex(database, "trainingSession", "userId");

            database.execSQL("CREATE TABLE TrainingSessionLog (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "userId TEXT NOT NULL REFERENCES User(id)," +
                    "start_time INTEGER" +
                    ")");

            createIndex(database, "trainingSessionLog", "userId");
        }
    };

    public static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSessionLog",
                    "userId TEXT NOT NULL REFERENCES User(id)," +
                            "start_time INTEGER NOT NULL," +
                            "PRIMARY KEY (userId, start_time)",
                    "userId, start_time");

            createIndex(database, "trainingSessionLog", "userId");
        }
    };

    public static final Migration MIGRATION_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSessionLog",
                    "userId TEXT NOT NULL REFERENCES User(id)," +
                            "start_time INTEGER NOT NULL," +
                            "comment TEXT," +
                            "PRIMARY KEY (userId, start_time)",
                    "userId, start_time, 'example\ntext\nmultiline'");

            createIndex(database, "trainingSessionLog", "userId");
        }
    };

    public static final Migration MIGRATION_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            updateTable(database, "TrainingSessionLog",
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "userId TEXT NOT NULL REFERENCES User(id)," +
                            "start_time INTEGER NOT NULL," +
                            "comment TEXT"
                    , "0,*");
            createIndex(database, "trainingSessionLog", "userId");
            createIndex(database, "trainingSessionLog", true, "userId", "start_time");

            database.execSQL("CREATE TABLE TrainingSessionLogExerciseXRef (" +
                    "sessionLogId INTEGER NOT NULL REFERENCES TrainingSessionLog(id) ON DELETE CASCADE," +
                    "exerciseId INTEGER NOT NULL REFERENCES Exercise(id)," +
                    "position INTEGER NOT NULL," +
                    "observations TEXT," +
                    "PRIMARY KEY(sessionLogId, exerciseId)" +
                    ")");
            createIndex(database, "TrainingSessionLogExerciseXRef", "sessionLogId");
            createIndex(database, "TrainingSessionLogExerciseXRef", "exerciseId");
        }
    };

    public static void updateTable(@NonNull SupportSQLiteDatabase database, String table, String updatedTableSpec, String values) {
        String tempTable = "temp_" + table;
        String auxTable = "original_" + table;

        database.execSQL("CREATE TABLE " + tempTable + " (" + updatedTableSpec + ")");
        database.execSQL("INSERT INTO " + tempTable + " SELECT " + values + " FROM " + table);

        database.execSQL("ALTER TABLE " + table + " RENAME TO " + auxTable);
        database.execSQL("ALTER TABLE " + tempTable + " RENAME TO " + table);

        database.execSQL("DROP TABLE IF EXISTS " + auxTable);
    }

    public static void createIndex(@NonNull SupportSQLiteDatabase database, String tableName, boolean unique, String... fields) {
        database.execSQL("CREATE" + (unique ? " UNIQUE " : " ") + "INDEX IF NOT EXISTS " +
                "index_" + tableName + "_" + TextUtils.join("_", fields) +
                " ON " + tableName + "(" + TextUtils.join(",", fields) + ")");
    }

    public static void createIndex(@NonNull SupportSQLiteDatabase database, String tableName, String... fields) {
        database.execSQL("CREATE INDEX IF NOT EXISTS " +
                "index_" + tableName + "_" + TextUtils.join("_", fields) +
                " ON " + tableName + "(" + TextUtils.join(",", fields) + ")");
    }
}
