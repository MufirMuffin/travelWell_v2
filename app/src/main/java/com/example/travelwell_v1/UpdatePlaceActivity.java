package com.example.travelwell_v1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UpdatePlaceActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText descriptionEditText, updatePlaceLongitudeEditText, updatePlaceLatitudeEditText;
    Button updateButton;
    ImageView updatePlaceImageView;
    DatabaseReference placesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_place);

        // Retrieve the place details from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish(); // Handle case of missing extras, or you can show an error message
            return;
        }

        String placeKey = extras.getString("placeID");
        String placeName = extras.getString("placeName");
        String placeDescription = extras.getString("placeDescription");
        String placeLatitude = extras.getString("placeLatitude");
        String placeLongitude = extras.getString("placeLongitude");
        String imageURL = extras.getString("imageURL");

        // Initialize Firebase Database reference
        placesRef = FirebaseDatabase.getInstance().getReference().child("places").child(placeKey);

        // Find views in the layout file
        nameEditText = findViewById(R.id.updatePlaceNameEditText);
        descriptionEditText = findViewById(R.id.updatePlaceDescriptionEditText);
        updatePlaceLongitudeEditText = findViewById(R.id.updatePlaceLongitudeEditText);
        updatePlaceLatitudeEditText = findViewById(R.id.updatePlaceLatitudeEditText);
        updatePlaceImageView = findViewById(R.id.updatePlaceImageView);
        updateButton = findViewById(R.id.updateButton);

        // Set the initial place details to the EditTexts
        nameEditText.setText(placeName);
        descriptionEditText.setText(placeDescription);
        updatePlaceLatitudeEditText.setText(placeLatitude);
        updatePlaceLongitudeEditText.setText(placeLongitude);

        Picasso.get().load(imageURL).into(updatePlaceImageView);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = nameEditText.getText().toString().trim();
                String updatedDescription = descriptionEditText.getText().toString().trim();
                String updatedLatitudeString = updatePlaceLatitudeEditText.getText().toString().trim();
                String updatedLongitudeString = updatePlaceLongitudeEditText.getText().toString().trim();


                // Convert latitude and longitude from string to double
                Double updatedLatitude = Double.parseDouble(updatedLatitudeString);
                Double updatedLongitude = Double.parseDouble(updatedLongitudeString);

                // Update the place details in the database
                placesRef.child("name").setValue(updatedName);
                placesRef.child("description").setValue(updatedDescription);
                placesRef.child("latitude").setValue(updatedLatitude);
                placesRef.child("longitude").setValue(updatedLongitude)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Place details updated successfully
                                Toast.makeText(UpdatePlaceActivity.this, "Place details updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to update place details
                                Toast.makeText(UpdatePlaceActivity.this, "Failed to update place details", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
