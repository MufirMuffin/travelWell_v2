package com.example.travelwell_v1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class userHospitalsInfo extends AppCompatActivity {

    private String hospitalNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_hospitals_info);

        //Retrieve
        Intent intent = getIntent();
        String hospitalName = intent.getStringExtra("hospitalName");
        String hospitalDesc = intent.getStringExtra("hospitalDescription");
        String hospitalImage = intent.getStringExtra("hospitalImage");
        hospitalNumber = intent.getStringExtra("hospitalNumber");
        Double hospitalLong = intent.getDoubleExtra("hospitalLong",0.0);
        Double hospitalLat = intent.getDoubleExtra("hospitalLat",0.0);

        //Find in xml
        ImageView userhospitalImageView = findViewById(R.id.userhospitalImageView);
        TextView userhospitalNameTextView = findViewById(R.id.userhospitalNameTextView);
        TextView userhospitalDescriptionTextView = findViewById(R.id.userhospitalDescriptionTextView);
        TextView userhospitalNumberTextView = findViewById(R.id.userhospitalNumberTextView);
        MapView userhospitalMapView = findViewById(R.id.userhospitalMapView);
        Button BtnHospitalCall = findViewById(R.id.BtnHospitalCall);

        //Set
        userhospitalNameTextView.setText(hospitalName);
        userhospitalDescriptionTextView.setText(hospitalDesc);
        userhospitalNumberTextView.setText(hospitalNumber);
        Picasso.get().load(hospitalImage).into(userhospitalImageView);
        userhospitalMapView.onCreate(savedInstanceState);
        userhospitalMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                //Setup Map
                LatLng location = new LatLng(hospitalLat,hospitalLong);
                googleMap.addMarker(new MarkerOptions().position(location).title(hospitalName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));
            }
        });
        // Set click listener for the call button
        BtnHospitalCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }
    private void makePhoneCall() {
        // Check if the police number is available
        if (hospitalNumber != null && !hospitalNumber.isEmpty()) {
            // Create an intent to initiate the phone call
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + hospitalNumber));
            startActivity(intent);
        }
    }
}