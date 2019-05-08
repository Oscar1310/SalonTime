package org.example.oah.mymapp.model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Review implements Serializable {

    private static final String TAG = "Review";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String comment, salondID;
    private int rating;

    public Review(String comment, int rating, String salondID) {
        this.comment = comment;
        this.rating = rating;
        this.salondID = salondID;
    }

    public Review(String comment, int rating) {
        this.comment = comment;
        this.rating = rating;
    }


    public void create() {
        Date currentTime = Calendar.getInstance().getTime();
        Map<String, Object> review = new HashMap<>();
        review.put("comment", this.comment);
        review.put("rating", this.rating);
        review.put("salonId", this.salondID);
        review.put("createUserId", "6WwIHAwcn1MAmIF385ZCE4dx2m33");
        review.put("createDate", currentTime);

        db.collection("Reviews")
                .add(review);
    }


    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
