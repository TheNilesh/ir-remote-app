package io.github.thenilesh.httpirremote.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.github.thenilesh.httpirremote.dto.RButton;
@Dao
public interface RButtonDao {

    @Insert
    void insert(RButton rButton);

    @Query("SELECT * FROM RButton")
    List<RButton> findAll();

    @Delete
    void delete(RButton rButton);
}
