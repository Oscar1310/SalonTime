package org.example.oah.mymapp.model;

import java.io.Serializable;

public class Review implements Serializable {

    private static final String TAG = "Review";

    private String comment;
    private int rating;

    public Review(String comment, int rating) {
        this.comment = comment;
        this.rating = rating;
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
