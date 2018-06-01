package com.example.taha.assignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<CountryModel> list;
    String responseString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        getCountryData();
    }

    public void getCountryData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.androidbegin.com/tutorial/")
                /*.addConverterFactory(GsonConverterFactory.create())*/
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Api apiService = retrofit.create(Api.class);

        Observable<ResponseBody> observable = apiService.getdata()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {
                Log.d("TAHA INSIDE ON COMPLETE","COMPLETED");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("TAHA INSIDE ON ERROR ",e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(ResponseBody responseBody) {

                try {
                    responseString = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                responseString= responseBody.toString();

                Log.d("TAHA", responseString);

                /*Log.d("THIS IS THE LIST", countryModelList.toString());

                list = new ArrayList<>();
                for (int i = 0; i < countryModelList.size(); i++) {

                    CountryModel countryModel = new CountryModel();
                    countryModel.setRank(countryModelList.get(i).getRank());
                    countryModel.setCountry(countryModelList.get(i).getCountry());
                    countryModel.setImage(countryModelList.get(i).getImage());
                    countryModel.setPopulation(countryModelList.get(i).getPopulation());
                    list.add(countryModel);
                }*/

                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list);
                RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                /*recyclerView.addItemDecoration(new GridSpacingdecoration(2, dpToPx(10), true));
                recyclerView.setItemAnimator( new DefaultItemAnimator());*/
                recyclerView.setLayoutManager(recyce);
                recyclerView.setAdapter(recyclerAdapter);

            }
        });
    }

}
