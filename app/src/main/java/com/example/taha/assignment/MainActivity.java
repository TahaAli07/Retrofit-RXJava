package com.example.taha.assignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<CountryModel> list;
    String responseString;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        getCountryData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Intent intent = new Intent(MainActivity.this, ImageClass.class);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageCountry);
                Drawable drawable = imageView.getDrawable();
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                Bitmap bitmap = bitmapDrawable .getBitmap();
                intent.putExtra("DATA",bitmap );
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    public void getCountryData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.androidbegin.com/tutorial/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Api apiService = retrofit.create(Api.class);
        apiService.getFile("http://www.androidbegin.com/tutorial/jsonparsetutorial.txt")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.d("TAHA ONCOMPLETED", "COMPLETED TRANSFER");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAHA ONERROR", e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            responseString = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.d("TAHA", responseString);

                        try {
                            jsonObject = new JSONObject(responseString);
                            Log.d("TAHA", jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        list = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.optJSONArray("worldpopulation");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryModel countryModel = new CountryModel();
                            JSONObject jsonobject = null;
                            try {
                                jsonobject = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String rank = jsonobject.getString("rank");
                                countryModel.setRank(Integer.valueOf(rank));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String country = jsonobject.getString("country");
                                countryModel.setCountry(country);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String population = jsonobject.getString("population");
                                Long l = Long.parseLong(population.replaceAll(",", ""));
                                countryModel.setPopulation(l);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String image = jsonobject.getString("flag");
                                countryModel.setImage(image);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            list.add(countryModel);
                            Log.d("TAHA COUNTRY MODEL", list.toString());

                        }

                        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list);
                        RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(recyce);
                        recyclerView.setAdapter(recyclerAdapter);
                    }
                });
    }
}
