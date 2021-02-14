package com.mobile.whynothere;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private CircularImageView avatar;
    private EditText name;
    private EditText surname;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText bio;
    private Button registrationButton;

    private static final int SELECT_AVATAR = 1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        avatar = (CircularImageView) this.findViewById(R.id.profileAvatarID);
        name = (EditText) this.findViewById(R.id.nameID);
        surname = (EditText) this.findViewById(R.id.surnameID);
        username = (EditText) this.findViewById(R.id.usernameID);
        email = (EditText) this.findViewById(R.id.emailID);
        password = (EditText) this.findViewById(R.id.passwordID);
        confirmPassword = (EditText) this.findViewById(R.id.confirmPasswordID);
        bio = (EditText) this.findViewById(R.id.bioID);
        registrationButton = (Button) this.findViewById(R.id.registrationButtonID);
    }

    public void onClickAvatar(View view) {
        Intent avatarPickerIntent = new Intent(Intent.ACTION_PICK);
        avatarPickerIntent.setType("image/*");
        avatarPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(avatarPickerIntent, "Select avatar"), SELECT_AVATAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_AVATAR && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                avatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onClickRegistration(View view) {
        if (this.checkField()) {
            insertUser();
        }
    }
    public void insertUser() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(
                    "{\"email\":\"" + this.email.getText().toString() + "\"," +
                            "\"name\":\"" + this.name.getText().toString() + "\"," +
                            "\"surname\":\"" + this.surname.getText().toString() + "\"," +
                            "\"username\":\"" + this.username.getText().toString() + "\"," +
                            "\"password\":\"" + this.password.getText().toString() + "\"," +
                            "\"bio\":\"" + this.bio.getText().toString() + "\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url = "https://whynothere-app.herokuapp.com/user/createuser";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("userresult")) {
                        Toast.makeText(getApplicationContext(), "BENVENUTO!", Toast.LENGTH_LONG).show();
                        //GO TO HOME
                    } else {
                        if ((response.getJSONObject("message").getJSONObject("keyPattern").has("email")) &&
                                (response.getJSONObject("message").getJSONObject("keyPattern").getInt("email") == 1)) {
                            Toast.makeText(getApplicationContext(), "EMAIL ESISTENTE!", Toast.LENGTH_LONG).show();
                        } else if ((response.getJSONObject("message").getJSONObject("keyPattern").has("username")) &&
                                (response.getJSONObject("message").getJSONObject("keyPattern").getInt("username") == 1)) {
                            Toast.makeText(getApplicationContext(), "USERNAME ESISTENTE!", Toast.LENGTH_LONG).show();
                        }
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

    private boolean checkField() {
        String name = this.name.getText().toString();
        String surname = this.surname.getText().toString();
        String username = this.username.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String confirmPassword = this.confirmPassword.getText().toString();
        if ((name.isEmpty()) || (surname.isEmpty()) || (username.isEmpty()) || (email.isEmpty()) || (password.isEmpty()) || (confirmPassword.isEmpty())) {
            Toast.makeText(getApplicationContext(), "COMPILARE TUTTI I CAMPI!", Toast.LENGTH_LONG).show();
            return false;
        } else if (username.length() < 3) {
            Toast.makeText(getApplicationContext(), "USERNAME TROPPO CORTO!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(email.contains("@")) || (!(email.contains(".")))) {
            Toast.makeText(getApplicationContext(), "INSERIRE UNA EMAIL VALIDA!", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.length() < 9) {
            Toast.makeText(getApplicationContext(), "PASSWORD TROPPO CORTA!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(password.equals(confirmPassword))) {
            Toast.makeText(getApplicationContext(), "INSERIRE UNA PASSWORD CORRETTA!", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }

}