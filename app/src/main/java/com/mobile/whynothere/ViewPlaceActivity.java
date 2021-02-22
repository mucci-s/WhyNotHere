package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobile.whynothere.utility.GetImageFromUrl;
import com.mobile.whynothere.utility.adapters.CustomListAdapter;
import com.mobile.whynothere.utility.adapters.CustomRecyclerViewPlaceAdaptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;

public class ViewPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {


    ArrayList<Integer> defaultImages = new ArrayList<>(Arrays.asList(
            R.drawable.default_icon_place, R.drawable.default_icon_place, R.drawable.default_icon_place, R.drawable.default_icon_place, R.drawable.default_icon_place, R.drawable.default_icon_place, R.drawable.default_icon_place
    ));

    // List<Bitmap> images = new ArrayList<>(Arrays.<Bitmap>asList());

    // Keys for storing activity state.
    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String TAG = NewPlaceActivity.class.getSimpleName();
    private int CURSOR_IMAGES = 0;


    private static final int GALLERY_REQUEST = 9;
    //Initialize variable
    GoogleMap googleMap;
    private CameraPosition cameraPosition;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    private JSONObject place;
    private LatLng placeLatLng;
    private Location placeLocation;
    private LinearLayout formLayout;
    private TextView author;
    private TextView title;
    private TextView description;

    private String placeID, userId;
    private String titlePlace;
    private String descriptionPlace;
    private String authorID;
    private String nameAuthor;
    private ImageButton favoriteIcon;
    private String userPhoto;
    private String userEmail;


