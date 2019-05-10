package org.example.oah.mymapp.model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserFavoriteSalons {
    private String id;
    private String salonId;
    private String userId;

    public UserFavoriteSalons(String salonId, String userId) {
        this.salonId = salonId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getSalonId() {
        return salonId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
     public void create() {
         Date currentTime = Calendar.getInstance().getTime();

         Map<String, Object> favorite = new HashMap<>();
         favorite.put("salonId", this.salonId);
         favorite.put("userId", this.userId);
         favorite.put("createDate", currentTime);

         FirebaseFirestore db = FirebaseFirestore.getInstance();

         db.collection("UserFavoriteSalons")
                 .add(favorite);

     }
}
