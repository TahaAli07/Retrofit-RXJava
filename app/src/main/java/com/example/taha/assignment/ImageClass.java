package com.example.taha.assignment;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

//This class contains just a ImageView
public class ImageClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_class);

        //getting out image from intent and showing it in ImageView
        Bitmap bitmap = this.getIntent().getParcelableExtra("DATA");
        ImageView viewBitmap = (ImageView) findViewById(R.id.bitmapview);
        viewBitmap.setImageBitmap(bitmap);
    }
}
