package com.example.travelwell_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class userEmergencyServices extends AppCompatActivity {

    private CardView cardViewPolice;
    private CardView cardViewHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_emergency_services);

        cardViewPolice = findViewById(R.id.cardViewPolice);
        cardViewHospital = findViewById(R.id.cardViewHospital);

        // Set click listener for Police's CardView
        cardViewPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to userPolices activity
                Intent intent = new Intent(userEmergencyServices.this, userPolices.class);
                startActivity(intent);
            }
        });

        // Set click listener for Hospital's CardView
        cardViewHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to userHospitals activity
                Intent intent = new Intent(userEmergencyServices.this,  userHospitals.class);
                startActivity(intent);
            }
        });
    }
}