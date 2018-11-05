package io.github.thenilesh.httpirremote.utils;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;

import io.github.thenilesh.httpirremote.dto.IRCode;

public class StringToIrCodeConverter {

    private static final Gson GSON = new Gson();

    @TypeConverter
    public static IRCode toIrCode(String irCodeStr) {
        return GSON.fromJson(irCodeStr, IRCode.class);
    }

    @TypeConverter
    public static String toString(IRCode irCode) {
        return GSON.toJson(irCode);
    }
}
