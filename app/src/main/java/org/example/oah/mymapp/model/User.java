package org.example.oah.mymapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class User {

    private static final String TAG = "User";

    String name, email, id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public User(String id, String name, String email) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void save() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.child(this.id).setValue(this);

        Date currentTime = Calendar.getInstance().getTime();
        Map<String, Object> createUser = new HashMap<>();
        createUser.put("name", this.name);
        createUser.put("email", this.email);
        createUser.put("createDate", currentTime);


        db.collection("Users")
                .document(this.id)
                .set(createUser);
    }

    public void update(){
        Map<String, Object> changeUser = new HashMap<>();
        changeUser.put("name", this.name);
        changeUser.put("email", this.email);

        db.collection("Users")
                .document(this.id)
                .update(changeUser);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
