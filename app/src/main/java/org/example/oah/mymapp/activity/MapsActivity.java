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
import org.example.oah.mymapp.utli.PermissionUtils;

import java.util.ArrayList;


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


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Salons")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Salon salon = new Salon(document.getId(), document.get("name").toString(), document.get("description").toString(),
                                        (double) document.get("locLat"), (double) document.get("locLang"), document.get("phoneNumber").toString(),
                                        document.get("maleAverage").toString(), document.get("femaleAverage").toString()
                                );

                                LatLng latLng = new LatLng(salon.locLat, salon.locLang);
                                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                                        .title(salon.name)
                                        .snippet(salon.getMarkerData())
                                );

                                marker.setTag(salon);
                                Log.d(TAG, document.getId() + " => " + salon.toString());

                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()  {
                                    @Override
                                    public void onInfoWindowClick(Marker marker)  {
                                        Log.d(TAG, "onInfoWindowClick: called " + marker.getTag().toString());

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

                                        FirebaseFirestore dbQuery = FirebaseFirestore.getInstance();

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


//                                        Review review1 = new Review("The russian girl. Oh my, shave with knife....risky. joking i fell asleep. Great work", 3);
//                                        Review review2 = new Review("Great hairdressers, nice service and friendly people. Also there's a foosball table and some soft drinks are included in the price.", 4);
//                                        Review review3 = new Review("Excellent male barbershop with beautiful hairdressers", 5);
//
//                                        ArrayList<Review> reviewArrayList = new ArrayList<>();
//
//                                        reviewArrayList.add(review1);
//                                        reviewArrayList.add(review2);
//                                        reviewArrayList.add(review3);
//
//                                        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(MapsActivity.this, R.layout.review_list_item, reviewArrayList);
//                                        ListView reviewList = dialog.findViewById(R.id.salon_reviews_list);
//                                        reviewList.setAdapter(reviewListAdapter);


                                    }
                                });
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
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