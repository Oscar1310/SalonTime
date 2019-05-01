package org.example.oah.mymapp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.Salon;
import org.example.oah.mymapp.utli.PermissionUtils;

public class AddEditSalon extends AppCompatActivity implements
        OnMapReadyCallback,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener {

    private EditText salonName;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static final String TAG = "AddEditSalon";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_salon);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        setTitle("Add salon");


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_edit_map);
        mapFragment.getMapAsync(this);


        Button saveBtn = findViewById(R.id.add_edit_save_btn);
        salonName = findViewById(R.id.create_salon_name);

        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: save called");
                Log.d(TAG, "name: " + salonName.getText().toString());
                Log.d(TAG, "Lat: " + mMap.getCameraPosition().target.latitude);
                Log.d(TAG, "Long: " + mMap.getCameraPosition().target.longitude);

                Double salonLat = mMap.getCameraPosition().target.latitude;
                Double salonLon = mMap.getCameraPosition().target.longitude;
                String name = salonName.getText().toString();
                String createdUser = currentUser.getUid();


//                Salon salon = new Salon(name, salonLat, salonLon, createdUser);
//                salon.save();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.436375,24.756952),15));

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

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

}
