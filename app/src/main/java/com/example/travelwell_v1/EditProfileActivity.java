package com.example.travelwell_v1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private TextView editUsername;
    private EditText editPassword;
    private Button saveButton;

    private DatabaseReference databaseReference;

    private String currentUsername; // Added variable to store the current user's username
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        //sharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String loggedInUser = getLoggedInUser();

        currentUsername = getIntent().getStringExtra("username"); // Get the current user's username

        if (!loggedInUser.isEmpty()) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String password = dataSnapshot.child("password").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);

                        // Use the retrieved data as needed
                        editEmail.setHint(email);
                        editUsername.setHint(username);
                        editPassword.setHint(password);
                        editName.setHint(name);
                    } else {
                        // Handle the case when the user data doesn't exist
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error case
                }
            });
        } else {
            // Handle the case when the userID is empty or not found in SharedPreferences
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        //String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the user profile in the Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(getLoggedInUser());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DatabaseReference userRef = dataSnapshot.getRef();
                    userRef.child("name").setValue(name);
                    userRef.child("email").setValue(email);
                    //userRef.child("username").setValue(username);
                    userRef.child("password").setValue(password);

                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the EditProfileActivity
                } else {
                    // Handle the case when the user data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
            }
        });
    }

    //sharedPreferences
    private String getLoggedInUser() {
        return sharedPreferences.getString("loggedInUser", "");
    }
}
