package com.mobile.whynothere.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.mobile.whynothere.R;

import java.io.IOException;
import java.io.InputStream;


public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;
    Bitmap bitmap;
    Context context;

    public GetImageFromUrl(ImageView img, Context context) {
        this.imageView = img;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        String stringUrl = url[0];

        bitmap = null;
        InputStream inputStream;
        try {
            if (stringUrl.equalsIgnoreCase("avatar_icon_qtc2se")) {
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.avatar_icon);
            } else {
                inputStream = new java.net.URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
    }
}
