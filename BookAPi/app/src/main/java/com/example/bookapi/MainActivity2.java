package com.example.bookapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity2 extends AppCompatActivity {

    ImageView imageView;
    String IMG_URL = "https://books.google.com/books/content?id=a1ZHAQAAMAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api&key=AIzaSyAKKNLnP52Zt24MRk6KFC5zm6d_YUuhtKA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);

        Picasso.get().
                load(IMG_URL).
                placeholder(R.drawable.notice).
                into(imageView,new Callback() {
            @Override
            public void onSuccess() {
                // Image loaded successfully
            }

            @Override
            public void onError(Exception e) {
                // Log the error or take appropriate action
                e.printStackTrace();
                Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}