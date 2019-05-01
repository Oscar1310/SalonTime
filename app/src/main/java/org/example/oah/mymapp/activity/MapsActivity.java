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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.ClusterMarker;
import org.example.oah.mymapp.model.Review;
import org.example.oah.mymapp.model.Salon;
import org.example.oah.mymapp.model.Service;
import org.example.oah.mymapp.utli.MyClusterManagerRenderer;
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
//    private ClusterManager mClusterManager;
//    private MyClusterManagerRenderer mMyClusterManagerRenderer;
//    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

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
                            case R.id.add_salone:
                                Log.d(TAG, "onOptionsItemSelected: add salon");

                                if (currentUser == null) {
                                    Toast.makeText(MapsActivity.this, "For adding salon you need to login", Toast.LENGTH_SHORT).show();
                                    return true;
                                }

                                intent = new Intent(MapsActivity.this, AddEditSalon.class);
                                startActivity(intent);
                                break;

                            case R.id.signin_btn:
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
//        Log.d(TAG, "onMapReady: " + mClusterManager);
        enableMyLocation();

        // 59.436375  24.756952 viru

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.436375,24.756952),15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Salons");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    String id, name, description, phoneNumber, maleAverage, femaleAverage, createdUser;
                    double locLang, locLat;

                for(DataSnapshot child : dataSnapshot.getChildren() ){
                    // Do magic here

                    if (child.child("createdUser").getValue() != null) {
                        id = child.child("createdUser").getValue().toString();
                    } else {
                        id = "";
                    }

                    if (child.child("name").getValue() != null) {
                        name = child.child("name").getValue().toString();
                    } else {
                        name = "";
                    }

                    if (child.child("description").getValue() != null) {
                        description = child.child("description").getValue().toString();
                    } else {
                        description = "";
                    }

                    if (child.child("locLat").getValue() != null) {
                        locLat = Double.parseDouble(child.child("locLat").getValue().toString());
                    } else {
                        locLat = 0.00;
                    }

                    if (child.child("locLang").getValue() != null) {
                        locLang = Double.parseDouble(child.child("locLang").getValue().toString());
                    } else {
                        locLang = 0.00;
                    }

                    if (child.child("phoneNumber").getValue() != null) {
                        phoneNumber = child.child("phoneNumber").getValue().toString();
                    } else {
                        phoneNumber = "";
                    }

                    if (child.child("maleAverage").getValue() != null) {
                        maleAverage = child.child("maleAverage").getValue().toString();
                    } else {
                        maleAverage = "";
                    }

                    if (child.child("femaleAverage").getValue() != null) {
                        femaleAverage = child.child("femaleAverage").getValue().toString();
                    } else {
                        femaleAverage = "";
                    }

                    if (child.child("createdUser").getValue() != null) {
                        createdUser = child.child("createdUser").getValue().toString();
                    } else {
                        createdUser = "";
                    }


                    Salon salon = new Salon(id, name, description,
                            locLat, locLang, phoneNumber,
                            maleAverage, femaleAverage, createdUser
                    );

                    Log.d(TAG, "onDataChange: " + salon.toString());

                    LatLng latLng = new LatLng(salon.locLat, salon.locLang);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(child.child("name").getValue().toString())
                            .snippet("test")
                    );
                    marker.setTag(salon);

                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()  {
                    @Override
                    public void onInfoWindowClick(Marker marker)  {
                        Log.d(TAG, "onInfoWindowClick: called " + marker.getTag().toString());

                        Salon salon = (Salon) marker.getTag();

                        Dialog dialog = new Dialog(MapsActivity.this);
                        dialog.setContentView(R.layout.salon_view);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.show();

                        TextView heading = dialog.findViewById(R.id.salon_view_heading);
                        heading.setText(salon.name);
                        TextView description = dialog.findViewById(R.id.salon_view_description);
                        description.setText(salon.description);
                        TextView phone = dialog.findViewById(R.id.salon_view_phone);
                        phone.setText(salon.phoneNumber);

                        TextView femalePrice = dialog.findViewById(R.id.salon_view_men_price);
                        femalePrice.setText(salon.femaleAverage + "€");
                        TextView menPrice = dialog.findViewById(R.id.salon_view_men_price);
                        menPrice.setText(salon.maleAverage + "€");


                        Service service1 = new Service("MEN’S hair toning", 11);
                        Service service2 = new Service("WOMAN’S hair roots colouring", 35);
                        Service service3 = new Service("WOMAN’S hair colouring and/or highlights ", 30);
                        // Service service4 = new Service("WOMAN’S hair colouring and haircut for half-long hai", 45);

                        ArrayList<Service> serviceArrayList = new ArrayList<>();

                        serviceArrayList.add(service1);
                        serviceArrayList.add(service2);
                        serviceArrayList.add(service3);

                        ServiceListAdapter serviceAdapter = new ServiceListAdapter(MapsActivity.this, R.layout.services_list_item, serviceArrayList);
                        ListView serviceList = dialog.findViewById(R.id.salon_services_list);
                        serviceList.setAdapter(serviceAdapter);

                        Review review1 = new Review("The russian girl. Oh my, shave with knife....risky. joking i fell asleep. Great work", 3);
                        Review review2 = new Review("Great hairdressers, nice service and friendly people. Also there's a foosball table and some soft drinks are included in the price.", 4);
                        Review review3 = new Review("Excellent male barbershop with beautiful hairdressers", 5);
                        // Service service4 = new Service("WOMAN’S hair colouring and haircut for half-long hai", 45);

                        ArrayList<Review> reviewArrayList = new ArrayList<>();

                        reviewArrayList.add(review1);
                        reviewArrayList.add(review2);
                        reviewArrayList.add(review3);

                        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(MapsActivity.this, R.layout.review_list_item, reviewArrayList);
                        ListView reviewList = dialog.findViewById(R.id.salon_reviews_list);
                        reviewList.setAdapter(reviewListAdapter);


                    }
                });

            }
            

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        
        

