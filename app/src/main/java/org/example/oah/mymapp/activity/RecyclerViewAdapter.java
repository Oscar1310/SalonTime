package org.example.oah.mymapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.Salon;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";


    private List<Salon> salons;

    public RecyclerViewAdapter(List<Salon> salons) {
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

        viewHolder.name.setText(salons.get(i).name);
        viewHolder.description.setText(salons.get(i).description);

        final Salon selectedSalon = salons.get(i);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle arguments = new Bundle();
                arguments.putSerializable(Salon.class.getSimpleName(), selectedSalon);

                SalonViewFragment salonViewFragment = new SalonViewFragment();
                salonViewFragment.setArguments(arguments);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.user_fragment_container, salonViewFragment)
                        .commit();


            }
        };

        View.OnLongClickListener longClickListener =new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
