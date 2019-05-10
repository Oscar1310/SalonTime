package org.example.oah.mymapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.Review;
import org.example.oah.mymapp.model.Salon;
import org.example.oah.mymapp.model.Service;

import java.util.ArrayList;

public class SalonViewFragment extends Fragment
    implements OnMapReadyCallback {

    private static final String TAG = "SalonViewFragment";

    private MapView mapView;
    private Bundle arguments;
    private Salon salon;
    private ListView serviceList, reviewList;

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

        serviceList = view.findViewById(R.id.salon_services_list);
        reviewList = view.findViewById(R.id.salon_reviews_list);


        ImageButton edit_salon_btn = view.findViewById(R.id.edit_salon_btn);

        edit_salon_btn.setVisibility(View.GONE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getUid().equals(salon.createdUser)) edit_salon_btn.setVisibility(View.VISIBLE);

        Log.d(TAG, "log in user: " + user.getUid());
        Log.d(TAG, "salon user: " + salon.createdUser);


        edit_salon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle arguments = new Bundle();
                arguments.putSerializable(Salon.class.getSimpleName(), salon);

                AddEditSalonFragment addEditSalonFragment = new AddEditSalonFragment();
                addEditSalonFragment.setArguments(arguments);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.user_fragment_container, addEditSalonFragment)
                        .commit();
            }
        });

        FirebaseFirestore dbQuery = FirebaseFirestore.getInstance();

        dbQuery.collection("Services")
                .whereEqualTo("salonId",salon.id)
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
                            ServiceListAdapter serviceAdapter = new ServiceListAdapter(getActivity(), R.layout.services_list_item, serviceArrayList);
                            serviceList.setAdapter(serviceAdapter);
                            ViewGroup.LayoutParams lp = serviceList.getLayoutParams();
                            lp.height = serviceArrayList.size() * 80;
                            serviceList.setLayoutParams(lp);

                        }
                    }
                });

        dbQuery.collection("Reviews")
                .whereEqualTo("salonId", salon.id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Review> reviewArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot review : task.getResult()) {
                                Log.d(TAG, review.getId() + " => " + review.getData());
                                reviewArrayList.add(new Review(review.get("comment").toString(), Integer.parseInt(review.get("rating").toString())));
                            }
                            ReviewListAdapter reviewListAdapter = new ReviewListAdapter(getActivity(), R.layout.review_list_item, reviewArrayList);
                            reviewList.setAdapter(reviewListAdapter);
                            ViewGroup.LayoutParams lp = reviewList.getLayoutParams();
                            lp.height = reviewArrayList.size() * 140;
                            reviewList.setLayoutParams(lp);

                        }
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
