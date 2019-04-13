package org.example.oah.mymapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class SalonViewFragment extends Fragment
    implements OnMapReadyCallback {

    private static final String TAG = "SalonViewFragment";

    private MapView mapView;
    private Bundle arguments;
    private Salon salon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.salon_view, container, false);

        salon = (Salon) getArguments().getSerializable(Salon.class.getSimpleName());

        TextView heading = view.findViewById(R.id.salon_view_heading);
        heading.setText(salon.name);
        TextView description = view.findViewById(R.id.salon_view_description);
        description.setText(salon.description);
        TextView phone = view.findViewById(R.id.salon_view_phone);
        phone.setText(salon.phoneNumber);

        TextView femalePrice = view.findViewById(R.id.salon_view_female_price);
        femalePrice.setText(salon.femaleAverage + "€");
        TextView menPrice = view.findViewById(R.id.salon_view_men_price);
        menPrice.setText(salon.maleAverage + "€");

        Log.d(TAG, "onCreateView: " +  salon.toString());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(salon.locLat, salon.locLang);
        googleMap.addMarker(new MarkerOptions().position(latLng)
                .title(salon.name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }
}
