package com.example.taha.assignment;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import rx.Observable;


public interface Api {

    @Streaming
    @GET("/jsonparsetutorial.txt")
    Observable<ResponseBody>getdata();

}
