package io.github.thenilesh.httpirremote.utils;

import io.github.thenilesh.httpirremote.dto.IRCode;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface IRRemoteService {

    @GET("/code")
    Call<IRCode> receiveIrCode(@Url String url);

    @POST("/code")
    Call<Void> sendIrCode(@Url String url, @Body IRCode irCode);
}
