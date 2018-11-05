package io.github.thenilesh.httpirremote.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.43.124:8080")
            .build();

    public static <T> T create(Class<T> clazz) {
        return retrofit.create(clazz);
    }
}
