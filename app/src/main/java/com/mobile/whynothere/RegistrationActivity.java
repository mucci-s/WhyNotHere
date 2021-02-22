package com.mobile.whynothere;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.utility.ImageResizer;
import com.mobile.whynothere.utility.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity {

    private CircularImageView avatar;
    private Bitmap imageSelected;
    private EditText name;
    private EditText surname;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText bio;
    private Button registrationButton;
    private TextView loginTextView;

    private static final int SELECT_AVATAR = 1;
    private static final String UPLOAD_URL = "https://whynothere-app.herokuapp.com/user/uploaduserphoto";
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        avatar = (CircularImageView) this.findViewById(R.id.listProfileAvatarID);
        name = (EditText) this.findViewById(R.id.nameID);
        surname = (EditText) this.findViewById(R.id.surnameID);
        username = (EditText) this.findViewById(R.id.usernameID);
        email = (EditText) this.findViewById(R.id.emailID);
        password = (EditText) this.findViewById(R.id.passwordID);
        confirmPassword = (EditText) this.findViewById(R.id.confirmPasswordID);
        bio = (EditText) this.findViewById(R.id.bioID);
        registrationButton = (Button) this.findViewById(R.id.registrationButtonID);
        loginTextView = (TextView) this.findViewById(R.id.loginTextViewID);
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
                imageSelected = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                avatar.setImageBitmap(imageSelected);
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
                        if (imageUri != null) {
                            uploadBitmap(imageSelected, response.getJSONObject("userdata").getString("_id"));
                        }
                        Toasty.info(getApplicationContext(), "BENVENUTO!", Toast.LENGTH_LONG).show();

                        JSONObject user = response.getJSONObject("userdata");
                        user.put("session", true);
                        signInAction(user);

                    } else {
                        if ((response.getJSONObject("message").getJSONObject("keyPattern").has("email")) &&
                                (response.getJSONObject("message").getJSONObject("keyPattern").getInt("email") == 1)) {
                            Toasty.error(getApplicationContext(), "EMAIL ESISTENTE!", Toast.LENGTH_LONG).show();
                        } else if ((response.getJSONObject("message").getJSONObject("keyPattern").has("username")) &&
                                (response.getJSONObject("message").getJSONObject("keyPattern").getInt("username") == 1)) {
                            Toasty.error(getApplicationContext(), "USERNAME ESISTENTE!", Toast.LENGTH_LONG).show();
                        }
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

    private boolean checkField() {

        String name = this.name.getText().toString();
        String surname = this.surname.getText().toString();
        String username = this.username.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String confirmPassword = this.confirmPassword.getText().toString();

        if ((name.isEmpty()) || (surname.isEmpty()) || (username.isEmpty()) || (email.isEmpty()) || (password.isEmpty()) || (confirmPassword.isEmpty())) {
            Toasty.error(getApplicationContext(), "COMPILARE TUTTI I CAMPI!", Toast.LENGTH_LONG).show();
            return false;
        } else if (username.length() < 3) {
            Toasty.error(getApplicationContext(), "USERNAME TROPPO CORTO!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(email.contains("@")) || (!(email.contains(".")))) {
            Toasty.error(getApplicationContext(), "INSERIRE UNA EMAIL VALIDA!", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.length() < 9) {
            Toasty.error(getApplicationContext(), "PASSWORD TROPPO CORTA!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!(password.equals(confirmPassword))) {
            Toasty.error(getApplicationContext(), "INSERIRE UNA PASSWORD CORRETTA!", Toast.LENGTH_LONG).show();
            return false;
        } else return true;

    }

    public void onClickLogin(View view) {
        Intent goToLoginIntent = new Intent(this, LoginActivity.class);
        this.startActivity(goToLoginIntent);
        this.finish();
    }

    public void goToHome(JSONObject userInfo) {
        Intent goToHomeIntent = new Intent(this, MapsHomeActivity.class);
        goToHomeIntent.putExtra("user", userInfo.toString());
        this.startActivity(goToHomeIntent);
        this.finish();
    }

    private void signInAction(JSONObject user) {


        this.goToHome(user);
    }


    private void uploadBitmap(final Bitmap image, String UserID) {

        Bitmap reducedBitmap = ImageResizer.reduceBitmapSize(image,240000);
        byte[] redusedFile = getBitmapFile(reducedBitmap);


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        setUserSession(UserID);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_id", UserID);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", redusedFile));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(RegistrationActivity.this).add(volleyMultipartRequest);

    }
    public void setUserSession(String userId) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject(
                    "{\"_id\":\"" + userId + "\" }");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/user/getuserbyid";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject user = response.getJSONObject("user");
                    SharedPreferences sessionPreferences = getSharedPreferences("session", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sessionPreferences.edit();
                    user.put("session", true);
                    editor.putString("UserLogged", user.toString());
                    editor.apply();

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


    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void onClickBackground(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }


    private byte[] getBitmapFile(Bitmap reducedBitmap) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "reduced_file");


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        reducedBitmap.compress(Bitmap.CompressFormat.PNG,0, bos);
        byte[] bitmapdata = bos.toByteArray();

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return bitmapdata;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmapdata;

    }

}