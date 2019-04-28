package org.example.oah.mymapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ServiceListAdapter extends ArrayAdapter<Service> {
    private static final String TAG = "ServiceListAdapter";

    private Context mContext;
    int mResource;

    public ServiceListAdapter(Context context, int resource, ArrayList<Service> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        Double price = getItem(position).getPrice();

        Service service = new Service(name, price);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView servicename = convertView.findViewById(R.id.service_name);
        TextView servicePrice = convertView.findViewById(R.id.service_price);

        servicename.setText(name);
        servicePrice.setText(price.toString());

        return convertView;
    }
}
