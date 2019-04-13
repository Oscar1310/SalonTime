package org.example.oah.mymapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateUserActivity extends AppCompatActivity {

    private static final String TAG = "CreateUserActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        Button saveBtn = findViewById(R.id.create_user_btn);

        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    public void createUser(){

        TextView emailField = findViewById(R.id.create_user_email);
        TextView passwordField = findViewById(R.id.create_user_password);
        TextView passwordRepeatField = findViewById(R.id.create_user_password_repeat);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordRepeat = passwordRepeatField.getText().toString();

        if (password.equals(passwordRepeat)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "CreateUserActivity:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                TextView nameField = findViewById(R.id.create_user_name);
                                String name = nameField.getText().toString();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profileUpdates);

                                Intent intent = new Intent(CreateUserActivity.this, LoginActivity.class);
                                startActivity(intent);

                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "CreateUserActivity:failure", task.getException());
                                Toast.makeText(CreateUserActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        } else {

            Toast.makeText(CreateUserActivity.this, "Cant create user because passwords not same, Please try again.",
                    Toast.LENGTH_SHORT).show();
        }


    }

}
