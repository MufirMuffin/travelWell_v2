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

public class UpdatePoliceActivity extends AppCompatActivity {

    EditText updatePoliceNameEditText, updatePoliceDescriptionEditText, updatePoliceLatitudeEditText;
    EditText updatePoliceLongitudeEditText;
    ImageView updatePoliceImageView;
    Button updatePoliceButton;
    DatabaseReference policesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_police);

        //Retrieve police details from intent extras
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            finish();
            return;
        }

        String policeKey = extras.getString("policeID");
        String policeName = extras.getString("policeName");
        String policeDescription = extras.getString("policeDescription");
        String policeLatitude = extras.getString("policeLatitude");
        String policeLongitude = extras.getString("policeLongitude");
        String imageURL = extras.getString("imageURL");

        //Initialize Firebase Database ref
        policesRef = FirebaseDatabase.getInstance().getReference().child("polices").child(policeKey);

        //Find views in layout file
        updatePoliceNameEditText = findViewById(R.id.updatePoliceNameEditText);
        updatePoliceDescriptionEditText = findViewById(R.id.updatePoliceDescriptionEditText);
        updatePoliceLongitudeEditText = findViewById(R.id.updatePoliceLongitudeEditText);
        updatePoliceLatitudeEditText = findViewById(R.id.updatePoliceLatitudeEditText);
        updatePoliceImageView = findViewById(R.id.updatePoliceImageView);
        updatePoliceButton = findViewById(R.id.updatePoliceButton);

        // Set the initial police details
        updatePoliceNameEditText.setText(policeName);
        updatePoliceDescriptionEditText.setText(policeDescription);
        updatePoliceLongitudeEditText.setText(policeLongitude);
        updatePoliceLatitudeEditText.setText(policeLatitude);

        Picasso.get().load(imageURL).into(updatePoliceImageView);

        updatePoliceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = updatePoliceNameEditText.getText().toString().trim();
                String updatedDescription = updatePoliceDescriptionEditText.getText().toString().trim();
                String updatedLatitudeString = updatePoliceLatitudeEditText.getText().toString().trim();
                String updatedLongitudeString = updatePoliceLongitudeEditText.getText().toString().trim();

                //Convert Lat and Long
                Double updatedLatitude = Double.parseDouble(updatedLatitudeString);
                Double updatedLongitude = Double.parseDouble(updatedLongitudeString);

                //Update the police details in the database
                policesRef.child("name").setValue(updatedName);
                policesRef.child("description").setValue(updatedDescription);
                policesRef.child("latitude").setValue(updatedLatitude);
                policesRef.child("longitude").setValue(updatedLongitude)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Police details updated successfully
                                Toast.makeText(UpdatePoliceActivity.this, "Police Station details updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Failed to update
                                Toast.makeText(UpdatePoliceActivity.this, "Failed to update police station", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}