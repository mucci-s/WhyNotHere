package com.mobile.whynothere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
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
import com.mobile.whynothere.models.Comment;
import com.mobile.whynothere.utility.adapters.CustomListAdapter;
import com.mobile.whynothere.utility.adapters.DefaultImageAdaptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private EditText addCommentEditText;
    private ImageButton addCommentButton;
    GridView gridView;

    private CircularImageView imageAuthor, userLoggedImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewplace);

        getUserId();

        this.imageAuthor = findViewById(R.id.placeAuthorProfileAvatarID);
        this.title = findViewById(R.id.placeTitleID);
        this.description = findViewById(R.id.placeDescriptionID);
        this.author = findViewById(R.id.placeAuthorID);
        this.userLoggedImage = findViewById(R.id.avatarUserLogged);

        this.addCommentEditText = findViewById(R.id.addCommentID);
        setFotoDavide();

        gridView = findViewById(R.id.imageGrid);
        setDefaultImages(gridView);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //   showDialogBox(position);
            }
        });
/*
        List<Comment> comments = getListData();
        ListView listView = findViewById(R.id.lista);
        listView.setAdapter(new CustomListAdapter(comments, this));
*/
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

    @Override
    protected void onStart() {
        super.onStart();
        placeID = getIntent().getStringExtra("placeId");
        getPlace(placeID);
    }

    public void onClickAddComment(View view) {

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    insertComment();

                }
            }, 500);

            //getComment();

    }

    private void setFotoDavide() {
        URL url = null;
        URL url2 = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            url = new URL("https://instagram.fcia2-1.fna.fbcdn.net/v/t51.2885-19/s320x320/119672999_449071479377091_6700639640968533977_n.jpg?_nc_ht=instagram.fcia2-1.fna.fbcdn.net&_nc_ohc=1soJCsVgJKkAX-Qg5-9&tp=1&oh=dc6741a9184ea11651c695b5954e7a28&oe=60567A7B");
            url2 = new URL("https://instagram.fcia2-1.fna.fbcdn.net/v/t51.2885-19/s320x320/121218074_1083846702059572_8582352636631822576_n.jpg?_nc_ht=instagram.fcia2-1.fna.fbcdn.net&_nc_ohc=_hGUpK2WXBYAX9BjkbD&tp=1&oh=2640a217b45b82e273b299aa21120358&oe=6055645A");

            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageAuthor.setImageBitmap(bmp);

            Bitmap bmp1 = BitmapFactory.decodeStream(url2.openConnection().getInputStream());
            userLoggedImage.setImageBitmap(bmp1);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertComment(){
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

                addCommentEditText.getText().clear();
                Toast.makeText(getApplicationContext(), "Aggiunto con successo!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERRORE!" + error, Toast.LENGTH_LONG).show();
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
                    JSONObject place = response.getJSONObject("post");

                    titlePlace = place.getString("title");
                    descriptionPlace = place.getString("description");
                    authorID = place.getString("author");

                    placeLatLng = new LatLng(place.getJSONObject("location").getJSONArray("coordinates").getDouble(0),
                            place.getJSONObject("location").getJSONArray("coordinates").getDouble(1));

                    getPlaceLocation();
                    title.setText(titlePlace);
                    description.setText(descriptionPlace);
                    getAuthor(authorID);

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
                    nameAuthor = user.getString("name");
                    author.setText(nameAuthor);
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


    private List<Comment> getListData() {
        List<Comment> list = new ArrayList<Comment>();
        Comment prova1 = new Comment("Peppino", "Bellissimo posto Bellissimo posto BELLO BELLO BELLO BELLO BELLO BELLO BELLO BELLO", R.drawable.avatar_icon);
        Comment prova2 = new Comment("Carlo", "Stancante arrivarci.", R.drawable.avatar_icon);
        Comment prova3 = new Comment("Antonio", "Consiglio, da ritornarci.", R.drawable.avatar_icon);
        Comment prova4 = new Comment("Antonino", "Consiglio, da ritornarciTOPTTOPTOTP.", R.drawable.avatar_icon);
        Comment prova5 = new Comment("Peppe", "Consiglio, da ritornarci.", R.drawable.avatar_icon);
        Comment prova6 = new Comment("Peppe", "Consiglio, da ritornarci.", R.drawable.avatar_icon);
        Comment prova7 = new Comment("Peppe", "Consiglio, da ritornarci.", R.drawable.avatar_icon);
        Comment prova8 = new Comment("Peppe", "Consiglio, da ritornarci.", R.drawable.avatar_icon);

        list.add(prova1);
        list.add(prova2);
        list.add(prova3);
        list.add(prova4);
        list.add(prova5);
        list.add(prova6);
        list.add(prova7);
        list.add(prova8);
        return list;
    }


    public void setDefaultImages(GridView gridView) {
        DefaultImageAdaptor defaultImageAdaptor = new DefaultImageAdaptor(defaultImages, this);
        gridView.setAdapter(defaultImageAdaptor);
        gridView.setEnabled(false);
    }

    /*
    public void showDialogBox(final int image_pos){
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
                Intent i = new Intent(NewPlaceActivity.this, FullViewImage.class);
                i.putExtra("img_id",images.get(image_pos));
                startActivity(i);
            }
        });

        dialog.show();
    }*/

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

    private void getComment(){



    }
}
