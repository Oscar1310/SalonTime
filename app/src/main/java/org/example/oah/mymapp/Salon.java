package org.example.oah.mymapp;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.UUID;

public class Salon implements Serializable {

    private static final String TAG = "Salon";

    private DatabaseReference mDatabase;

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

        Log.d(TAG, "Salon: called");
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
                 String femaleAverage, String createdUser) {

        Log.d(TAG, "Salon: called");
        this.id = id;
        this.name = name;
        this.description = description;
        this.locLat = locLat;
        this.locLang = locLang;
        this.phoneNumber = phoneNumber;
        this.maleAverage = maleAverage;
        this.femaleAverage = femaleAverage;
        this.createdUser = createdUser;
    }

    public void save() {
        Log.d(TAG, "Salon save: " + this.name);
        mDatabase = FirebaseDatabase.getInstance().getReference("salons");
        mDatabase.child(this.id).setValue(this);
    }


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
}
