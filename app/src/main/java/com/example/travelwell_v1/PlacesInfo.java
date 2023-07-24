package com.example.travelwell_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PlacesInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_info);

        // Retrieve the place details from the intent extras
        Intent intent = getIntent();
        String placeKey = intent.getStringExtra("placeID"); // Update the key name to "placeID"
        String placeName = intent.getStringExtra("placeName");
        String placeDescription = intent.getStringExtra("placeDescription");
        String imageURL = intent.getStringExtra("imageURL");

        // Add other place details here if needed

        // Find the TextViews in the layout file
        TextView nameTextView = findViewById(R.id.placeNameTextView);
        TextView descriptionTextView = findViewById(R.id.placeDescriptionTextView);
        Button placeDeleteButton = findViewById(R.id.placeDeleteButton);
        ImageView placeImageView = findViewById(R.id.placeImageView);
        TextView placeLongitudeTextView = findViewById(R.id.placeLongitudeTextView);
        TextView placeLatitudeTextView = findViewById(R.id.placeLatitudeTextView);
        Button placeUpdateButton = findViewById(R.id.placeUpdateButton);

        //TextView placeKeyTextView = findViewById(R.id.placeKeyTextView);

        // Set the place details to the TextViews
        nameTextView.setText(placeName);
        descriptionTextView.setText(placeDescription);

        //placeKeyTextView.setText(placeKey);
        // Load the image into the ImageView using Picasso
        Picasso.get().load(imageURL).into(placeImageView);

        DatabaseReference placesNameRef = FirebaseDatabase.getInstance().getReference().child("places");
        placesNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot placeSnapshot : snapshot.getChildren()){
                    String key = placeSnapshot.getKey();
                    Double latitudeDouble = placeSnapshot.child("latitude").getValue(Double.class);
                    Double longitudeDouble = placeSnapshot.child("longitude").getValue(Double.class);
                    String latitude = String.valueOf(latitudeDouble);
                    String longitude = String.valueOf(longitudeDouble);


                    if (key.equals(placeKey)) {
                        placeLatitudeTextView.setText(latitude);
                        placeLongitudeTextView.setText(longitude);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //update place
        placeUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(PlacesInfo.this, UpdatePlaceActivity.class);
                updateIntent.putExtra("placeID", placeKey);
                updateIntent.putExtra("placeName", placeName);
                updateIntent.putExtra("placeDescription", placeDescription);
                updateIntent.putExtra("imageURL", imageURL);
                updateIntent.putExtra("placeLatitude", placeLatitudeTextView.getText().toString());
                updateIntent.putExtra("placeLongitude", placeLongitudeTextView.getText().toString());
                // Add other place details to the intent if needed
                startActivity(updateIntent);
            }
        });

        //delete place
        placeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");

                Query deleteQuery = placesRef.orderByChild("name").equalTo(placeName);
                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                            placeSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Place deleted successfully
                                            Toast.makeText(PlacesInfo.this, "Place deleted successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to delete place
                                            Toast.makeText(PlacesInfo.this, "Failed to delete place", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle onCancelled event if needed
                    }
                });
            }
        });
    }
}
