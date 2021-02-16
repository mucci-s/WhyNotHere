package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mobile.whynothere.models.PlaceDataInfoModel;
import com.mobile.whynothere.utility.adapters.CustomNearlyPlaceAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsHomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    View mapView;
    PlacesClient placesClient;
    ImageButton searchButton;
    BottomNavigationView navigationBar;
    ListView listViewNearlyPlace;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private ConstraintLayout bottomSheetLayoutPlaceInfo;

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetBehavior bottomSheetBehaviorPlaceInfo;
    boolean locationPermissionGranted;
    ConstraintLayout bottomSheetLayout;

    private final static String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final String TAG = MapsHomeActivity.class.getSimpleName();
    private String userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        userLogged = getIntent().getStringExtra("user");

        String apiKey = getString(R.string.api_key);
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        // Retrieve location and camera position from saved instance state.


        //      updateValuesFromBundle(savedInstanceState);

        setContentView(R.layout.activity_maps_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        searchButton = (ImageButton) findViewById(R.id.searchButton);
        navigationBar = (BottomNavigationView) findViewById(R.id.navigation_bar);


        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        bottomSheetLayoutPlaceInfo = findViewById(R.id.bottom_place_info);
        bottomSheetBehaviorPlaceInfo = BottomSheetBehavior.from(bottomSheetLayoutPlaceInfo);

        listViewNearlyPlace = (ListView) bottomSheetLayout.findViewById(R.id.listview);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;

                    case R.id.profile:

                        goToUserProfile();
                        return true;

                    case R.id.addplace:

                        Intent goToUserProfileIntent = new Intent(MapsHomeActivity.this, NewPlaceActivity.class);
                        goToUserProfileIntent.putExtra("userLogged", userLogged);
                        startActivity(goToUserProfileIntent);

                        return true;
                }
                return false;
            }
        });

        navigationBar.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                    case R.id.profile:
                    case R.id.addplace:
                }
            }
        });

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mapView = mapFragment.getView();

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
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (mMap.getCameraPosition().zoom < 15) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                        }
                    }, 500);


                } else {

                    TextView title = bottomSheetLayoutPlaceInfo.findViewById(R.id.title_place_info);
                    TextView address = bottomSheetLayoutPlaceInfo.findViewById(R.id.address_place_info);
                    TextView coordinate = bottomSheetLayoutPlaceInfo.findViewById(R.id.coordinates_places_info);

                    PlaceDataInfoModel placeInfo = (PlaceDataInfoModel) marker.getTag();
                    assert placeInfo != null;

                    title.setText(placeInfo.getTitle());
                    coordinate.setText(placeInfo.getPosition().toString().substring(8));

                    if (getGeoLocate(placeInfo.getPosition()) == null) {
                        address.setText("Non c'è indirizzo per questo luogo");
                    } else {
                        address.setText(getGeoLocate(placeInfo.getPosition()).getAddressLine(0));
                    }

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehaviorPlaceInfo.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                return false;
            }
        });

        this.loadPostsLocation();

        //  mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //zoomToUserLocation();
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }

