package io.github.thenilesh.httpirremote.dto;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.github.thenilesh.httpirremote.dao.UriConverter;
import io.github.thenilesh.httpirremote.utils.StringToIrCodeConverter;

/**
 * Created by nilesh on 15/4/18.
 */

@Entity
public class RButton {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private String name;
    @TypeConverters(StringToIrCodeConverter.class)
    private IRCode irCode;
    @TypeConverters(UriConverter.class)
    private Uri thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public IRCode getIrCode() {
        return irCode;
    }

    public void setIrCode(IRCode irCode) {
        this.irCode = irCode;
    }

    public void setThumbnail(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }
}
