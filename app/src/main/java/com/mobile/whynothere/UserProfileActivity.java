package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.fragments.userview.UserLikedPhotoFragment;
import com.mobile.whynothere.fragments.userview.UserPhotoFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity {

    private CircularImageView avatar;
    private TextView name;
    private TextView surname;
    private TextView username;
    private TextView bio;
    private ImageView settingsButton;
    private ImageView spottedPlacesButton;
    private ImageView likedPlacesButton;

    private boolean settingsButtonPressed = false;
    private boolean spottedPlacesPressed = true;
    private boolean likedPlacesPressed = false;

    private BottomNavigationView navigationBar;

    private String userLogged;
    private String userName;
    private String userSurname;
    private String userUsername;
    private String userEmail;
    private String userId;
    private String userPassword;
    private String userBio;

    UserPhotoFragment userPhotoFragment;
    UserLikedPhotoFragment userLikedPhotoFragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userLogged = getIntent().getStringExtra("userLogged");

        System.out.println("UTENTE 1");

        try {
            JSONObject userObject = new JSONObject(userLogged);
            userId = userObject.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("UTENTE X" + userId);

        getUser(userId);

        avatar = (CircularImageView) this.findViewById(R.id.profileAvatarID);
        name = (TextView) this.findViewById(R.id.profileNameID);
        //surname = (TextView) this.findViewById(R.id.profileSurnameID);
        username = (TextView) this.findViewById(R.id.profileUsernameID);
        bio = (TextView) this.findViewById(R.id.profileBioID);
        settingsButton = (ImageView) this.findViewById(R.id.settingsButtonID);
        spottedPlacesButton = (ImageView) this.findViewById(R.id.profileSpottedPlacesID);
        likedPlacesButton = (ImageView) this.findViewById(R.id.profileLikedPlacesID);
        navigationBar = (BottomNavigationView) findViewById(R.id.navigation_bar);

        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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

        userPhotoFragment = new UserPhotoFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutID, userPhotoFragment).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationBar.setSelectedItemId(R.id.profile);
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
            this.spottedPlacesButton.setMaxHeight(20);
            this.spottedPlacesButton.setMaxWidth(20);
        }

        this.likedPlacesPressed = true;
        this.likedPlacesButton.setImageResource(R.drawable.heart_blue);
        this.likedPlacesButton.setMaxHeight(20);
        this.likedPlacesButton.setMaxWidth(20);

        this.userLikedPhotoFragment = new UserLikedPhotoFragment();
        this.fragmentManager.beginTransaction().replace(R.id.frameLayoutID, userLikedPhotoFragment).commit();
    }

    public void onClickSettings(View view) {
        this.settingsButtonPressed = true;
        this.settingsButton.setImageResource(R.drawable.settings_blue);
        this.settingsButton.setMaxHeight(2);
        this.settingsButton.setMaxWidth(2);

        this.goToSettings();
    }

    public void goToHome() {
        Intent goToHomeIntent = new Intent(this, MapsHomeActivity.class);
        goToHomeIntent.putExtra("userLogged", userLogged);
        this.startActivity(goToHomeIntent);
    }

    public void goToSettings() {
        Intent goToSettingsIntent = new Intent(this, UserSettings.class);
        this.startActivity(goToSettingsIntent);
    }

    public void getUser(String userId) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonBody = null;

        try {

            System.out.println("USER ID " + userId);

            jsonBody = new JSONObject("{\"_id\":" + userId + "}");

            System.out.println(jsonBody.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("ERRORE NOSTRO");
        }

        System.out.println("ARRIVA QUA");

        final String url = "https://whynothere-app.herokuapp.com/user/getuserbyid";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject user = response.getJSONObject("user");

                    System.out.println("UTENTE:" + user.toString());
                    userName = user.getString("name");

                    name.setText(userName);

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