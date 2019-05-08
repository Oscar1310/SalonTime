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
//                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Salon salon = new Salon(document.getId(), document.get("name").toString(), document.get("description").toString(),
                                        (double) document.get("locLat"), (double) document.get("locLang"), document.get("phoneNumber").toString(),
                                        document.get("maleAverage").toString(), document.get("femaleAverage").toString()
                                );

                                salonsList.add(salon);
                                firebaseCallback.onCallback(salonsList);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Salons");
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        myRef.orderByChild("createdUser").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String id, name, description, phoneNumber, maleAverage, femaleAverage, createdUser;
//                double locLang, locLat;
//
//                for(DataSnapshot child : dataSnapshot.getChildren() ){
//                    // Do magic here
//
////                    Log.d(TAG, "Salong: " + child.child("name").getValue());
////                    Log.d(TAG, "Salong: " + child.child("description").getValue());
////
////                    if (child.child("name").getValue() != null) {
////                        salonNames.add(child.child("name").getValue().toString());
////                    } else {
////                        salonNames.add("-");
////                    }
////
////                    if (child.child("description").getValue() != null) {
////                        salonDescription.add(child.child("description").getValue().toString());
////                    } else {
////                        salonDescription.add("-");
////                    }
//
//                    if (child.child("createdUser").getValue() != null) {
//                        id = child.child("createdUser").getValue().toString();
//                    } else {
//                        id = "";
//                    }
//
//                    if (child.child("name").getValue() != null) {
//                        name = child.child("name").getValue().toString();
//                    } else {
//                        name = "";
//                    }
//
//                    if (child.child("description").getValue() != null) {
//                        description = child.child("description").getValue().toString();
//                    } else {
//                        description = "";
//                    }
//
//                    if (child.child("locLat").getValue() != null) {
//                        locLat = Double.parseDouble(child.child("locLat").getValue().toString());
//                    } else {
//                        locLat = 0.00;
//                    }
//
//                    if (child.child("locLang").getValue() != null) {
//                        locLang = Double.parseDouble(child.child("locLang").getValue().toString());
//                    } else {
//                        locLang = 0.00;
//                    }
//
//                    if (child.child("phoneNumber").getValue() != null) {
//                        phoneNumber = child.child("phoneNumber").getValue().toString();
//                    } else {
//                        phoneNumber = "";
//                    }
//
//                    if (child.child("maleAverage").getValue() != null) {
//                        maleAverage = child.child("maleAverage").getValue().toString();
//                    } else {
//                        maleAverage = "";
//                    }
//
//                    if (child.child("femaleAverage").getValue() != null) {
//                        femaleAverage = child.child("femaleAverage").getValue().toString();
//                    } else {
//                        femaleAverage = "";
//                    }
//
//                    if (child.child("createdUser").getValue() != null) {
//                        createdUser = child.child("createdUser").getValue().toString();
//                    } else {
//                        createdUser = "";
//                    }
//
//
//                    salonsList.add(new Salon(id, name, description,
//                            locLat, locLang, phoneNumber,
//                            maleAverage, femaleAverage
//                    ));
//
//                }
//
//     //           firebaseCallback.onCallback(salonNames, salonDescription);
//
//                firebaseCallback.onCallback(salonsList);
             //   Log.d(TAG, "onDataChange: " + salonNames.toString());
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });

    }

    private interface FirebaseCallback {
//        void onCallback(List<String> names, List<String> desciption);

        void onCallback(List<Salon> salons);
    }


}
