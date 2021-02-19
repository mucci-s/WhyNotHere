package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.whynothere.R;
import com.mobile.whynothere.UserProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ImageAdaptor extends BaseAdapter {
    List<Bitmap> imageIds;
    JSONArray posts;
    Context mContext;
    String postPhotoUrl;
    private LayoutInflater layoutInflater;
    ImageView imageView;
    public int[] arrayImage = {
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon,
            R.drawable.avatar_icon
    };


    public ImageAdaptor(JSONArray posts,Context mContext) {
        this.mContext = mContext;
        this.posts = posts;
        imageView = new ImageView(mContext);
        layoutInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return posts.length();
    }

    @Override
    public Object getItem(int position) {

        try {
            return posts.getString(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = layoutInflater.inflate(android.R.layout.simple_gallery_item, null);
//            holder = new ViewHolder();
//          //  holder.image = convertView.findViewById(R.id.image_upload);
//
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
////        holder.image.setLayoutParams(new );
//        holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//
//        if(imageIds.size() > 0){
//            holder.image.setImageBitmap(imageIds.get(position));
//        }

//        posts.getJSONObject(position).getJSONArray("photos").get(0)


        try {
            getPost(posts.getString(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //    imageView.setImageResource(arrayImage[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(320,330));


        return imageView;
    }

    static class ViewHolder{
        ImageView image;
    }

    public void getPost(String postID) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"_id\":" + postID + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/getpostbyid";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject post = response.getJSONObject("post");
                    postPhotoUrl = post.getJSONArray("photos").getString(0);

                    Glide.with(mContext)
                            .load(postPhotoUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.progress_bar)
                            .into(imageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


}
//
//    List<Bitmap> imageIds;
//    Context mContext;
//    private LayoutInflater layoutInflater;
//
//
//    public ImageAdaptor(List<Bitmap> imageIds, Context mContext) {
//        this.imageIds = imageIds;
//        this.mContext = mContext;
//        layoutInflater = LayoutInflater.from(mContext);
//    }
//
//    @Override
//    public int getCount() {
//        return imageIds.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//    public Bitmap getBitmapImage(int pos){
//        return imageIds.get(pos);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        convertView = layoutInflater.inflate(R.layout.image_layout, null);
//
//        ImageView imageView = (ImageView) convertView;
//
//        if(imageView == null){
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(400,450));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        }
//
//        imageView.setImageBitmap(imageIds.get(position));
//
//        return imageView;
//    }
//
//    static class ViewHolder{
//        ImageView image;
//        TextView address;
//        TextView coordinates;
//    }
//
//
//}
