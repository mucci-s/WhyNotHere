package com.mobile.whynothere.utility.adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

import org.json.JSONArray;
import org.json.JSONException;

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
        try {
            Glide.with(context)
                    .load(photos.getString(position))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.loader_bar)
                    .into(holder.mImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        holder.mImage.setImageURI(picUri);

    }


    @Override
    public int getItemCount() {
        return photos.length();
    }


    public void showDialogBox(String image, Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        ImageView imageView = dialog.findViewById(R.id.img);

        Glide.with(context)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.loader_bar)
                .into(imageView);




        dialog.show();
    }


}
