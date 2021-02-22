package com.mobile.whynothere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.mobile.whynothere.utility.GetImageFromUrl;

public class FullViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view_image);

        String image = getIntent().getExtras().getString("image");
        ImageView imageFull = findViewById(R.id.imageViewFull);
        imageFull.setImageDrawable(getResources().getDrawable(R.drawable.progress_bar));
        new GetImageFromUrl(imageFull,getApplicationContext()).execute(image);

    }
}