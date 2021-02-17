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
import android.widget.TextView;
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

    private CircularImageView avatarView;
    private EditText nameView;
    private EditText surnameView;
    private EditText usernameView;
    private TextView emailView;
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

       /* this.userLogged = getIntent().getStringExtra("userLogged");

        try {
            JSONObject userObject = new JSONObject(userLogged);
            userId = userObject.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        this.getUserId();
        this.getUserData(userId);

        this.avatarView = (CircularImageView) this.findViewById(R.id.profileAvatarID);
        this.nameView = (EditText) this.findViewById(R.id.profileNameID);
        this.surnameView = (EditText) this.findViewById(R.id.profileSurnameID);
        this.usernameView = (EditText) this.findViewById(R.id.profileUsernameID);
        this.emailView = (TextView) this.findViewById(R.id.profileEmailID);
        this.passwordView = (EditText) this.findViewById(R.id.profilePasswordID);
        this.confirmPasswordView = (EditText) this.findViewById(R.id.profileConfirmPasswordID);
        this.bioView = (EditText) this.findViewById(R.id.profileBioID);
        this.logoutButton = (ImageView) this.findViewById(R.id.logoutButtonID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.getUserId();
        this.getUserData(this.userId);
    }


    public void getUserId(){
        SharedPreferences userPreferences = getSharedPreferences("session",MODE_PRIVATE);
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

    public void onClickConfirm(View view) {
        if (this.checkField()) {
            this.checkUsername();
        }
    }

    public boolean checkField() {
        String name = this.nameView.getText().toString();
        String surname = this.surnameView.getText().toString();
        String username = this.usernameView.getText().toString();
        String password = this.passwordView.getText().toString();
        String confirmPassword = this.confirmPasswordView.getText().toString();

        if ((name.isEmpty()) || (surname.isEmpty()) || (username.isEmpty()) || (password.isEmpty()) || (confirmPassword.isEmpty())) {
            Toast.makeText(getApplicationContext(), "COMPILARE TUTTI I CAMPI!", Toast.LENGTH_LONG).show();
            return false;
        } else if (username.length() < 3) {
            Toast.makeText(getApplicationContext(), "USERNAME TROPPO CORTO!", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.length() < 9) {
            Toast.makeText(getApplicationContext(), "PASSWORD TROPPO CORTA!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(password.equals(confirmPassword))) {
            Toast.makeText(getApplicationContext(), "INSERIRE UNA PASSWORD CORRETTA!", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
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
                        Toast.makeText(getApplicationContext(), "USERNAME ESISTENTE!!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERRORE!" + error, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "MODIFICHE SALVATE CON SUCCESSO!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "ERRORE!" + error, Toast.LENGTH_LONG).show();
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