//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//
//            SettingsClient settingsClient = LocationServices.getSettingsClient(MapsFineActivity.this);
//            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
//
//            task.addOnSuccessListener(MapsFineActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
//                @Override
//                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                }
//            });
//
//            task.addOnFailureListener(MapsFineActivity.this, new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    if (e instanceof ResolvableApiException) {
//                        ResolvableApiException resolvable = (ResolvableApiException) e;
//                        try {
//                            resolvable.startResolutionForResult(MapsFineActivity.this, 51);
//                        } catch (IntentSender.SendIntentException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            });


        }


        View compassButton = mapView.findViewWithTag("GoogleMapCompass");//to access the compass button
        RelativeLayout.LayoutParams rlp1 = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
        rlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp1.setMargins(0, 180, 0, 0);


        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        locationButton.setBackgroundColor(255);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 25, 0, 25);

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
        }
    };


    @Override
    protected void onResume() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bottomSheetBehaviorPlaceInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        super.onResume();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        } else {
            buildAlertMessageNoGps();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationBar.setSelectedItemId(R.id.home);
        setNearlyPlaces();
    }

    private void enableUserLocation() {
        this.checkLocationPermission();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void zoomToUserLocation() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLocationPermission();
                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                });
            }
        }, 1000);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(MapsHomeActivity.this, "Permission needed", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void onSearchCalled(View view) {

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                //  mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                Log.i(TAG, "Place autocomplete prediction: " + place.getName() + ", " + place.getLatLng());
                Log.i(TAG, "Place complete: " + place);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                assert status.getStatusMessage() != null;
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onClickNearlyButton(View view) {

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorPlaceInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private Address getGeoLocate(LatLng latLng) {
        Geocoder geocoder = new Geocoder(MapsHomeActivity.this);
        List<Address> locationList = new ArrayList<>();
        try {
            locationList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Log.e("TAG", "geolocate: IOException: " + e.getMessage());
        }

        if (locationList.size() > 0) {
            return locationList.get(0);
        }
        return null;

    }


    private void setNearlyPlaces() {
        this.checkLocationPermission();
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            loadTopNearlyPost(location);
                        }
                    }
                });
    }


    private void loadTopNearlyPost(Location userLocation) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        double lat = userLocation.getLatitude();
        double lng = userLocation.getLongitude();

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(
                    "{\"lat\":" + lat + "," +
                            "\"lng\":" + lng + "}");

            System.out.println(jsonBody.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/topnearlyplaces";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int i = 0;

                List<PlaceDataInfoModel> nearlyPlacesInfo = new ArrayList<PlaceDataInfoModel>();
                PlaceDataInfoModel placeInfo;
                try {
                    if (response.getJSONArray("post").length() > 0) {
                        JSONArray lightObject = response.getJSONArray("post");

                        while (i < response.getJSONArray("post").length()) {
                            JSONObject post = lightObject.getJSONObject(i);
                            JSONObject location = post.getJSONObject("location");
                            double lat = location.getJSONArray("coordinates").getDouble(0);
                            double lng = location.getJSONArray("coordinates").getDouble(1);
                            placeInfo = new PlaceDataInfoModel(
                                    post.getString("_id"),
                                    new LatLng(lat, lng),
                                    post.getString("title"),
                                    post.getString("description"),
                                    post.getInt("stars"));
                            placeInfo.setDistance(post.getDouble("distance"));
                            nearlyPlacesInfo.add(placeInfo);
                            i++;
                        }
                        CustomNearlyPlaceAdapter adapterCustom = new CustomNearlyPlaceAdapter(MapsHomeActivity.this, nearlyPlacesInfo);
                        listViewNearlyPlace.setAdapter(adapterCustom);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    } else {
                        Toast.makeText(getApplicationContext(), "non ci sono luoghi nelle vicinanze", Toast.LENGTH_LONG).show();
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


    private void loadPostsLocation() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final String url = "https://whynothere-app.herokuapp.com/post/getposts";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int i = 0;
                PlaceDataInfoModel placeData;

                try {

                    if (response.getJSONArray("posts").length() > 0) {
                        JSONArray lightObject = response.getJSONArray("posts");
                        while (i < response.getJSONArray("posts").length()) {
                            JSONObject post = lightObject.getJSONObject(i);
                            JSONObject location = post.getJSONObject("location");
                            double lat = location.getJSONArray("coordinates").getDouble(0);
                            double lng = location.getJSONArray("coordinates").getDouble(1);
                            placeData = new PlaceDataInfoModel(
                                    post.getString("_id"),
                                    new LatLng(lat, lng),
                                    post.getString("title"),
                                    post.getString("description"),
                                    post.getInt("stars")
                            );
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lng)));
                            marker.setTag(placeData);
                            i++;
                        }

                    } else {
                        Log.i(TAG, "There are no post to add");
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


    public void goToUserProfile(){
        Intent goToUserProfileIntent = new Intent(this, UserProfileActivity.class);
        goToUserProfileIntent.putExtra("userLogged", userLogged);
        this.startActivity(goToUserProfileIntent);
    }


}


//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        if (savedInstanceState == null) {
//            return;
//        }
//        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//            requestingLocationUpdates = savedInstanceState.getBoolean(
//                    REQUESTING_LOCATION_UPDATES_KEY);
//        }
//    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }
//
//    private void stopLocationUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }
//
//
//    protected void onResume() {
//        super.onResume();
//        if (requestingLocationUpdates) {
//            startLocationUpdates();
//        }
//    }
//
//    private void startLocationUpdates() {
//        checkLocationPermission();
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
//                locationCallback,
//                Looper.getMainLooper());
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        stopLocationUpdates();
//    }


/*

    private void init(){



        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER ){
                    geoLocate();
                }
                return false;
            }
        });
    }


    private void geoLocate(){
        String serachLocation = searchBar.getText().toString();

        Geocoder geocoder = new Geocoder(MapsFineActivity.this);
        List<Address> locationList = new ArrayList<>();
        try{
            locationList = geocoder.getFromLocationName(serachLocation,1);
        }catch (IOException e){
            Log.e("TAG", "geolocate: IOException: " + e.getMessage() );
        }

        if(locationList.size() > 0){
            Address location = locationList.get(0);
            LatLng location_latlng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location_latlng, 14));
            mMap.addMarker(new MarkerOptions().position(location_latlng));
            Log.d("ADDRESS", "location found: " + location.toString());

        }
    }


*/
