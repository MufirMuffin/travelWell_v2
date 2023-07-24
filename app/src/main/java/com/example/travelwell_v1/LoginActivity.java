package com.example.travelwell_v1;

import android.content.Context;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    TextView signupRedirectText;
    Button loginButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = loginUsername.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                // Validate username and password field
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authenticate the user
                authenticateUser(username, password);
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve user credentials from SharedPreferences to check if the user is already logged in
        String storedUsername = sharedPreferences.getString("username", "");
        String storedUserRole = sharedPreferences.getString("userRole", "");

        if (!storedUsername.isEmpty() && !storedUserRole.isEmpty()) {
            // User is already logged in
            if (storedUserRole.equals("admin")) {
                // Redirect to the admin dashboard or activity
                Intent intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
                intent.putExtra("username", storedUsername);
                startActivity(intent);
                finish(); // Optional: Prevents going back to the LoginActivity when pressing the back button
            } else if (storedUserRole.equals("normal_user")) {
                // Redirect to the normal user dashboard or activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", storedUsername);
                startActivity(intent);
                finish(); // Optional: Prevents going back to the LoginActivity when pressing the back button
            }
        }
    }

    private void authenticateUser(String username, String password) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean authenticated = false;
                String userId = null;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String usernameFromDB = userSnapshot.child("username").getValue(String.class);
                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                    String userIdFromDB = userSnapshot.getKey();

                    if (username.trim().equals(usernameFromDB.trim()) && password.equals(passwordFromDB)) {
                        // Authentication successful
                        authenticated = true;
                        userId = userIdFromDB;
                        break;
                    }
                }

                if (authenticated) {
                    // Proceed with the authenticated user
                    String finalUserId = userId;
                    determineUserRole(username, new RoleCallback() {
                        @Override
                        public void onRoleCallback(String role) {
                            proceedWithAuthenticatedUser(username, role, finalUserId);
                        }
                    });
                } else {
                    // Check if it's an admin user
                    determineAdminUser(username, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error case
                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithAuthenticatedUser(String username, String userRole, String userId) {
        // Store the user credentials in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("userRole", userRole);
        editor.putString("loggedInUserId", userId);
        editor.apply();

        storeLoggedInUser(username);
        storeLoggedInUserId(userId);

        // Display the Toast message about saved loggedInUserId
        displayLoggedInUserIdToast(userId);

        if (userRole.equals("admin")) {
            // User is an admin
            // Proceed to the admin dashboard or activity
            Intent intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else if (userRole.equals("normal_user")) {
            // User is a normal user
            // Proceed to the MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        }
    }

    private void determineUserRole(String username, RoleCallback callback) {
        DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference().child("admins");
        adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(username)) {
                    // User is an admin
                    callback.onRoleCallback("admin");
                } else {
                    // User is a normal user
                    callback.onRoleCallback("normal_user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void determineAdminUser(String username, String password) {
        DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference().child("admins");
        adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean authenticated = false;

                for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                    String adminUsername = adminSnapshot.child("username").getValue(String.class);
                    String adminPassword = adminSnapshot.child("password").getValue(String.class);

                    if (username.trim().equals(adminUsername.trim()) && password.equals(adminPassword)) {
                        // Authentication successful for admin user
                        authenticated = true;
                        proceedWithAuthenticatedUser(username, "admin", null);
                        break;
                    }
                }

                if (!authenticated) {
                    // Authentication failed
                    // Show an error message or prompt the user to enter valid credentials
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface RoleCallback {
        void onRoleCallback(String role);
    }

    // SharedPreferences
    private void storeLoggedInUser(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUser", username);
        editor.apply();
    }
    private void storeLoggedInUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUserId", userId);
        editor.apply();
    }
    private void displayLoggedInUserIdToast(String userId) {
        String message = "Logged-in User ID: " + userId;
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
