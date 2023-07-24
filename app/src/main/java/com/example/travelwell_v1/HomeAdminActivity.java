package com.example.travelwell_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeAdminActivity extends AppCompatActivity {

    CardView listPlacesButton, listEmergencyButton;
    Button logoutAdminButton, signupAdmin;

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        editor = getSharedPreferences("MyAppPreferences", MODE_PRIVATE).edit();

        listPlacesButton = findViewById(R.id.listPlacesButton);
        listEmergencyButton = findViewById(R.id.listEmergencyButton);
        logoutAdminButton = findViewById(R.id.logoutAdminButton);
        signupAdmin = findViewById(R.id.signupAdmin);

        listPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this,AdminPlaces.class);
                startActivity(intent);
            }
        });

        listEmergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this,AdminEmergency.class);
                startActivity(intent);
            }
        });

        signupAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAdminActivity.this,SignupAdminActivity.class);
                startActivity(intent);
            }
        });

        logoutAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the saved admin credentials
                editor.clear();
                editor.apply();

                // Redirect to the login activity
                Intent intent = new Intent(HomeAdminActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
}