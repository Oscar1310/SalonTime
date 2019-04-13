package org.example.oah.mymapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

 //   private Context context;
//    private ArrayList<String> salonId;
//    private ArrayList<String> salonNames;
//    private ArrayList<String> salonDescriptions;

//    public RecyclerViewAdapter(ArrayList<String> salonNames, ArrayList<String> salonDescriptions) {
//        Log.d(TAG, "RecyclerViewAdapter: called " + salonNames.size());
//
// //       this.context = context;
//        this.salonNames = salonNames;
//        this.salonDescriptions = salonDescriptions;
//    }

    private List<Salon> salons;

    public RecyclerViewAdapter(List<Salon> salons) {
        Log.d(TAG, "RecyclerViewAdapter: called " + salons.size());

        //       this.context = context;
        this.salons = salons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.salon_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        Log.d(TAG, "onBindViewHolder: called");

        viewHolder.name.setText(salons.get(i).name);
        viewHolder.description.setText(salons.get(i).description);

        final Salon selectedSalon = salons.get(i);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: starts" + selectedSalon.name);

                Bundle arguments = new Bundle();
                arguments.putSerializable(Salon.class.getSimpleName(), selectedSalon);

                SalonViewFragment salonViewFragment = new SalonViewFragment();
                salonViewFragment.setArguments(arguments);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.user_fragment_container, salonViewFragment)
                        .commit();

//                getSupportFragmentManager()
//                        .setArguments(arguments)
//                        .beginTransaction()
//                        .replace(R.id.user_fragment_container, new SalonViewFragment())
//                        .commit();


            }
        };

        View.OnLongClickListener longClickListener =new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick starts");
                return false;
            }
        };

        viewHolder.salonListLayout.setOnClickListener(clickListener);
        viewHolder.salonListLayout.setOnLongClickListener(longClickListener);

    }

    @Override
    public int getItemCount() {
        return salons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout salonListLayout;
        TextView name;
        TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.salonListLayout = itemView.findViewById(R.id.salon_listview_layout);
            this.name = itemView.findViewById(R.id.salonlist_name);
            this.description = itemView.findViewById(R.id.salonlist_description);
        }
    }

}
