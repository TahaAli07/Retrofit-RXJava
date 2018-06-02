package com.example.taha.assignment;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface Api {

    @Streaming
    @GET
    Observable<ResponseBody> getFile(@Url String url);

}
