package org.example.oah.mymapp.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.Review;
import org.example.oah.mymapp.model.Salon;

import java.util.ArrayList;
import java.util.List;

public class SalonsListFragment extends Fragment {

    private static final String TAG = "SalonsListFragment";

    private ArrayList<Salon> salonsList = new ArrayList<>();

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: called");

        view = inflater.inflate(R.layout.content_salons_list, container, false);

        getUserSalonsData(new FirebaseCallback() {
            @Override
            public void onCallback(List<Salon> salons) {
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(salons);
                RecyclerView recyclerView = view.findViewById(R.id.salons_recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        });


        return view;

    }

    private void getUserSalonsData(final FirebaseCallback firebaseCallback) {
        Log.d(TAG, "getUserSalonsData: called");


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Salons")
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

                                Salon salon = new Salon(document.getId(), name,
                                        lat, lan, phoneNumber, maleAverage, femaleAverage,
                                        createdUser, description, email, homePage
                                );

                                salonsList.add(salon);
                                firebaseCallback.onCallback(salonsList);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private interface FirebaseCallback {

        void onCallback(List<Salon> salons);
    }


}
