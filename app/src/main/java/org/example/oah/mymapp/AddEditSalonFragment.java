package org.example.oah.mymapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddEditSalonFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private static final String TAG = "AddEditSalonFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;
    private GoogleMap googleMap;
    private TextView salonName, salonDescription, salonPhoneNumber,
        salonMaleAveragPrice, femaleAveragePrice;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_salon_view, container, false);

        Button saveBtn = view.findViewById(R.id.create_salon_save_btn);
        salonName = view.findViewById(R.id.create_salon_name);
        salonDescription = view.findViewById(R.id.create_salon_description);
        salonPhoneNumber = view.findViewById(R.id.create_salon_phone);
        salonMaleAveragPrice = view.findViewById(R.id.create_salon_men_avarge_price);
        femaleAveragePrice = view.findViewById(R.id.create_salon_female_avarge_price);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: save called");

                Double salonLat = googleMap.getCameraPosition().target.latitude;
                Double salonLon = googleMap.getCameraPosition().target.longitude;
                String name = salonName.getText().toString();
                String description = salonDescription.getText().toString();
                String createdUser = currentUser.getUid();
                String phone = salonPhoneNumber.getText().toString();
                String malePrice = salonMaleAveragPrice.getText().toString();
                String femalePrice = femaleAveragePrice.getText().toString();


                Salon salon = new Salon(name, description, salonLat,
                        salonLon, phone, malePrice, femalePrice, createdUser);
                Log.d(TAG, "save salon: " + salon.toString());

                salon.save();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.create_salon_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.436375, 24.756952), 15));

        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

}