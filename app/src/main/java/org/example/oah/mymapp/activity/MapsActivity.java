package org.example.oah.mymapp.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.Review;
import org.example.oah.mymapp.model.Salon;
import org.example.oah.mymapp.model.Service;
import org.example.oah.mymapp.model.UserFavoriteSalons;
import org.example.oah.mymapp.utli.PermissionUtils;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    private static final String TAG = "MapsActivity";

    private DrawerLayout drawerLayout;
    private Intent intent;
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private RatingBar salonReiting;
    private TextView salonComment;

    private Salon markerSalon;

    private ListView serviceList, reviewList;
    private Dialog rateSalonDialog;

    private ArrayList<Review> reviewArrayList;

    private ImageView favorite_salon_btn;
    private boolean isFavorite;
    private FirebaseFirestore dbQuery = FirebaseFirestore.getInstance();

    private RatingBar ratingBar ;
    private TextView salon_reviews_count;



    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        drawerLayout = findViewById(R.id.maps_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        Log.d(TAG, "onNavigationItemSelected: called");

                        drawerLayout.closeDrawers();

                        Log.d(TAG, "onNavigationItemSelected: touched: " + menuItem.getItemId());

                        switch (menuItem.getItemId()) {

                            case R.id.profile_btn:
                                Log.d(TAG, "onOptionsItemSelected: log in");
                                if (currentUser == null) {
                                    intent = new Intent(MapsActivity.this, LoginActivity.class);
                                } else {
                                    intent = new Intent(MapsActivity.this, UserActivity.class);
                                }
                                startActivity(intent);
                                break;

                        }

                        return true;
                    }
                });

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        // 59.436375  24.756952 viru

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.436375,24.756952),15));
        mMap.getUiSettings().setZoomControlsEnabled(true);


        dbQuery.collection("Salons")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String name = (document.get("name")==null ? "" : document.get("name").toString());
                                double lat = (document.get("locLat")==null ? 0 : (double) document.get("locLat"));
                                double lan = (document.get("locLang")==null ? 0 : (double) document.get("locLang"));
                                String phoneNumber = (document.get("phoneNumber")==null ? "" : document.get("phoneNumber").toString());
                                String maleAverage = (document.get("maleAverage")==null ? "" : document.get("maleAverage").toString());
                                String femaleAverage = (document.get("femaleAverage")==null ? "" : document.get("femaleAverage").toString());
                                String createdUser = (document.get("createdUser")==null ? "" : document.get("createdUser").toString());
                                String description = (document.get("description")==null ? "" : document.get("description").toString());
                                String email = (document.get("email")==null ? "" : document.get("email").toString());
                                String homePage = (document.get("homePage")==null ? "" : document.get("homePage").toString());

                                final Salon salon = new Salon(document.getId(), name,
                                        lat, lan, phoneNumber, maleAverage, femaleAverage,
                                        createdUser, description, email, homePage
                                );

                                dbQuery.collection("Reviews")
                                        .whereEqualTo("salonId", salon.id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    long sum = 0;
                                                    int reiting = 0;
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document.get("rating")!=null) {
                                                            Log.d(TAG, "onComplete: " + document.get("rating").getClass().getName());
                                                            sum += (long) document.get("rating");
                                                        }
                                                    }

                                                    if (task.getResult().size()!=0) {

                                                        Log.d(TAG, "SUM: " + sum);
                                                        Log.d(TAG, "Count: " + task.getResult().size());

                                                        reiting = (int) Math.round((double) sum / (double) task.getResult().size() * 2 / 2.0);
                                                    }

                                                    String markerInfoField = "Female avg: " +  salon.femaleAverage + " € Male avg: " + salon.maleAverage + " € Raiting: 5/" + reiting;



                                                    LatLng latLng = new LatLng(salon.locLat, salon.locLang);
                                                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                                                            .title(salon.name)
                                                            .snippet(markerInfoField)
                                                    );

                                                    marker.setTag(salon);

                                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()  {
                                                    @Override
                                                    public void onInfoWindowClick(Marker marker)  {
                                                        Log.d(TAG, "onInfoWindowClick: called " + marker.getTag().toString());
                                                        isFavorite = false;

                                                        markerSalon = (Salon) marker.getTag();

                                                        Dialog dialog = new Dialog(MapsActivity.this);
                                                        dialog.setContentView(R.layout.salon_simple_view);
                                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                                        dialog.show();

                                                        TextView heading = dialog.findViewById(R.id.salon_view_heading);
                                                        heading.setText(markerSalon.name);
                                                        TextView description = dialog.findViewById(R.id.salon_view_description);
                                                        description.setText(markerSalon.description);
                                                        TextView phone = dialog.findViewById(R.id.salon_view_phone);
                                                        phone.setText(markerSalon.phoneNumber);

                                                        TextView femalePrice = dialog.findViewById(R.id.salon_view_female_price);
                                                        femalePrice.setText(markerSalon.femaleAverage + "€");
                                                        TextView menPrice = dialog.findViewById(R.id.salon_view_men_price);
                                                        menPrice.setText(markerSalon.maleAverage + "€");

                                                        serviceList = dialog.findViewById(R.id.salon_services_list);
                                                        reviewList = dialog.findViewById(R.id.salon_reviews_list);

                                                        ratingBar = dialog.findViewById(R.id.ratingBar);
                                                        salon_reviews_count = dialog.findViewById(R.id.salon_reviews_count);


                                                        dbQuery.collection("Reviews")
                                                                .whereEqualTo("salonId", markerSalon.id)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            long sum = 0;
                                                                            float reiting = 0;
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                if (document.get("rating") != null) {
                                                                                    Log.d(TAG, "onComplete: " + document.get("rating").getClass().getName());
                                                                                    sum += (long) document.get("rating");
                                                                                }
                                                                            }

                                                                            if (task.getResult().size() != 0) {

                                                                                Log.d(TAG, "SUM: " + sum);
                                                                                Log.d(TAG, "Count: " + task.getResult().size());

                                                                                reiting = (float) Math.round((double) sum / (double) task.getResult().size() * 2 / 2.0);

                                                                                salon_reviews_count.setText(task.getResult().size() + " Reviews");
                                                                            }
                                                                            ratingBar.setRating(reiting);
                                                                        }
                                                                    }
                                                                });

                                                        dbQuery.collection("Services")
                                                                .whereEqualTo("salonId",markerSalon.id)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            ArrayList<Service> serviceArrayList = new ArrayList<>();
                                                                            for (QueryDocumentSnapshot service : task.getResult()) {
                                                                                Log.d(TAG, service.getId() + " => " + service.getData());
                                                                                serviceArrayList.add(new Service(service.get("name").toString(), Double.parseDouble(service.get("price").toString())));
                                                                            }
                                                                            ServiceListAdapter serviceAdapter = new ServiceListAdapter(MapsActivity.this, R.layout.services_list_item, serviceArrayList);
                                                                            serviceList.setAdapter(serviceAdapter);
                                                                            ViewGroup.LayoutParams lp = serviceList.getLayoutParams();
                                                                            lp.height = serviceArrayList.size() * 80;
                                                                            serviceList.setLayoutParams(lp);

                                                                        }
                                                                    }
                                                                });

                                                        dbQuery.collection("Reviews")
                                                                .whereEqualTo("salonId", markerSalon.id)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            reviewArrayList = new ArrayList<>();
                                                                            for (QueryDocumentSnapshot review : task.getResult()) {
                                                                                Log.d(TAG, review.getId() + " => " + review.getData());
                                                                                reviewArrayList.add(new Review(review.get("comment").toString(), Integer.parseInt(review.get("rating").toString())));
                                                                            }
                                                                            ReviewListAdapter reviewListAdapter = new ReviewListAdapter(MapsActivity.this, R.layout.review_list_item, reviewArrayList);
                                                                            reviewList.setAdapter(reviewListAdapter);
                                                                            ViewGroup.LayoutParams lp = reviewList.getLayoutParams();
                                                                            lp.height = reviewArrayList.size() * 140;
                                                                            reviewList.setLayoutParams(lp);

                                                                        }
                                                                    }
                                                                });


                                                        ImageView add_comment = dialog.findViewById(R.id.add_comment);
                                                        add_comment.setOnClickListener(new View.OnClickListener(){
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (currentUser == null) {
                                                                    //intent = new Intent(MapsActivity.this, LoginActivity.class);
                                                                    Toast.makeText(MapsActivity.this, "Please log in to rate salon", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    rateSalonDialog = new Dialog(MapsActivity.this);
                                                                    rateSalonDialog.setContentView(R.layout.rate_salon_view);
                                                                    rateSalonDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                                                    rateSalonDialog.show();

                                                                    salonReiting = rateSalonDialog.findViewById(R.id.salon_rating);
                                                                    salonComment = rateSalonDialog.findViewById(R.id.salon_comment);

                                                                    Button rate_salon_btn = rateSalonDialog.findViewById(R.id.rate_salon_btn);
                                                                    rate_salon_btn.setOnClickListener(new View.OnClickListener(){
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Review review = new Review(salonComment.getText().toString(),
                                                                                    (int) salonReiting.getRating(),
                                                                                    markerSalon.id,
                                                                                    currentUser.getUid());

                                                                            review.create();

                                                                            reviewArrayList.add(0, review);
                                                                            ReviewListAdapter reviewListAdapter = new ReviewListAdapter(MapsActivity.this, R.layout.review_list_item, reviewArrayList);
                                                                            reviewList.setAdapter(reviewListAdapter);
                                                                            ViewGroup.LayoutParams lp = reviewList.getLayoutParams();
                                                                            lp.height = reviewArrayList.size() * 140;
                                                                            reviewList.setLayoutParams(lp);

                                                                            rateSalonDialog.cancel();

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });


                                                        favorite_salon_btn = dialog.findViewById(R.id.favorite_salon_btn);
                                                        if (currentUser != null) {

                                                            dbQuery.collection("UserFavoriteSalons")
                                                                    .whereEqualTo("salonId", markerSalon.id)
                                                                    .whereEqualTo("userId", currentUser.getUid())
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                if (task.getResult().size()>0) {
                                                                                    favorite_salon_btn.setImageResource(R.drawable.ic_bookmark);
                                                                                    isFavorite = true;
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                        favorite_salon_btn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (isFavorite) {
                                                                    deleteFavorite();
                                                                    isFavorite = false;
                                                                    favorite_salon_btn.setImageResource(R.drawable.ic_bookmark_empty);
                                                                    Toast.makeText(MapsActivity.this, "Removed favorite",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                                else if (currentUser != null) {
                                                                    Log.d(TAG, "onClick: add favorite");
                                                                    UserFavoriteSalons userFavoriteSalons = new UserFavoriteSalons(markerSalon.id, currentUser.getUid());
                                                                    userFavoriteSalons.create();
                                                                    favorite_salon_btn.setImageResource(R.drawable.ic_bookmark);
                                                                    isFavorite = true;
                                                                    Toast.makeText(MapsActivity.this, "Add favorite",
                                                                            Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Log.d(TAG, "onClick: not logged id");
                                                                    Toast.makeText(MapsActivity.this, "Cant add to favorite, you need to log in.",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void deleteFavorite() {
        dbQuery.collection("UserFavoriteSalons")
                .whereEqualTo("salonId", markerSalon.id)
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList <String> deleteFavorites = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                deleteFavorites.add(document.getId());
                            }

                            for (String deleteId : deleteFavorites) {
                                dbQuery.collection("UserFavoriteSalons")
                                        .document(deleteId)
                                        .delete();
                            }

                        }
                    }
                });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: called");
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_nav_bar:
                Log.d(TAG, "onOptionsItemSelected: show nab bar");
                drawerLayout.openDrawer(GravityCompat.END);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

}