package org.example.oah.mymapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SalonsListFragment extends Fragment {

    private static final String TAG = "SalonsListFragment";

//    private ArrayList<String> salonNames = new ArrayList<>();
//    private ArrayList<String> salonDescription = new ArrayList<>();
//    private ArrayList<String> salonId = new ArrayList<>();

    private ArrayList<Salon> salonsList = new ArrayList<>();

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: called");

        view = inflater.inflate(R.layout.content_salons_list, container, false);

        getUserSalonsData(new FirebaseCallback() {
            @Override
//            public void onCallback(List<String> names, List<String> desciption) {
            public void onCallback(List<Salon> salons) {
//                Log.d(TAG, "onCallback: " + names.toString());
//                Log.d(TAG, "onCallback: " + desciption.toString());
//                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(salonNames, salonDescription);

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Salons");

        myRef.orderByChild("createdUser").equalTo("b7UoljcSmsdk2ca1oJ7bOCkiM4u2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id, name, description, phoneNumber, maleAverage, femaleAverage, createdUser;
                double locLang, locLat;

                for(DataSnapshot child : dataSnapshot.getChildren() ){
                    // Do magic here

//                    Log.d(TAG, "Salong: " + child.child("name").getValue());
//                    Log.d(TAG, "Salong: " + child.child("description").getValue());
//
//                    if (child.child("name").getValue() != null) {
//                        salonNames.add(child.child("name").getValue().toString());
//                    } else {
//                        salonNames.add("-");
//                    }
//
//                    if (child.child("description").getValue() != null) {
//                        salonDescription.add(child.child("description").getValue().toString());
//                    } else {
//                        salonDescription.add("-");
//                    }

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


                    salonsList.add(new Salon(id, name, description,
                            locLat, locLang, phoneNumber,
                            maleAverage, femaleAverage, createdUser
                    ));

                }

     //           firebaseCallback.onCallback(salonNames, salonDescription);

                firebaseCallback.onCallback(salonsList);
             //   Log.d(TAG, "onDataChange: " + salonNames.toString());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private interface FirebaseCallback {
//        void onCallback(List<String> names, List<String> desciption);

        void onCallback(List<Salon> salons);
    }


}
