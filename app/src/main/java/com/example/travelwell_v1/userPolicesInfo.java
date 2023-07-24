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

public class userPolicesInfo extends AppCompatActivity {

    private String policeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_polices_info);

        //Retrieve
        Intent intent = getIntent();
        String policeName = intent.getStringExtra("policeName");
        String policeDescription = intent.getStringExtra("policeDescription");
        String policeImage = intent.getStringExtra("policeImage");
        policeNumber = intent.getStringExtra("policeNumber");
        Double policeLong = intent.getDoubleExtra("policeLong",0.0);
        Double policeLat = intent.getDoubleExtra("policeLat",0.0);
        //Find TextViews
        //ImgViewPolice
        //TxtViewPoliceName, TxtViewPoliceDescription, TxtViewPoliceNumber
        //BtnPoliceCall
        TextView TxtViewPoliceName = findViewById(R.id.TxtViewPoliceName);
        TextView TxtViewPoliceDescription = findViewById(R.id.TxtViewPoliceDescription);
        TextView TxtViewPoliceNumber = findViewById(R.id.TxtViewPoliceNumber);
        Button BtnPoliceCall = findViewById(R.id.BtnPoliceCall);
        ImageView ImgViewPolice = findViewById(R.id.ImgViewPolice);
        MapView userPoliceMapView = findViewById(R.id.userPoliceMapView);

        //Set
        TxtViewPoliceName.setText(policeName);
        TxtViewPoliceDescription.setText(policeDescription);
        TxtViewPoliceNumber.setText(policeNumber);

        Picasso.get().load(policeImage).into(ImgViewPolice);

        //MapView
        userPoliceMapView.onCreate(savedInstanceState);
        userPoliceMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                //Setup Map
                LatLng location = new LatLng(policeLat,policeLong);
                googleMap.addMarker(new MarkerOptions().position(location).title(policeName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));
            }
        });

        // Set click listener for the call button
        BtnPoliceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }
    private void makePhoneCall() {
        // Check if the police number is available
        if (policeNumber != null && !policeNumber.isEmpty()) {
            // Create an intent to initiate the phone call
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + policeNumber));
            startActivity(intent);
        }
    }
}