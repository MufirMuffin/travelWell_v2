package com.example.travelwell_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.List;

public class userPlacesInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_places_info);

        //Retrieve
        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");
        String placeDescription = intent.getStringExtra("placeDescription");
        String imageURL = intent.getStringExtra("imageURL");
        Double placeLong = intent.getDoubleExtra("placeLong",0.0);
        Double placeLat = intent.getDoubleExtra("placeLat",0.0);

        // Retrieve the reviews list from the intent
        List<Review> reviewsList = (List<Review>) getIntent().getSerializableExtra("reviewsList");

        //Find
        TextView userplaceNameTextView = findViewById(R.id.userplaceNameTextView);
        TextView userplaceDescriptionTextView = findViewById(R.id.userplaceDescriptionTextView);
        ImageView userplaceImageView = findViewById(R.id.userplaceImageView);
        MapView userMapView = findViewById(R.id.userMapView);
        ListView userPlaceReview = findViewById(R.id.userPlaceReview);

        if (reviewsList != null){
            //Set
            userplaceNameTextView.setText(placeName);
            userplaceDescriptionTextView.setText(placeDescription);

            //Upload Image
            Picasso.get().load(imageURL).into(userplaceImageView);

            //Map
            userMapView.onCreate(savedInstanceState);
            userMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    //Setup Map
                    LatLng location = new LatLng(placeLat,placeLong);
                    googleMap.addMarker(new MarkerOptions().position(location).title(placeName));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                }
            });
            // Set up the reviews list
            ReviewAdapter2 reviewAdapter = new ReviewAdapter2(this, reviewsList);
            userPlaceReview.setAdapter(reviewAdapter);
        }
        else {
            //Set
            userplaceNameTextView.setText(placeName);
            userplaceDescriptionTextView.setText(placeDescription);

            //Upload Image
            Picasso.get().load(imageURL).into(userplaceImageView);

            //Map
            userMapView.onCreate(savedInstanceState);
            userMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    //Setup Map
                    LatLng location = new LatLng(placeLat,placeLong);
                    googleMap.addMarker(new MarkerOptions().position(location).title(placeName));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                }
            });
            userPlaceReview.setVisibility(View.GONE);
        }


    }
}