    private EditText addCommentEditText;
    private ImageView addCommentButton;
    GridView gridView;
    ListView listCommentView;
    private CircularImageView imageAuthor;
    private CircularImageView userLoggedImage;
    private RecyclerView imageRecycler;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewplace);



        this.listCommentView = findViewById(R.id.lista);
        this.imageAuthor = findViewById(R.id.placeAuthorProfileAvatarID);
        this.title = findViewById(R.id.placeTitleID);
        this.description = findViewById(R.id.placeDescriptionID);
        this.author = findViewById(R.id.placeAuthorID);
        this.userLoggedImage = findViewById(R.id.avatarUserLogged2);
        this.favoriteIcon = findViewById(R.id.favoriteiconID);
        this.imageRecycler = findViewById(R.id.list1);
        this.addCommentButton = findViewById(R.id.addCommentButtonID);
        this.addCommentEditText = findViewById(R.id.addCommentID);


        this.userLoggedImage.setImageResource(R.drawable.ic_profile);
        getUserId();


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);


        listCommentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomListAdapter customListAdapter = (CustomListAdapter) parent.getAdapter();
                Intent goToProfile = new Intent(ViewPlaceActivity.this, UserProfileActivity.class);
                goToProfile.putExtra("userId", customListAdapter.getIdAuthor(position));
                startActivity(goToProfile);
            }
        });

        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavoritePost(userId,placeID);
                favoriteIcon.setImageDrawable(getDrawable(R.drawable.ic_favorite_red));
            }
        });

        imageAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPlaceActivity.this, UserProfileActivity.class);
                try {
                    intent.putExtra("userId",place.getString("author"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        //addCommentButton.setBackgroundResource(R.drawable.custom_insert_comment);

        addCommentButton.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    addCommentButton.setImageResource(R.drawable.ic_baseline_add_comment_focused);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    addCommentButton.setImageResource(R.drawable.ic_baseline_add_comment);

                return false;
            }
        });

    }

    public void getUserId() {
        SharedPreferences userPreferences = getSharedPreferences("session", MODE_PRIVATE);
        try {
            JSONObject userLogged = new JSONObject(userPreferences.getString("UserLogged", ""));
            this.userId = userLogged.getString("_id");
            this.userPhoto = userLogged.getString("photo_profile");
            this.userEmail = userLogged.getString("email");
            this.userLoggedImage.setImageResource(R.drawable.progress_bar);
            new GetImageFromUrl(this.userLoggedImage, getApplicationContext()).execute(userLogged.getString("photo_profile"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        placeID = getIntent().getStringExtra("placeId");
        getPlace(placeID);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecycler.setLayoutManager(manager1);

       checkFavorite(userEmail,placeID);

    }

    public void onClickAddComment(View view) {
        if (checkComment()) {
            insertComment();

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        }

    }

    private boolean checkComment() {
        if (this.addCommentEditText.getText().toString().length() == 0) {
            Toasty.error(getApplicationContext(), "INSERIRE UN COMMENTO VALIDO!", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }

    private void insertComment() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject(
                    "{\"_id\":\"" + placeID + "\"," +
                            "\"description\":\"" + addCommentEditText.getText().toString() + "\"," +
                            "\"vote_post\":\"" + DEFAULT_ZOOM + "\"," +
                            "\"user_id\":\"" + userId + "\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/addcomment";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    listCommentView.setAdapter(new CustomListAdapter(response.getJSONArray("comments"), getApplicationContext()));
                    listCommentView.post(new Runnable() {
                        @Override
                        public void run() {
                            listCommentView.setSelection(listCommentView.getCount() - 1);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addCommentEditText.getText().clear();
                Toasty.success(getApplicationContext(), "AGGIUNTO CON SUCCESSO!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toasty.error(getApplicationContext(), "ERRORE!" + error, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getPlace(String placeID) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"_id\":" + placeID + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/getpostbyid";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    place = response.getJSONObject("post");

                    titlePlace = place.getString("title");
                    descriptionPlace = place.getString("description");
                    authorID = place.getString("author");

                    placeLatLng = new LatLng(place.getJSONObject("location").getJSONArray("coordinates").getDouble(0),
                            place.getJSONObject("location").getJSONArray("coordinates").getDouble(1));

                    getPlaceLocation();
                    title.setText(titlePlace);
                    description.setText(descriptionPlace);

                    if(authorID.equalsIgnoreCase(userId)){
                        favoriteIcon.setVisibility(View.INVISIBLE);
                    }

                    CustomRecyclerViewPlaceAdaptor imageAdaptor = new CustomRecyclerViewPlaceAdaptor(ViewPlaceActivity.this,place.getJSONArray("photos"));
                    imageRecycler.setAdapter(imageAdaptor);
                    getAuthor(authorID);

                    ListView listView = findViewById(R.id.lista);
                    try {
                        listView.setAdapter(new CustomListAdapter(place.getJSONArray("comments"), ViewPlaceActivity.this));
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void getAuthor(String authorId) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"_id\":" + authorId + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url = "https://whynothere-app.herokuapp.com/user/getuserbyid";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject user = response.getJSONObject("user");
                    nameAuthor = user.getString("username");
                    author.setText(nameAuthor);
                    imageAuthor.setImageResource(R.drawable.progress_bar);
                    new GetImageFromUrl(imageAuthor, getApplicationContext()).execute(user.getString("photo_profile"));
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




    private void addFavoritePost(String userId ,String placeID) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"_id\":" + userId + " , \"favourite_post\":" + placeID + " }");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/post/addfavoritepost";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    private void checkFavorite(String email, String placeID ) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = null;

        try {
            jsonBody = new JSONObject("{\"favourites_posts\":" + placeID +",\"email\":" + email + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "https://whynothere-app.herokuapp.com/user/checkiffavourite";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getJSONArray( "result").length() > 0) {
                        if (response.getJSONArray("result").getJSONObject(0).getInt("count") == 1) {
                            favoriteIcon.setClickable(false);
                            favoriteIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_red));
                        }
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(googleMap.MAP_TYPE_HYBRID);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        updateLocationUI();
        //getPlaceLocation();
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, placeLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private void getPlaceLocation() {

        LatLng location = placeLatLng;
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
                MarkerOptions options = new MarkerOptions().position(location)
                        .title(titlePlace);

                googleMap.addMarker(options);
            }
        }, 1000);
    }

    private void getComment() {


    }

    public void gotoAuthorProfile(View view) {

        Intent goToProfile = new Intent(ViewPlaceActivity.this, UserProfileActivity.class);
        goToProfile.putExtra("userId", authorID);
        startActivity(goToProfile);
    }
}
