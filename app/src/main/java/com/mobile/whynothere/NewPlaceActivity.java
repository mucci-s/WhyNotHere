package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mobile.whynothere.utility.adapters.DefaultImageAdaptor;
import com.mobile.whynothere.utility.adapters.ImageAdaptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Keys for storing activity state.
    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String TAG = NewPlaceActivity.class.getSimpleName();
    private int CURSOR_IMAGES = 0;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int GALLERY_REQUEST = 9;
    //Initialize variable
    GoogleMap googleMap;
    private CameraPosition cameraPosition;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    private Location lastKnownLocation;

    private Button viewPlace;
    private Button addPhoto;
    private Button cancelButton;
    private Button confirmButton;
    private LinearLayout formLayout;
    private EditText title;
    private EditText description;
    GridView gridView;

    private String userLogged;
    private String userID;
    private String placeID = "602c58e747c1bc00046f55b0";
    ArrayList<Integer> defaultImages = new ArrayList<>(Arrays.asList(
            R.drawable.default_icon_place, R.drawable.default_icon_place, R.drawable.default_icon_place, R.drawable.default_icon_place
    ));

    List<Bitmap> images = new ArrayList<>(Arrays.<Bitmap>asList());
    List<Uri> uriImages = new ArrayList<>(Arrays.<Uri>asList());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        userLogged = getIntent().getStringExtra("userLogged");

        try {
            JSONObject userObject = new JSONObject(userLogged);
            userID = userObject.getString("_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_newplace);

        gridView = findViewById(R.id.imageGrid);
        setDefaultImages(gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogBox(position);
            }
        });

        addPhoto = this.findViewById(R.id.button_add_photo);
        cancelButton = this.findViewById(R.id.cancelPlaceButtonID);
        confirmButton = this.findViewById(R.id.confirmPlaceButtonID);
        formLayout = this.findViewById(R.id.formLayout);
        title = this.findViewById(R.id.listAuthorCommentID);
        description = this.findViewById(R.id.listCommentID);

        viewPlace = this.findViewById(R.id.button);
        formLayout.setOnClickListener(null);

        addPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewPlaceActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewPlaceActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                    return;
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);


    }

    public void onClickViewPlace(View view) {
        Intent goToViewPlace = new Intent(this, ViewPlaceActivity.class);
        goToViewPlace.putExtra("placeId",placeID);
        this.startActivity(goToViewPlace);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }



    public void setDefaultImages(GridView gridView) {
        DefaultImageAdaptor defaultImageAdaptor = new DefaultImageAdaptor(defaultImages, this);
        gridView.setAdapter(defaultImageAdaptor);
        gridView.setEnabled(false);
    }

    public void showDialogBox(final int image_pos) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        ImageView Image = dialog.findViewById(R.id.img);
        Button btn_full = dialog.findViewById(R.id.btn_full);
        Button btn_close = dialog.findViewById(R.id.btn_close);

        Image.setImageBitmap(images.get(image_pos));

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        dialog.show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Il tuo GPS sembra disabilitato, vuoi attivarlo?")
                .setCancelable(false)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(googleMap.MAP_TYPE_HYBRID);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        // checkGpsEnabled();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            getDeviceLocation();
        }
        //Check permission
        if (ActivityCompat.checkSelfPermission(NewPlaceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission grated
            //Call method
            updateLocationUI();

            getDeviceLocation();
        } else {
            //When permission denied
            //Request permission
            ActivityCompat.requestPermissions(NewPlaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getDeviceLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationResult = client.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        MarkerOptions options = new MarkerOptions().position(latLng)
                                .title("Sono qui");
                        googleMap.addMarker(options);
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.d(TAG, "Exception: %s", task.getException());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                }
            }
        });
    }

    //Open phone gallery
    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            ClipData clipData = data.getClipData();

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {

                    uriImages.add(clipData.getItemAt(i).getUri());

                    Toast.makeText(getApplicationContext(), "uri imagine  " + clipData.getItemAt(i).getUri(), Toast.LENGTH_LONG).show();
                    try {
                        InputStream is = getContentResolver().openInputStream(uriImages.get(i));
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        images.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            ImageAdaptor imageAdaptor = new ImageAdaptor(images, this);
            gridView.setAdapter(imageAdaptor);
            gridView.setEnabled(true);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When permission granted
                //Call method
                updateLocationUI();
            }
        }
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    public void onClickAddPlace(View view) {

        final Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                insertPlace();
            }
        }, 500);

        title.getText().clear();
        description.getText().clear();

    }


    public void insertPlace() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonBody = null;

        try {

            jsonBody = new JSONObject(
                    "{\"title\":\"" + title.getText().toString() + "\"," +
                            "\"author\":\"" + userID + "\"," +
                            "\"name\":\"" + description.getText().toString() + "\"," +
                            "\"location\": {\"coordinates\": [ " + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude() + "], \"type\": \"Point\"} }");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/createpost";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Aggiunto con successo!", Toast.LENGTH_LONG).show();
                Intent goToHome = new Intent(getBaseContext(),MapsHomeActivity.class);
                startActivity(goToHome);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERRORE!" + error, Toast.LENGTH_LONG).show();

            }

        });
        requestQueue.add(jsonObjectRequest);
    }


}