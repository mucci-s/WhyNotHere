package com.mobile.whynothere.fragments.userview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.whynothere.MapsHomeActivity;
import com.mobile.whynothere.R;
import com.mobile.whynothere.UserProfileActivity;
import com.mobile.whynothere.utility.adapters.CustomImageAdapterProfile;
import com.mobile.whynothere.utility.adapters.ImageAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class UserPhotoFragment extends Fragment {

    private GridView gridView;
    private String userId;

    public UserPhotoFragment(String userId) {
        this.userId = userId;
    }

    int[] defaultImages = {
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.default_icon_place,
            R.drawable.avatar_icon,
            R.drawable.heart_grey,
            R.drawable.map_blue,
            R.drawable.avatar_icon
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_photo, container, false);


        this.gridView = root.findViewById(R.id.imageGridID);
        this.getUserPosts(userId);


        return root;
    }

    public void setPics(String userid) {

    }

    public void setUserID(String userID ){
        userId = userID;
    }


    public void getUserPosts(String userId) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"_id\":" + userId + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/getuserposts";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                ImageAdaptor defaultImageAdaptor = null;
                try {
                    JSONArray posts =  response.getJSONArray("userposts");

                    defaultImageAdaptor = new ImageAdaptor(posts,getContext());
                    gridView.setAdapter(defaultImageAdaptor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Permission needed", Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
