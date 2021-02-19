package com.mobile.whynothere.utility.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.R;
import com.mobile.whynothere.models.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CustomListAdapter  extends BaseAdapter {

    private JSONArray listComment;
    private LayoutInflater layoutInflater;
    private Context context;
    private String username;
    private String icon;

    public CustomListAdapter(JSONArray listComment, Context acontext) {
        this.listComment = listComment;
        this.context = acontext;
        layoutInflater = LayoutInflater.from(acontext);
    }

    @Override
    public int getCount() { return listComment.length();  }

    @Override
    public Object getItem(int i) {
        try {
            return listComment.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return null;
    }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {

            try {
                getAuthor(listComment.getJSONObject(i).getString("user_id"),i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            view = layoutInflater.inflate(R.layout.list_comment_layout, null);
            holder = new ViewHolder();
            holder.authorImageView = view.findViewById(R.id.listProfileAvatarID);
            holder.authorNameView = view.findViewById(R.id.titleNewPlaceID);
            holder.commentTextView = view.findViewById(R.id.descriptionNewPlaceID);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        URL url = null;

        JSONObject comment = null;
        try {
            comment = this.listComment.getJSONObject(i);
            holder.commentTextView.setText(comment.getString("description"));
            holder.authorNameView.setText(username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            comment.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //holder.authorImageView.setImageResource(comment.getPhotoProfile());

        return view;
    }

    private void getAuthor(String authorId, int i) {

        RequestQueue requestQueue = Volley.newRequestQueue(this.context);

        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"_id\":" + authorId + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url = "https://whynothere-app.herokuapp.com/user/getuserbyid";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject user = response.getJSONObject("user");
                    username = user.getString("name");
                    icon = user.getString("photo_profile");
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

    static class ViewHolder{
        CircularImageView authorImageView;
        TextView authorNameView;
        TextView commentTextView;
    }
}