//        addMapCustomMarkers();

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
    


//    public void addMapCustomMarkers() {
//        if (mMap != null) {
//
//            if (mClusterManager == null) {
//                mClusterManager = new ClusterManager<ClusterMarker>(this.getApplicationContext(), mMap);
//            }
//            if (mMyClusterManagerRenderer == null) {
//                mMyClusterManagerRenderer = new MyClusterManagerRenderer(
//                  this,
//                        mMap,
//                        mClusterManager
//                );
//                mClusterManager.setRenderer(mMyClusterManagerRenderer);
//            }
//
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("Salons");
//
//            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String id, name, description, phoneNumber, maleAverage, femaleAverage, createdUser;
//                    double locLang, locLat;
//
//                    for(DataSnapshot child : dataSnapshot.getChildren() ){
//                        // Do magic here
//
//                        Log.d(TAG, "Salong: " + child.child("name").getValue());
//                        Log.d(TAG, "Lat: " + child.child("locLat").getValue(Double.class));
//                        Log.d(TAG, "Long: " + child.child("locLang").getValue(Double.class));
//
//                        if (child.child("createdUser").getValue() != null) {
//                            id = child.child("createdUser").getValue().toString();
//                        } else {
//                            id = "";
//                        }
//
//                        if (child.child("name").getValue() != null) {
//                            name = child.child("name").getValue().toString();
//                        } else {
//                            name = "";
//                        }
//
//                        if (child.child("description").getValue() != null) {
//                            description = child.child("description").getValue().toString();
//                        } else {
//                            description = "";
//                        }
//
//                        if (child.child("locLat").getValue() != null) {
//                            locLat = Double.parseDouble(child.child("locLat").getValue().toString());
//                        } else {
//                            locLat = 0.00;
//                        }
//
//                        if (child.child("locLang").getValue() != null) {
//                            locLang = Double.parseDouble(child.child("locLang").getValue().toString());
//                        } else {
//                            locLang = 0.00;
//                        }
//
//                        if (child.child("phoneNumber").getValue() != null) {
//                            phoneNumber = child.child("phoneNumber").getValue().toString();
//                        } else {
//                            phoneNumber = "";
//                        }
//
//                        if (child.child("maleAverage").getValue() != null) {
//                            maleAverage = child.child("maleAverage").getValue().toString();
//                        } else {
//                            maleAverage = "";
//                        }
//
//                        if (child.child("femaleAverage").getValue() != null) {
//                            femaleAverage = child.child("femaleAverage").getValue().toString();
//                        } else {
//                            femaleAverage = "";
//                        }
//
//                        if (child.child("createdUser").getValue() != null) {
//                            createdUser = child.child("createdUser").getValue().toString();
//                        } else {
//                            createdUser = "";
//                        }
//
//
//                        Salon salon = new Salon(id, name, description,
//                                locLat, locLang, phoneNumber,
//                                maleAverage, femaleAverage, createdUser
//                        );
//
//                        int avatar = R.drawable.ic_salon;
//
//                        String snippet = "Favg: " + salon.femaleAverage + ", Mavg: " + salon.maleAverage + ", Rating: 5";
//
//                        ClusterMarker clusterMarker = new ClusterMarker(
//                                new LatLng(salon.locLat, salon.locLang),
//                                salon.name,
//                                snippet,
//                                avatar,
//                                salon
//                        );
//
//                        mClusterManager.addItem(clusterMarker);
//                        mClusterMarkers.add(clusterMarker);
//
//
//                    }
//
//                    mMap.setOnInfoWindowClickListener(mClusterManager);
//                    mClusterManager.cluster();
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    Log.w(TAG, "Failed to read value.", error.toException());
//                }
//            });
//
//        }
//    }

}