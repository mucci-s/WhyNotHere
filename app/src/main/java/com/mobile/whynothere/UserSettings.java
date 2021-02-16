package com.mobile.whynothere;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSettings extends AppCompatActivity {

    private String userId;

    private String userLogged;

    private ImageView logoutButton;

    private CircularImageView avatar;
    private EditText name;
    private EditText surname;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText bio;

    private String userName;
    private String userSurname;
    private String userUsername;
    private String userEmail;
    private String userPassword;
    private String userBio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        this.userLogged = getIntent().getStringExtra("userLogged");

        try {
            JSONObject userObject = new JSONObject(userLogged);
            userId = userObject.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.getUserData(userId);

        this.avatar = (CircularImageView) this.findViewById(R.id.profileAvatarID);
        this.name = (EditText) this.findViewById(R.id.profileNameID);
        this.surname = (EditText) this.findViewById(R.id.profileSurnameID);
        this.username = (EditText) this.findViewById(R.id.profileUsernameID);
        this.email = (EditText) this.findViewById(R.id.profileEmailID);
        this.password = (EditText) this.findViewById(R.id.profilePasswordID);
        this.confirmPassword = (EditText) this.findViewById(R.id.profileConfirmPasswordID);
        this.bio = (EditText) this.findViewById(R.id.profileBioID);
        this.logoutButton = (ImageView) this.findViewById(R.id.logoutButtonID);
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
                    name.setText(userName);

                    userSurname = user.getString("surname");
                    surname.setText(userSurname);

                    userUsername = user.getString("username");
                    username.setText(userUsername);

                    userEmail = user.getString("email");
                    email.setText(userEmail);

                    userPassword = user.getString("password");
                    password.setText(userPassword);
                    confirmPassword.setText(userPassword);

                    userBio = user.getString("bio");
                    bio.setText(userBio);

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

    public void onClickLogout(View view) {
        this.logoutButton.setImageResource(R.drawable.logout_blue);

        AlertDialog.Builder alertMessage = new AlertDialog.Builder(this);
        alertMessage.setTitle("Logout");
        alertMessage.setMessage("Sei sicuro di voler uscire?");

        alertMessage.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences userLoggedPreferences = getSharedPreferences("session", MODE_PRIVATE);
                SharedPreferences.Editor editor = userLoggedPreferences.edit();

                try {
                    JSONObject userPreferences = new JSONObject(userLogged);
                    userPreferences.put("session", false);
                    editor.putString("UserLogged", userPreferences.toString());
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "ARRIVEDERCI!", Toast.LENGTH_LONG).show();

                    goToLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        alertMessage.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutButton.setImageResource(R.drawable.logout_grey);
            }
        });

        AlertDialog alertDialog = alertMessage.create();
        alertDialog.show();

    }

    public void goToLogin() {
        Intent goToLoginIntent = new Intent(this, LoginActivity.class);
        goToLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(goToLoginIntent);
    }

}