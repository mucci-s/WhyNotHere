package com.mobile.whynothere;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class UserSettings extends AppCompatActivity {

    private String userId;
    private String userLogged;

    private ImageView logoutButton;

    private CircularImageView avatarView;
    private EditText nameView;
    private EditText surnameView;
    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private EditText bioView;

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

        this.getUserId();
        this.getUserData(userId);

        this.avatarView = this.findViewById(R.id.profileAvatarID);
        this.nameView = this.findViewById(R.id.profileNameID);
        this.surnameView = this.findViewById(R.id.profileSurnameID);
        this.usernameView = this.findViewById(R.id.profileUsernameID);
        this.emailView = this.findViewById(R.id.profileEmailID);
        this.passwordView = this.findViewById(R.id.profilePasswordID);
        this.confirmPasswordView = this.findViewById(R.id.profileConfirmPasswordID);
        this.bioView = this.findViewById(R.id.profileBioID);
        this.logoutButton = this.findViewById(R.id.logoutButtonID);

    }

    @Override
    protected void onStart() {
        super.onStart();

        this.getUserId();
        this.getUserData(this.userId);
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

                    Glide.with(UserSettings.this)
                            .load(user.getString("photo_profile"))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.avatar_icon)
                            .into(avatarView);

                    userName = user.getString("name");
                    nameView.setText(userName);

                    userSurname = user.getString("surname");
                    surnameView.setText(userSurname);

                    userUsername = user.getString("username");
                    usernameView.setText(userUsername);

                    userEmail = user.getString("email");
                    emailView.setText(userEmail);

                    userPassword = user.getString("password");
                    passwordView.setText(userPassword);
                    confirmPasswordView.setText(userPassword);

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
                    //new JSONObject(userId); //UserLogged?
                    JSONObject userPreferences = new JSONObject(userLoggedPreferences.getString("UserLogged", ""));
                    userPreferences.put("session", false);
                    editor.putString("UserLogged", userPreferences.toString());
                    editor.apply();

                    Toasty.info(getApplicationContext(), "ARRIVEDERCI!", Toast.LENGTH_LONG).show();

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

    public void onClickConfirm(View view) {
        if (this.checkField()) {
            this.checkUsername();
        }
    }

    public void onClickBackground(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public boolean checkField() {
        String name = this.nameView.getText().toString();
        String surname = this.surnameView.getText().toString();
        String username = this.usernameView.getText().toString();
        String password = this.passwordView.getText().toString();
        String confirmPassword = this.confirmPasswordView.getText().toString();
        String bio = this.bioView.getText().toString();

        if ((name.isEmpty()) || (surname.isEmpty()) || (username.isEmpty()) || (password.isEmpty()) || (confirmPassword.isEmpty())) {
            Toasty.error(getApplicationContext(), "COMPILARE TUTTI I CAMPI!", Toast.LENGTH_LONG).show();
            return false;
        } else if (username.length() < 3) {
            Toasty.error(getApplicationContext(), "USERNAME TROPPO CORTO!", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.length() < 9) {
            Toasty.error(getApplicationContext(), "PASSWORD TROPPO CORTA!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(password.equals(confirmPassword))) {
            Toasty.error(getApplicationContext(), "INSERIRE UNA PASSWORD CORRETTA!", Toast.LENGTH_LONG).show();
            return false;
        } else if (bio.length() > 53) {
            Toasty.error(getApplicationContext(), "BIO TROPPO LUNGA!", Toast.LENGTH_LONG).show();
            return false;
        }else return true;
    }

    public void checkUsername() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"username\":" + this.usernameView.getText().toString() + "}");    //Errore con username con spazio
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/user/getuserbyusername";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if ((userUsername.equalsIgnoreCase(usernameView.getText().toString())) || (response.getBoolean("error"))) {
                        showAlertConfirm();
                    } else {
                        Toasty.error(getApplicationContext(), "USERNAME ESISTENTE!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(), "ERRORE!" + error, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    public void showAlertConfirm() {
        AlertDialog.Builder alertMessage = new AlertDialog.Builder(this);
        alertMessage.setTitle("MODIFICA");
        alertMessage.setMessage("Salvare modifiche?");

        alertMessage.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateUser();
                Toasty.success(getApplicationContext(), "MODIFICHE SALVATE CON SUCCESSO!", Toast.LENGTH_LONG).show();
                goToUserProfile();
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

    public void updateUser() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject(
                    "{\"_id\":\"" + this.userId + "\"," +
                            "\"name\":\"" + this.nameView.getText().toString() + "\"," +
                            "\"surname\":\"" + this.surnameView.getText().toString() + "\"," +
                            "\"username\":\"" + this.usernameView.getText().toString() + "\"," +
                            "\"password\":\"" + this.passwordView.getText().toString() + "\"," +
                            "\"bio\":\"" + this.bioView.getText().toString() + "\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/user/updateuser";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(), "ERRORE!" + error, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    public void goToUserProfile() {
        Intent goToProfileIntent = new Intent(this, UserProfileActivity.class);
        this.finish();
        this.startActivity(goToProfileIntent);
    }

}


