package com.example.travelwell_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName;
    private TextView profileEmail;
    private TextView profileUsername;
    private TextView profilePassword;

    private TextView titleName;
    private  TextView titleUsername;
    private Button editButton, logoutButton, reviewPlacesButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editButton = findViewById(R.id.editButton);
        logoutButton = findViewById(R.id.logoutButton);
        reviewPlacesButton = findViewById(R.id.reviewPlacesButton);

        //Button reviewButton = findViewById(R.id.reviewButton);

        //sharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String loggedInUser = getLoggedInUser();
        profileName.setText(loggedInUser);

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
                        titleName.setText(name);
                        titleUsername.setText(username);
                        profileEmail.setText(email);
                        profileUsername.setText(username);
                        profilePassword.setText(password);
                        profileName.setText(name);
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

        //--Intent
        String username = getIntent().getStringExtra("username");
        showUserData(username);
        //--
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
        //--
        reviewPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, reviewPlacesInfo.class);
                startActivity(intent);
            }
        });
        //--
        /*
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, userReview.class);
                startActivity(intent);
            }
        });
         */
        //--
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the stored user credentials in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                logout();
                // Redirect to the LoginActivity
                Intent intent1 = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish(); // Optional: Prevents going back to the ProfileActivity when pressing back
            }
        });
    }

    //logout method
    private void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    private void showUserData(String username) {
        if (username != null) {
            // Encode the email or use a different identifier
            String encodedUsername = username.replace(".", "_");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(encodedUsername);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nameFromDB = snapshot.child("name").getValue(String.class);
                        String emailFromDB = snapshot.child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child("username").getValue(String.class);
                        String passwordFromDB = snapshot.child("password").getValue(String.class);

                        profileName.setText(nameFromDB);
                        profileEmail.setText(emailFromDB);
                        profileUsername.setText(usernameFromDB);
                        profilePassword.setText(passwordFromDB);

                        titleName.setText(nameFromDB);
                        titleUsername.setText(usernameFromDB);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error if necessary
                }
            });
        }
    }

    //sharedPreferences
    private String getLoggedInUser() {
        return sharedPreferences.getString("loggedInUser", "");
    }

}
