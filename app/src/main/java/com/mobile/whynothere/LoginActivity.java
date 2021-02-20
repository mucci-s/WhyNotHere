package com.mobile.whynothere;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.whynothere.utility.LoginFormState;
import com.mobile.whynothere.utility.emailsender.JavaMailAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextView registerButton;
    LoginFormState loginFormState;
    private EditText emailEditText;
    private EditText passwordEditText;

    private TextView lostPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences userPreferences = getSharedPreferences("session", MODE_PRIVATE);
        try {
            JSONObject userLogged = new JSONObject(userPreferences.getString("UserLogged", ""));
            if (userLogged.getBoolean("session")) {
                goToHome(userLogged);
            } else {
                Toast.makeText(getApplicationContext(), "Accedi", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        registerButton = (TextView) findViewById(R.id.singnupButtonId);
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        lostPassword = findViewById(R.id.lostPasswordID);

        final Button loginButton = findViewById(R.id.login_button);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginDataChanged(emailEditText.getText().toString(), passwordEditText.getText().toString());

                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));

                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }

            }
        };

        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
    }

    @Override
    protected void onResume() {
        emailEditText.setError(null);
        passwordEditText.setError(null);
        super.onResume();

    }

    @Override
    protected void onStop() {
        emailEditText.getText().clear();
        passwordEditText.getText().clear();
        super.onStop();
    }

    public void onClickSignUp(View view) {
        Intent goToRegistrationIntent = new Intent(this, RegistrationActivity.class);
        this.finish();
        this.startActivity(goToRegistrationIntent);
    }

    public void goToHome(JSONObject userInfo) {
        Intent goToHomeIntent = new Intent(this, MapsHomeActivity.class);
        goToHomeIntent.putExtra("user", userInfo.toString());
        this.startActivity(goToHomeIntent);
        this.finish();
    }


    public void signInClick(View view) {
        this.checkCredential(emailEditText.getText().toString().trim(), passwordEditText.getText().toString());
    }

    public void loginDataChanged(String username, String password) {

        if (!isUserNameValid(username)) {
            loginFormState = new LoginFormState(R.string.invalid_username, null);
        } else if (!isPasswordValid(password)) {
            loginFormState = new LoginFormState(null, R.string.invalid_password);
        } else {
            loginFormState = new LoginFormState(true);
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 8;
    }


    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return true;
        } else {
            return false;
        }
    }

    private void signInAction(JSONObject user) {

        SharedPreferences sessionPreferences = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sessionPreferences.edit();
        editor.putString("UserLogged", user.toString());
        editor.apply();

        goToHome(user);

    }

    private void checkCredential(final String email, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(
                    "{\"email\":\"" + email + "\"," +
                            "\"password\":\"" + password + "\"}");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/user/checkLoginCredentials";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int i = 0;

                try {
                    if (!response.getBoolean("password")) {
                        passwordEditText.setError("Password errata");
                        Toast.makeText(getApplicationContext(), "errore", Toast.LENGTH_LONG).show();
                    }
                    if (!response.getBoolean("email")) {
                        emailEditText.setError("Email non trovata");
                        Toast.makeText(getApplicationContext(), "errore", Toast.LENGTH_LONG).show();
                    }

                    if (response.getBoolean("password") &&
                            response.getBoolean("email")) {
                        JSONObject user = response.getJSONObject("user");
                        user.put("session", true);

                        signInAction(user);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void onClickLostPassword(View view) {

        String userEmail = this.emailEditText.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"email\":" + userEmail + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/user/getuserbyemail";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), "EMAIL INESISTENTE!", Toast.LENGTH_LONG).show();
                    } else {

                        JSONObject user = response.getJSONObject("user");
                        String userLostPassword = user.getString("password");

                        JavaMailAPI javaMailAPI = new JavaMailAPI(getApplicationContext(), userEmail, "Recupero password", "La tua password è: " + userLostPassword);
                        javaMailAPI.execute();

                        Toast.makeText(getApplicationContext(), "TI ABBIAMO INVIATO UN'EMAIL!", Toast.LENGTH_LONG).show();
                    }

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

    public void onClickBackground(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

}

//android:icon="@mipmap/ic_launcher"