package com.mobile.whynothere.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.mobile.whynothere.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Image extends AppCompatActivity {

    ViewPager mPager;
    List<Bitmap> bitmapImages = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        mPager = this.findViewById(R.id.pager);

       // List<Integer> images = getIntent().getExtras().getIntegerArrayList("images");

        List<Uri> images = getIntent().getExtras().getParcelableArrayList("images");

        int currentImage = getIntent().getExtras().getInt("current");

        Log.d("DEBUG", String.valueOf(images));

        for (int i = 0; i < images.size();i++){

            try {
                bitmapImages.add(MediaStore.Images.Media.getBitmap(this.getContentResolver(),images.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}




