package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.fragments.userview.UserLikedPhotoFragment;
import com.mobile.whynothere.fragments.userview.UserPhotoFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity {

    //this.spottedPlacesButton.setMaxHeight(20); Problemi con icon
    //this.spottedPlacesButton.setMaxWidth(20);

    private CircularImageView avatarView;
    private TextView nameSurnameView;
    private TextView usernameView;
    private TextView bioView;
    private ImageView settingsButton;
    private ImageView spottedPlacesButton;
    private ImageView likedPlacesButton;

    private boolean spottedPlacesPressed = true;
    private boolean likedPlacesPressed = false;

    private BottomNavigationView navigationBar;

    private String userLogged;

    private String userName;
    private String userSurname;
    private String userUsername;
    private String userId;
    private String userBio;

    UserPhotoFragment userPhotoFragment;
    UserLikedPhotoFragment userLikedPhotoFragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.userLogged = getIntent().getStringExtra("userLogged");

        try {
            JSONObject userObject = new JSONObject(userLogged);
            userId = userObject.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.getUserData(userId);  //Se lo metto dopo è meglio?

        this.avatarView = (CircularImageView) this.findViewById(R.id.profileAvatarID);
        this.nameSurnameView = (TextView) this.findViewById(R.id.profileNameSurnameID);
        this.usernameView = (TextView) this.findViewById(R.id.profileUsernameID);
        this.bioView = (TextView) this.findViewById(R.id.profileBioID);
        this.settingsButton = (ImageView) this.findViewById(R.id.settingsButtonID);
        this.spottedPlacesButton = (ImageView) this.findViewById(R.id.profileSpottedPlacesID);
        this.likedPlacesButton = (ImageView) this.findViewById(R.id.profileLikedPlacesID);

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
                        return true;

                }
                return false;
            }
        });

        this.userPhotoFragment = new UserPhotoFragment();
        this.fragmentManager = getSupportFragmentManager();
        this.fragmentManager.beginTransaction().replace(R.id.frameLayoutID, userPhotoFragment).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        this.userLogged = getIntent().getStringExtra("userLogged");

        try {
            JSONObject userObject = new JSONObject(userLogged);
            userId = userObject.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        this.navigationBar.setSelectedItemId(R.id.profile);

        this.getUserData(this.userId);

        this.settingsButton.setImageResource(R.drawable.settings_grey);
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

                    userName = user.getString("name");
                    userSurname = user.getString("surname");
                    nameSurnameView.setText(userName + " " + userSurname);

                    userUsername = user.getString("username");
                    usernameView.setText(userUsername);

                    userBio = user.getString("bio");
                    bioView.setText(userBio);

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

        this.userPhotoFragment = new UserPhotoFragment();
        //Quando si va indietro potrebbe crashare, vedere video su yt
        this.fragmentManager.beginTransaction().replace(R.id.frameLayoutID, userPhotoFragment).commit();
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
        goToHomeIntent.putExtra("userLogged", userLogged);
        this.startActivity(goToHomeIntent);
    }

    public void goToSettings() {
        Intent goToSettingsIntent = new Intent(this, UserSettings.class);
        goToSettingsIntent.putExtra("userLogged", userLogged);
        this.startActivity(goToSettingsIntent);
    }

}