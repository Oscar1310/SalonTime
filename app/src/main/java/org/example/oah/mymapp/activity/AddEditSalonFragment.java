package org.example.oah.mymapp.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.Salon;
import org.example.oah.mymapp.model.Service;

import java.util.ArrayList;

public class AddEditSalonFragment extends Fragment
        implements OnMapReadyCallback {

    private static final String TAG = "AddEditSalonFragment";

    private MapView mapView, dialogMapView;
    private GoogleMap googleMap, editMap;
    private TextView salonName, salonDescription, salonPhoneNumber,
        salonMaleAveragPrice, femaleAveragePrice;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Dialog editMapDialog;


    TextView addServiceName, addServicePrice;
    ListView serviceList;

    Dialog dialog;
    ArrayList<Service> serviceArrayList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_salon_view, container, false);

        Button saveBtn = view.findViewById(R.id.create_salon_save_btn);
        salonName = view.findViewById(R.id.create_salon_name);
        salonDescription = view.findViewById(R.id.create_salon_description);
        salonPhoneNumber = view.findViewById(R.id.create_salon_phone);
        salonMaleAveragPrice = view.findViewById(R.id.create_salon_men_avarge_price);
        femaleAveragePrice = view.findViewById(R.id.create_salon_female_avarge_price);

        Service service1 = new Service("MEN’S hair toning", "3d8c431b-9e10-45ce-ab49-19e73982e3a6", 11);
        Service service2 = new Service("WOMAN’S hair roots colouring", "3d8c431b-9e10-45ce-ab49-19e73982e3a6", 35);



 //       ServiceListAdapter serviceAdapter = new ServiceListAdapter(getActivity(), R.layout.services_list_item, serviceArrayList);
        serviceList = view.findViewById(R.id.service_list);
//        serviceList.setAdapter(serviceAdapter);

        Button addService = view.findViewById(R.id.add_service_btn);
        addService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: add service");

                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.add_service_view);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
                
                Button add_service = dialog.findViewById(R.id.save_btn);

                addServiceName = dialog.findViewById(R.id.service_name);
                addServicePrice = dialog.findViewById(R.id.service_price);

                add_service.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: add service");

                        serviceArrayList.add(new Service(addServiceName.getText().toString(), Double.parseDouble(addServicePrice.getText().toString())));
                        ServiceListAdapter serviceAdapter = new ServiceListAdapter(getActivity(), R.layout.services_list_item, serviceArrayList);
                        serviceList.setAdapter(serviceAdapter);

                        ViewGroup.LayoutParams lp = serviceList.getLayoutParams();

                        lp.height = serviceArrayList.size() * 80;
                        serviceList.setLayoutParams(lp);

                        dialog.cancel();

                        Log.d(TAG, "Service list: " + serviceArrayList.size() * 100);
                    }
                });
            }
        });


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: save called");

                Double salonLat = googleMap.getCameraPosition().target.latitude;
                Double salonLon = googleMap.getCameraPosition().target.longitude;
                String name = salonName.getText().toString();
                String description = salonDescription.getText().toString();
                String createdUser = currentUser.getUid();
                String phone = salonPhoneNumber.getText().toString();
                String malePrice = salonMaleAveragPrice.getText().toString();
                String femalePrice = femaleAveragePrice.getText().toString();

                Boolean allOkeyForSaving = true;

                if (name.equals("")) {
                    salonName.setError("Salon name require");
                    allOkeyForSaving = false;
                }
                if (phone.equals("")) {
                    salonPhoneNumber.setError("Phone number require");
                    allOkeyForSaving = false;
                }
                if (malePrice.equals("")) {
                    salonMaleAveragPrice.setError("Male price require");
                    allOkeyForSaving = false;
                }
                if (femalePrice.equals("")) {
                    femaleAveragePrice.setError("Female price require");
                    allOkeyForSaving = false;
                }

                if (allOkeyForSaving) {
                    Salon salon = new Salon(name, description, salonLat,
                    salonLon, phone, malePrice, femalePrice, createdUser);
                    Log.d(TAG, "save salon: " + salon.toString());

                    salon.create();

                    for (Service service : serviceArrayList) {
                        Service addService = new Service(service.getName(), salon.id, service.getPrice());
                        addService.create();
                    }

                    Bundle arguments = new Bundle();
                    arguments.putSerializable(Salon.class.getSimpleName(), salon);

                    SalonViewFragment salonViewFragment = new SalonViewFragment();
                    salonViewFragment.setArguments(arguments);

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.user_fragment_container, salonViewFragment)
                            .commit();
                }

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.create_salon_map);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.436375, 24.756952), 15));
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                Log.d(TAG, "onMapClick: called");

                editMapDialog = new Dialog(getActivity());
                editMapDialog.setContentView(R.layout.map_view);
                editMapDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                editMapDialog.show();

                dialogMapView = editMapDialog.findViewById(R.id.dialog_mapview);

                dialogMapView.onCreate(editMapDialog.onSaveInstanceState());
                dialogMapView.onResume();

                dialogMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap map) {
                        editMap = map;
                        editMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude), 16));
                        editMap.getUiSettings().setZoomControlsEnabled(true);
                    }
                });


                ImageView set_location_btn = editMapDialog.findViewById(R.id.set_btn);

                set_location_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d(TAG, "onClick: set location camera postison: " + editMap.getCameraPosition());

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(editMap.getCameraPosition().target.latitude, editMap.getCameraPosition().target.longitude), 15));

                        editMapDialog.dismiss();
                    }
                });
            }
        });

    }

}