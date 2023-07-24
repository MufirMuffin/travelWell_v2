package com.example.travelwell_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class userReview extends AppCompatActivity {

    private ListView placeListView;
    private List<Place> placeList;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        placeListView = findViewById(R.id.placeListView);

        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");
        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Places> placesList = new ArrayList<>();

                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()){
                    String placeKey = placeSnapshot.getKey();
                    Places places = placeSnapshot.getValue(Places.class);
                    places.setPlaceKey(placeKey);
                    placesList.add(places);
                }

                ReviewAdapter adapter = new ReviewAdapter(userReview.this, placesList);
                placeListView.setAdapter(adapter);

                placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Handle the click event for the item in the ListView
                        Places selectedPlace = placesList.get(position);

                        //Retrieve placeKey indirectly
                        String placeKey = selectedPlace.getPlaceKey();

                        // Pass the selected place data to the ReviewActivity
                        Intent intent = new Intent(userReview.this, ReviewActivity.class);
                        intent.putExtra("placeKey", placeKey);
                        intent.putExtra("placeName", selectedPlace.getName());
                        intent.putExtra("placeDescription", selectedPlace.getDescription());
                        intent.putExtra("imageURL", selectedPlace.getImageURL());
                        intent.putExtra("placeLong",selectedPlace.getLongitude());
                        intent.putExtra("placeLat",selectedPlace.getLatitude());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}