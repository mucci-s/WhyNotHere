package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.fragments.userview.UserLikedPhotoFragment;
import com.mobile.whynothere.fragments.userview.UserPhotoFragment;
import com.mobile.whynothere.utility.adapters.ImageAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity {

    private CircularImageView avatarView;
    private TextView nameSurnameView;
    private TextView usernameView;
    private TextView bioView;
    private ImageView settingsButton;
    private ImageView spottedPlacesButton;
    private ImageView likedPlacesButton;

    private boolean spottedPlacesPressed = true;
    private boolean likedPlacesPressed = false;

    private String userPassedId;
    private String userId;
    private String userName;
    private String userSurname;
    private String userUsername;
    private String userBio;
    private GridView gridView;

    UserPhotoFragment userPhotoFragment;
    UserLikedPhotoFragment userLikedPhotoFragment;
    FragmentManager fragmentManager;

    private BottomNavigationView navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.getUserId();

        this.avatarView = this.findViewById(R.id.profileAvatarID);
        this.nameSurnameView = this.findViewById(R.id.profileNameSurnameID);
        this.usernameView = this.findViewById(R.id.profileUsernameID);
        this.bioView = this.findViewById(R.id.profileBioID);
        this.settingsButton = this.findViewById(R.id.settingsButtonID);
        this.spottedPlacesButton = this.findViewById(R.id.profileSpottedPlacesID);
        this.likedPlacesButton = this.findViewById(R.id.profileLikedPlacesID);
        this.gridView = this.findViewById(R.id.imageGridID);

        this.userPassedId = getIntent().getStringExtra("userId");
        if (this.userPassedId == null) {
            this.getUserData(this.userId);
        } else if (this.userId.equals(this.userPassedId)) {
            this.getUserData(this.userId);
        } else {
            this.getUserData(userPassedId);
            this.settingsButton.setVisibility(View.INVISIBLE);
        }

        this.navigationBar = (BottomNavigationView) findViewById(R.id.navigation_bar);
        this.navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        goToHome();
                        return true;

                    case R.id.profile:
                        return true;

                    case R.id.addplace:
                        goToAddPlace();
                        return true;
                }
                return false;
            }
        });

        this.userPhotoFragment = new UserPhotoFragment(userId);
        this.fragmentManager = getSupportFragmentManager();
        this.fragmentManager.beginTransaction().replace(R.id.frameLayoutID, userPhotoFragment).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        this.getUserId();

        this.userPassedId = getIntent().getStringExtra("userId");
        if (this.userPassedId == null) {
            this.getUserData(this.userId);
        } else if (this.userId.equals(this.userPassedId)) {
            this.getUserData(this.userId);
        } else {
            this.getUserData(userPassedId);
            this.settingsButton.setVisibility(View.INVISIBLE);
        }

        this.navigationBar.setSelectedItemId(R.id.profile);
        this.settingsButton.setImageResource(R.drawable.settings_grey);

    }

    public void getUserId() {
        SharedPreferences userPreferences = getSharedPreferences("session", MODE_PRIVATE);
        try {
            JSONObject userLogged = new JSONObject(userPreferences.getString("UserLogged", ""));
            this.userId = userLogged.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserData(String userId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"_id\":" + userId + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/user/getuserbyid";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject user = response.getJSONObject("user");
                    Glide.with(UserProfileActivity.this)
                            .load(user.getString("photo_profile"))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.avatar_icon)
                            .into(avatarView);

                    userName = user.getString("name");
                    userSurname = user.getString("surname");
                    nameSurnameView.setText(userName + " " + userSurname);
                    userUsername = user.getString("username");
                    usernameView.setText(userUsername);
                    userBio = user.getString("bio");
                    bioView.setText(userBio);
                    userPhotoFragment.setUserID(user.getString("_id"));

//                    ImageAdaptor defaultImageAdaptor = new ImageAdaptor(getApplicationContext());
//                    gridView.setAdapter(defaultImageAdaptor);


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

    public void onClickNearlyButton(View view) {
        goToHome();
    }

    public void onClickSpottedPlaces(View view) {
        if (this.likedPlacesPressed) {
            this.likedPlacesPressed = false;
            this.likedPlacesButton.setImageResource(R.drawable.heart_grey);
            this.likedPlacesButton.setMaxHeight(2);
            this.likedPlacesButton.setMaxWidth(2);
        }

        this.spottedPlacesPressed = true;

        this.spottedPlacesButton.setImageResource(R.drawable.map_blue);
        this.spottedPlacesButton.setMaxHeight(2);
        this.spottedPlacesButton.setMaxWidth(2);

        this.userPhotoFragment = new UserPhotoFragment(userId);
        //Quando si va indietro potrebbe crashare, vedere video su yt
        this.fragmentManager.beginTransaction().replace(R.id.frameLayoutID, userPhotoFragment).commit();
        //  userPhotoFragment.setPics(userId);
    }

    public void onClickLikedPlaces(View view) {
        if (this.spottedPlacesPressed) {
            this.spottedPlacesPressed = false;
            this.spottedPlacesButton.setImageResource(R.drawable.map_grey);
        }

        this.likedPlacesPressed = true;
        this.likedPlacesButton.setImageResource(R.drawable.heart_blue);

        this.userLikedPhotoFragment = new UserLikedPhotoFragment();
        this.fragmentManager.beginTransaction().replace(R.id.frameLayoutID, userLikedPhotoFragment).commit();
    }

    public void onClickSettings(View view) {
        this.settingsButton.setImageResource(R.drawable.settings_blue);
        this.goToSettings();
    }

    public void goToHome() {
        Intent goToHomeIntent = new Intent(this, MapsHomeActivity.class);
        //goToHomeIntent.putExtra("userLogged", userLogged);
        this.startActivity(goToHomeIntent);
    }

    public void goToSettings() {
        Intent goToSettingsIntent = new Intent(this, UserSettings.class);
        //goToSettingsIntent.putExtra("userLogged", userLogged);
        this.startActivity(goToSettingsIntent);
    }

    public void goToAddPlace() {
        Intent goToAddPlaceIntent = new Intent(this, NewPlaceActivity.class);
        this.startActivity(goToAddPlaceIntent);
    }

}