package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mobile.whynothere.utility.ImageResizer;
import com.mobile.whynothere.utility.VolleyMultipartRequest;
import com.mobile.whynothere.utility.adapters.CustomRecyclerAdaptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import es.dmoral.toasty.Toasty;


public class NewPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Keys for storing activity state.
    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String TAG = NewPlaceActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 1;
    private int CURSOR_IMAGES = 0;
    private static final String UPLOAD_URL = "https://whynothere-app.herokuapp.com/post/uploadpostimage";

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
    private ImageButton confirmButton;
    private EditText title;
    private EditText description;

    private String userID;

    private RecyclerView imageRecycler;
    private Marker positionMarker;
    private ConstraintLayout imageLayout;
    private ProgressBar progressBar;
    private LatLng newPositionMarker;

    List<Bitmap> images = new ArrayList<>(Arrays.<Bitmap>asList());
    List<Uri> uriImages = new ArrayList<>(Arrays.<Uri>asList());
    List<byte[]> fileListImage = new ArrayList<>(Arrays.<byte[]>asList());


    @Override
    protected void onStart() {
        super.onStart();
        imageLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID = getUserId();
        setContentView(R.layout.activity_newplace);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        title = findViewById(R.id.titleNewPlaceID);
        description = findViewById(R.id.descriptionNewPlaceID);

        imageRecycler = findViewById(R.id.list1);
        addPhoto = findViewById(R.id.button_add_photo);


        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecycler.setLayoutManager(manager1);
        confirmButton = findViewById(R.id.confirmPlaceButtonID);
        progressBar = findViewById(R.id.progressBar);
        imageLayout = findViewById(R.id.imagesLayoutID);


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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
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

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                newPositionMarker = positionMarker.getPosition();
            }
        });
    }

    private void getDeviceLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                .draggable(true)
                                .title("Sono qui");
                        positionMarker = googleMap.addMarker(options);
                        newPositionMarker = positionMarker.getPosition();
                       // googleMap.addMarker(options);
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.d(TAG, "Exception: %s", task.getException());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && (data.getClipData() != null || data.getData() != null)) {

            ClipData clipData = data.getClipData();
            images = new ArrayList<>(Arrays.<Bitmap>asList());
            CustomRecyclerAdaptor imageAdaptor = new CustomRecyclerAdaptor(this, data);
            imageRecycler.setAdapter(imageAdaptor);

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    uriImages.add(clipData.getItemAt(i).getUri());
                    try {
                        InputStream is = getContentResolver().openInputStream(uriImages.get(i));
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Bitmap reducedBitmap = ImageResizer.reduceBitmapSize(bitmap,240000);
                        byte[] redusedFile = getBitmapFile(reducedBitmap);
                        fileListImage.add(redusedFile);
                        images.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (data.getData() != null) {
                try {
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Bitmap reducedBitmap = ImageResizer.reduceBitmapSize(bitmap,240000);
                    byte[] redusedFile = getBitmapFile(reducedBitmap);
                    fileListImage.add(redusedFile);
                    images.add(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocationUI();
            }
        }
    }

    private void updateLocationUI() {
        if (googleMap == null) {
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    public void onClickAddPlace(View view) {
        if (checkField()) {
            insertPlace();
        }
    }

    public void insertPlace() {
        imageLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject(
                    "{\"title\":\"" + title.getText().toString() + "\"," +
                            "\"author\":\"" + userID + "\"," +
                            "\"description\":\"" + description.getText().toString() + "\"," +
                            "\"location\": {\"coordinates\": [ " + newPositionMarker.latitude + "," + newPositionMarker.longitude + "], \"type\": \"Point\"} }");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/createpost";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    uploadBitmap(fileListImage, fileListImage.size(), response.getString("_id"));
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

    public String getUserId() {
        SharedPreferences userPreferences = getSharedPreferences("session", MODE_PRIVATE);
        try {
            JSONObject userLogged = new JSONObject(userPreferences.getString("UserLogged", ""));
            return userLogged.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadBitmap(final List<byte[]> data, final int items, String newpostID) {
//        Uri picUri = null;
//        CustomRecyclerAdaptor adaptor1 = new CustomRecyclerAdaptor(getApplicationContext(), data);
//        imageRecycler.setAdapter(adaptor1);
//        if (data.getClipData() != null) {
//            picUri = data.getClipData().getItemAt(items - 1).getUri();
//        } else if (data.getData() != null) {
//            picUri = data.getData();
//        }
//        filePath = getPath(picUri);
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
//            images.add(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        //    Toast.makeText(getApplicationContext(),"Dentro respos" + itemCount, Toast.LENGTH_LONG).show();
                        //   if (data.getClipData() != null) {
                        if (items > 1) {
                            uploadBitmap(data, items - 1, newpostID);
                        } else {
                            //mainCategoryRecycler.setAdapter(new CategoryItemRecyclerAdapter(getApplicationContext(),images));
                            title.getText().clear();
                            description.getText().clear();
                            Toasty.success(getApplicationContext(), "AGGIUNTO CON SUCCESSO!", Toast.LENGTH_LONG).show();
                            Intent goToHome = new Intent(getBaseContext(), MapsHomeActivity.class);
                            startActivity(goToHome);
                        }
//                        } else {
//
//                            Toast.makeText(getApplicationContext(), "Dentro respos solo un immagine", Toast.LENGTH_LONG).show();
//                        }
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
                params.put("_id", newpostID);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", data.get(items - 1)));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(NewPlaceActivity.this).add(volleyMultipartRequest);

    }


    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private boolean checkField() {

        if ((this.title.getText().toString().isEmpty()) || (this.description.getText().toString().isEmpty()) || (this.images.size() == 0)) {
            Toasty.error(getApplicationContext(), "COMPILARE TUTTI I CAMPI!", Toast.LENGTH_LONG).show();
            return false;
        } else if ((title.length() < 3) || (title.length() > 50)) {
            Toasty.error(getApplicationContext(), "INSERIRE UN TITOLO VALIDO!", Toast.LENGTH_LONG).show();
            return false;
        } else if (description.length() > 150) {
            Toasty.error(getApplicationContext(), "DESCRIZIONE TROPPO LUNGA!", Toast.LENGTH_LONG).show();
            return false;
        } else return true;

    }

}