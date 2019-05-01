package org.example.oah.mymapp.model;

import android.graphics.Picture;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.example.oah.mymapp.model.Salon;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;

    private String snippet;
    private int iconPicture;
    private Salon salon;

    public ClusterMarker(LatLng position, String title, String snippet, int iconPicture, Salon salon) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.salon = salon;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public Salon getSalon() {
        return salon;
    }
}
