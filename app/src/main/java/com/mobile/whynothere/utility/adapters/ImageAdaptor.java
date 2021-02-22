package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.whynothere.R;
import com.mobile.whynothere.ViewPlaceActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ImageAdaptor extends BaseAdapter {
    List<Bitmap> imageIds;
    JSONArray posts;
    Context mContext;
    String postPhotoUrl;
    private LayoutInflater layoutInflater;
    ImageView imageView;



    public ImageAdaptor( JSONArray posts,Context mContext) {
        this.mContext = mContext;
        this.posts = posts;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return posts.length();
    }

    @Override
    public Object getItem(int position) {

        try {
            return posts.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        imageView = new ImageView(mContext);

        try {
            Glide.with(mContext)
                    .load(posts.getJSONObject(position).getJSONArray("photos").getString(0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.progress_bar)
                    .into(imageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(320, 330));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPlace = new Intent(mContext, ViewPlaceActivity.class);
                try {
                    goToPlace.putExtra("placeId",posts.getJSONObject(position).getString("_id"));
                    mContext.startActivity(goToPlace);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        return imageView;
    }

}

