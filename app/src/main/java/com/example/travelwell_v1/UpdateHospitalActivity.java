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

public class UpdateHospitalActivity extends AppCompatActivity {

    EditText updateHospitalNameEditText, updateHospitalDescriptionEditText, updateHospitalLatitudeEditText;
    EditText updateHospitalLongitudeEditText;
    ImageView updateHospitalImageView;
    Button updateHospitalButton;
    DatabaseReference hospitalsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_hospital);

        //Retrieve police details from intent extras
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            finish();
            return;
        }

        String hospitalKey = extras.getString("hospitalID");
        String hospitalName = extras.getString("hospitalName");
        String hospitalDescription = extras.getString("hospitalDescription");
        String hospitalLatitude = extras.getString("hospitalLatitude");
        String hospitalLongitude = extras.getString("hospitalLongitude");
        String imageURL = extras.getString("imageURL");

        //Initialize Firebase Database ref
        hospitalsRef = FirebaseDatabase.getInstance().getReference().child("hospitals").child(hospitalKey);

        //Find views in layout file
        updateHospitalNameEditText = findViewById(R.id.updateHospitalNameEditText);
        updateHospitalDescriptionEditText = findViewById(R.id.updateHospitalDescriptionEditText);
        updateHospitalLongitudeEditText = findViewById(R.id.updateHospitalLongitudeEditText);
        updateHospitalLatitudeEditText = findViewById(R.id.updateHospitalLatitudeEditText);
        updateHospitalImageView = findViewById(R.id.updateHospitalImageView);
        updateHospitalButton = findViewById(R.id.updateHospitalButton);

        // Set the initial police details
        updateHospitalNameEditText.setText(hospitalName);
        updateHospitalDescriptionEditText.setText(hospitalDescription);
        updateHospitalLongitudeEditText.setText(hospitalLongitude);
        updateHospitalLatitudeEditText.setText(hospitalLatitude);
        Picasso.get().load(imageURL).into(updateHospitalImageView);

        updateHospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = updateHospitalNameEditText.getText().toString().trim();
                String updatedDescription = updateHospitalDescriptionEditText.getText().toString().trim();
                String updatedLatitudeString = updateHospitalLatitudeEditText.getText().toString().trim();
                String updatedLongitudeString = updateHospitalLongitudeEditText.getText().toString().trim();

                //Convert Lat and Long
                Double updatedLatitude = Double.parseDouble(updatedLatitudeString);
                Double updatedLongitude = Double.parseDouble(updatedLongitudeString);

                //Update the police details in the database
                hospitalsRef.child("name").setValue(updatedName);
                hospitalsRef.child("description").setValue(updatedDescription);
                hospitalsRef.child("latitude").setValue(updatedLatitude);
                hospitalsRef.child("longitude").setValue(updatedLongitude)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Police details updated successfully
                                Toast.makeText(UpdateHospitalActivity.this, "Police Station details updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Failed to update
                                Toast.makeText(UpdateHospitalActivity.this, "Failed to update police station", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}