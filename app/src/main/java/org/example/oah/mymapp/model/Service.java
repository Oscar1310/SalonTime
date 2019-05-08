package org.example.oah.mymapp.model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Service implements Serializable {

    private static final String TAG = "Service";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String name, salonId;
    private double price;

    public Service(String name, String salonId, double price) {
        this.name = name;
        this.salonId = salonId;
        this.price = price;
    }

    public Service(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getSalondId() {
        return salonId;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalondId(String salonId) {
        this.salonId = salonId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void create() {
        Date currentTime = Calendar.getInstance().getTime();
        Map<String, Object> saveService = new HashMap<>();
        saveService.put("name", this.name);
        saveService.put("salonId", this.salonId);
        saveService.put("price", this.price);
        saveService.put("createDate", currentTime);

        db.collection("Services")
                .add(saveService);
    }
}
