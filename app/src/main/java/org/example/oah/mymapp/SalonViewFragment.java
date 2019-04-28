package org.example.oah.mymapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

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

        TextView femalePrice = view.findViewById(R.id.salon_view_men_price);
        femalePrice.setText(salon.femaleAverage + "€");
        TextView menPrice = view.findViewById(R.id.salon_view_men_price);
        menPrice.setText(salon.maleAverage + "€");


        Service service1 = new Service("MEN’S hair toning", 11);
        Service service2 = new Service("WOMAN’S hair roots colouring", 35);
        Service service3 = new Service("WOMAN’S hair colouring and/or highlights ", 30);
       // Service service4 = new Service("WOMAN’S hair colouring and haircut for half-long hai", 45);

        ArrayList<Service> serviceArrayList = new ArrayList<>();

        serviceArrayList.add(service1);
        serviceArrayList.add(service2);
        serviceArrayList.add(service3);

        ServiceListAdapter serviceAdapter = new ServiceListAdapter(getActivity(), R.layout.services_list_item, serviceArrayList);
        ListView serviceList = view.findViewById(R.id.salon_services_list);
        serviceList.setAdapter(serviceAdapter);

        Review review1 = new Review("The russian girl. Oh my, shave with knife....risky. joking i fell asleep. Great work", 3);
        Review review2 = new Review("Great hairdressers, nice service and friendly people. Also there's a foosball table and some soft drinks are included in the price.", 4);
        Review review3 = new Review("Excellent male barbershop with beautiful hairdressers", 5);
        // Service service4 = new Service("WOMAN’S hair colouring and haircut for half-long hai", 45);

        ArrayList<Review> reviewArrayList = new ArrayList<>();

        reviewArrayList.add(review1);
        reviewArrayList.add(review2);
        reviewArrayList.add(review3);

        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(getActivity(), R.layout.review_list_item, reviewArrayList);
        ListView reviewList = view.findViewById(R.id.salon_reviews_list);
        reviewList.setAdapter(reviewListAdapter);


        Log.d(TAG, "onCreateView: " +  salon.toString());

        ImageView editBtn = view.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onClick: edti salon");
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.user_fragment_container, new AddEditSalonFragment())
//                        .commit();
            }
        });


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
