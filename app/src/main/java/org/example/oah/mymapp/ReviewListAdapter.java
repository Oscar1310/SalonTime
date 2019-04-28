package org.example.oah.mymapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<Review> {
    private static final String TAG = "ReviewListAdapter";

    private Context mContext;
    int mResource;

    public ReviewListAdapter(Context context, int resource, ArrayList<Review> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String comment = getItem(position).getComment();
        int raiting = getItem(position).getRating();

        Review review = new Review(comment, raiting);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView reviewComment = convertView.findViewById(R.id.reveiw_comment);
        RatingBar ratingBar = convertView.findViewById(R.id.review_raiting);

        reviewComment.setText(comment);
        ratingBar.setRating(raiting);
        return convertView;
    }
}

