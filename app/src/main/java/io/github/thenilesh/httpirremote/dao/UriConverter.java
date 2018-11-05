package io.github.thenilesh.httpirremote.dao;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class UriConverter {

    @TypeConverter
    public static Uri toUri(String strUri) {
        return strUri == null ? null : Uri.parse(strUri);
    }

    @TypeConverter
    public static String toString(Uri uri) {
        return uri == null ? null : uri.toString();
    }
}
