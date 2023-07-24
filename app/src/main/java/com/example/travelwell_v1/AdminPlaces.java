    package com.example.travelwell_v1;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.Button;
    import android.widget.ListView;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.List;

    public class AdminPlaces extends AppCompatActivity {

        Button addPlaceButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_places);

            ListView listView = findViewById(R.id.placesListView);
            DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");
            addPlaceButton = findViewById(R.id.addPlaceButton);

            placesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Places> placesList = new ArrayList<>();

                    for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                        String placeKey = placeSnapshot.getKey();
                        Places places = placeSnapshot.getValue(Places.class);
                        placesList.add(places);
                    }

                    // Create and set the custom adapter for the ListView
                    PlaceAdapter adapter = new PlaceAdapter(AdminPlaces.this, placesList);
                    listView.setAdapter(adapter);

                    // Set click listener for ListView items
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Get the selected place
                            Places selectedPlace = placesList.get(position);

                            // Create intent to start the PlacesInfo activity
                            Intent intent = new Intent(AdminPlaces.this, PlacesInfo.class);
                            intent.putExtra("placeID", selectedPlace.getKey()); // Update the key name to "placeID"
                            intent.putExtra("placeName", selectedPlace.getName());
                            intent.putExtra("placeDescription", selectedPlace.getDescription());
                            intent.putExtra("imageURL", selectedPlace.getImageURL());
                            intent.putExtra("placeLatitude", selectedPlace.getLatitude());
                            intent.putExtra("placeLongitude", selectedPlace.getLongitude());
                            // Add other place details to the intent if needed

                            startActivity(intent);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event if needed
                }
            });

            addPlaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminPlaces.this, AddPlaces.class);
                    startActivity(intent);
                }
            });
        }
    }
