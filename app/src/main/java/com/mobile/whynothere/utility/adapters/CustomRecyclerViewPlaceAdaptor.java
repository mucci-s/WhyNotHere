package com.mobile.whynothere.utility.adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.whynothere.R;
import com.mobile.whynothere.UserProfileActivity;
import com.mobile.whynothere.utility.GetImageFromUrl;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CustomRecyclerViewPlaceAdaptor extends RecyclerView.Adapter<CustomRecyclerViewPlaceAdaptor.MyViewHolder> {

    private Context context;
    private JSONArray photos;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public CustomRecyclerViewPlaceAdaptor(Context context, JSONArray photos) {
        this.context = context;
        this.photos = photos;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.image);

            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        showDialogBox(photos.getString(getAdapterPosition()), context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_list_photo_newplace, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

//            Glide.with(context)
//                    .load("https://res.cloudinary.com/whynothereimages/image/upload/v1613868470/post_image/6031adb30fb5fe0004d1ae15/icelpyghelbfxoqzyff5.png")
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .placeholder(R.drawable.progress_bar)
//                    .into(holder.mImage);

          holder.mImage.setImageResource(R.drawable.progress_bar);
        try {
            new GetImageFromUrl(holder.mImage, context).execute(photos.getString(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                .permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        Bitmap bmp = null;
//        try {
//            URL url = new URL("https://res.cloudinary.com/whynothereimages/image/upload/v1613868470/post_image/6031adb30fb5fe0004d1ae15/icelpyghelbfxoqzyff5.png");
//            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        holder.mImage.setImageBitmap(bmp);



    }


    @Override
    public int getItemCount() {
        return photos.length();
    }


    public void showDialogBox(String image, Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        ImageView imageView = dialog.findViewById(R.id.img);

        imageView.setImageResource(R.drawable.progress_bar);

    }

//    Bitmap bitmap;
//
//    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
//        ImageView imageView;
//        public GetImageFromUrl(ImageView img){
//            this.imageView = img;
//        }
//        @Override
//        protected Bitmap doInBackground(String... url) {
//            String stringUrl = url[0];
//            bitmap = null;
//            InputStream inputStream;
//            try {
//                inputStream = new java.net.URL(stringUrl).openStream();
//                bitmap = BitmapFactory.decodeStream(inputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//        @Override
//        protected void onPostExecute(Bitmap bitmap){
//            super.onPostExecute(bitmap);
//            imageView.setImageBitmap(bitmap);
//        }
//    }


}
