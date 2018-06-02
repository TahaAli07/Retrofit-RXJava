package com.example.taha.assignment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import okhttp3.ResponseBody;
import okhttp3.internal.io.FileSystem;
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
    Button btn_saveContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btn_saveContacts = (Button) findViewById(R.id.saveContacts);

        getCountryData();

        //Item touch Listener fo the recycler view
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Intent intent = new Intent(MainActivity.this, ImageClass.class);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageCountry);
                Drawable drawable = imageView.getDrawable();
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                Bitmap bitmap = bitmapDrawable.getBitmap();
                intent.putExtra("DATA", bitmap);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        //OnClickListener for Save Contacts Button
        btn_saveContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Asking for runtime permissions
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        });


    }

    public void getCountryData() {

        //Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.androidbegin.com/tutorial/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        //Creating object of Api class
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

                        //Converting Response to JSONObject
                        try {
                            jsonObject = new JSONObject(responseString);
                            Log.d("TAHA", jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        list = new ArrayList<>();
                        //Getting out the world population array
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

                            //adding data to List
                            list.add(countryModel);
                            Log.d("TAHA COUNTRY MODEL", list.toString());

                        }

                        //Setting Adapter for RecyclerView
                        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list);
                        RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setLayoutManager(recyce);
                        recyclerView.setAdapter(recyclerAdapter);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    saveContacts();
                } else {

                    // permission denied
                    Toast.makeText(MainActivity.this, "Permissions are Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    public void saveContacts() {

        //getting out contacts in a cursor
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        Log.d("TAHA CURSOR COUNT", String.valueOf(cursor.getCount()));

        List<String[]> data = new ArrayList<String[]>();

        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().concat("/contacts.csv");
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv));
        } catch (IOException e) {
            Log.d("TAHA NULL CSV WRIER", e.getMessage());
            e.printStackTrace();
        }

        //Storing id, name , number in List "data"
        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            data.add(new String[]{id, name, phoneNumber});

        }

        //Writing List "data" to CSV
        if (writer != null) {
            writer.writeAll(data);
            Toast.makeText(this, "SUCCESSFULLY CREATED CSV FROM CONTACTS", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "COULD NOT SAVE CONTACTS TO CSV", Toast.LENGTH_SHORT).show();
        }

        //Closing cursor and writer
        cursor.close();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileHelper fileHelper = new FileHelper();

        fileHelper.zip(android.os.Environment.getExternalStorageDirectory().getAbsolutePath().concat("/contacts.csv")
                , android.os.Environment.getExternalStorageDirectory().getAbsolutePath(), "TAHACONTACTS.zip"
                , false);

        /*File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath().concat("/contacts.zip"));
        addFilesToZip(file
                ,new File[]{new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath().concat("/contacts.csv"))});*/

    }

    private void storeZIP() throws IOException {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(android.os.Environment.getExternalStorageDirectory().getAbsolutePath().concat("/contacts.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("contacts.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ZipOutputStream zos = new ZipOutputStream(fos);

        ZipEntry ze = new ZipEntry("contacts.csv");

        zos.putNextEntry(ze);
        zos.closeEntry();
        zos.close();

        fis.close();
        fos.close();

    }

    public static void addFilesToZip(File source, File[] files) {
        try {
            File tmpZip = File.createTempFile(source.getName(), null);
            tmpZip.delete();
            if (!source.renameTo(tmpZip)) {
                throw new Exception("Could not make temp file (" + source.getName() + ")");
            }
            byte[] buffer = new byte[1024 * 100000];
            ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpZip));
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(source));

            for (int i = 0; i < files.length; i++) {
                InputStream in = new FileInputStream(files[i]);
                out.putNextEntry(new ZipEntry(files[i].getName()));
                for (int read = in.read(buffer); read > -1; read = in.read(buffer)) {
                    out.write(buffer, 0, read);
                }
                out.closeEntry();
                in.close();
            }

            for (ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
                out.putNextEntry(ze);
                for (int read = zin.read(buffer); read > -1; read = zin.read(buffer)) {
                    out.write(buffer, 0, read);
                }
                out.closeEntry();
            }

            out.close();
            tmpZip.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
