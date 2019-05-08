package org.example.oah.mymapp.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Calendar;

public class Salon implements Serializable {

    private static final String TAG = "Salon";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String id;
    public String name;
    public double locLat;
    public double locLang;
    public String phoneNumber;
    public String maleAverage;
    public String femaleAverage;
    public String createdUser;
    public String description;

    public Salon(String name, String description, double locLat,
                 double locLang, String phoneNumber, String maleAverage,
                 String femaleAverage, String createdUser) {

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.locLat = locLat;
        this.locLang = locLang;
        this.phoneNumber = phoneNumber;
        this.maleAverage = maleAverage;
        this.femaleAverage = femaleAverage;
        this.createdUser = createdUser;
    }

    public Salon(String id, String name, String description, double locLat,
                 double locLang, String phoneNumber, String maleAverage,
                 String femaleAverage) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.locLat = locLat;
        this.locLang = locLang;
        this.phoneNumber = phoneNumber;
        this.maleAverage = maleAverage;
        this.femaleAverage = femaleAverage;
    }

    public void create() {
        Date currentTime = Calendar.getInstance().getTime();
        Log.d(TAG, "Salon save: " + this.name);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Salons");
        mDatabase.child(this.id).setValue(this);

        Map<String, Object> saveSalon = new HashMap<>();
        saveSalon.put("name", this.name);
        saveSalon.put("description", this.description);
        saveSalon.put("locLat", this.locLat);
        saveSalon.put("locLang", this.locLang);
        saveSalon.put("phoneNumber", this.phoneNumber);
        saveSalon.put("maleAverage", this.maleAverage);
        saveSalon.put("femaleAverage", this.femaleAverage);
        saveSalon.put("createdUser", this.createdUser);
        saveSalon.put("createDate", currentTime);

        db.collection("Salons")
                .document(this.id)
                .set(saveSalon);
    }

//    public Salon(String id) {
//        db.collection("Salons")
//                .document(id)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot doc = task.getResult();
//                            Salon a = new Salon(
//                                    doc.get("id").toString(),
//                                    doc.get("name").toString(),
//                                    doc.get("description").toString(),
//                                    (Double) doc.get("locLat"),
//                                    (Double) doc.get("locLang"),
//                                    doc.get("phoneNumber").toString(),
//                                    doc.get("maleAverage").toString(),
//                                    doc.get("femaleAverage").toString(),
//                                    doc.get("createdUser").toString());
//
//                            Log.d(TAG, "Kanaliha" + a.toString());
//
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//        });
//    }


    @Override
    public String toString() {
        return "Salon{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locLat=" + locLat +
                ", locLang=" + locLang +
                ", createdUser='" + createdUser + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getMarkerData() {
        String info = "";

        if (this.femaleAverage != null) {
            info += "Female avg: " +  this.femaleAverage + " € ";
        }

        if (this.maleAverage != null) {
            info += "Male avg: " +  this.maleAverage + " € ";
        }

        info += "Rating: 5/4";

        return info;
    }
}
