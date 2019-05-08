package org.example.oah.mymapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.example.oah.mymapp.R;
import org.example.oah.mymapp.model.User;

public class UserViewFragment extends Fragment {
    private static final String TAG = "UserViewFragment";

    TextView nameField, emailField, editName, editEmail;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    String name, email;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_view, container, false);

        currentUser = mAuth.getCurrentUser();

        nameField = view.findViewById(R.id.user_name);
        emailField = view.findViewById(R.id.user_email);

        ImageView editname = view.findViewById(R.id.edit_name_btn);

        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.edit_string);

                editName = dialog.findViewById(R.id.editText);

                editName.setText(user.getName());

                Button saveBtn = dialog.findViewById(R.id.save_btn);
                Button cancel = dialog.findViewById(R.id.cancel_btn);

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setName(editName.getText().toString());
                        nameField.setText(editName.getText().toString());
                        user.update();
                        dialog.cancel();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);


                dialog.show();

            }
        });

        ImageView editemail = view.findViewById(R.id.edit_email_btn);

        editemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.edit_string);

                editEmail = dialog.findViewById(R.id.editText);

                editEmail.setText(user.getEmail());

                Button saveBtn = dialog.findViewById(R.id.save_btn);
                Button cancel = dialog.findViewById(R.id.cancel_btn);

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setEmail(editEmail.getText().toString());
                        emailField.setText(editEmail.getText().toString());
                        user.update();
                        dialog.cancel();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();

            }
        });




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(currentUser.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Log.d(TAG, "KEY: " + child.getKey());
                    Log.d(TAG, "VALUE: " + child.getValue());

                    if (child.getKey().equals("name")) {
                        name = child.getValue().toString();
                        nameField.setText(name);
                    }

                    if (child.getKey().equals("email")) {
                        email = child.getValue().toString();
                        emailField.setText(email);
                    }

                }
                user = new User(currentUser.getUid(), name, email);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        Button logOutBtn = view.findViewById(R.id.logout_btn);

        logOutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: log out");

                mAuth.signOut();
                Intent myIntent = new Intent(getActivity(), MapsActivity.class);
                startActivity(myIntent);

            }
        });

        return view;
    }


}
