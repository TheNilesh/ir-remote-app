package io.github.thenilesh.httpirremote.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import io.github.thenilesh.httpirremote.dto.RButton;

@Database(entities = {RButton.class}, version = AppDatabase.SCHEMA_VERSION, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    static final int SCHEMA_VERSION = 4;
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "irremotedb").fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

    public abstract RButtonDao rButtonDao();
    public abstract ConfigDao configDao();
}