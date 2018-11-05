package io.github.thenilesh.httpirremote.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import io.github.thenilesh.httpirremote.dto.AppConfig;

@Dao
public interface ConfigDao {

    @Query("SELECT * from AppConfig")
    AppConfig load();
    @Insert
    void save(AppConfig appConfig);
}
