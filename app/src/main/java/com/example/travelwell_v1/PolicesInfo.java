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

public class PolicesInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polices_info);

        //Retrieve the police details from the intent extras
        Intent intent = getIntent();
        String policeKey = intent.getStringExtra("policeKey");
        String policeName = intent.getStringExtra("policeName");
        String policeDescription = intent.getStringExtra("policeDescription");
        String policeImage = intent.getStringExtra("policeImage");
        String policeNumber = intent.getStringExtra("policeNumber");

        //Find the TextViews in the layout file
        TextView policeNameTxtView = findViewById(R.id.policeNameTxtView);
        TextView policeDescriptionTextView = findViewById(R.id.policeDescriptionTextView);
        TextView policeNumberTextView = findViewById(R.id.policeNumberTextView);
        TextView policeLatitudeTextView = findViewById(R.id.policeLatitudeTextView);
        TextView policeLongitudeTextView = findViewById(R.id.policeLongitudeTextView);
        Button policeUpdateButton = findViewById(R.id.policeUpdateButton);
        Button policeDeleteButton = findViewById(R.id.policeDeleteButton);
        ImageView policeImageView = findViewById(R.id.policeImgView);

        //Set the police details to textview
        policeNameTxtView.setText(policeName);
        policeDescriptionTextView.setText(policeDescription);
        policeNumberTextView.setText(policeNumber);

        Picasso.get().load(policeImage).into(policeImageView);

        DatabaseReference policeNameRef = FirebaseDatabase.getInstance().getReference().child("polices");
        policeNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot policeSnapshot : snapshot.getChildren()){
                    String key = policeSnapshot.getKey();
                    Double latitudeDouble = policeSnapshot.child("latitude").getValue(Double.class);
                    Double longitudeDouble = policeSnapshot.child("longitude").getValue(Double.class);
                    String latitude = String.valueOf(latitudeDouble);
                    String longitude = String.valueOf(longitudeDouble);

                    if (key.equals(policeKey)){
                        policeLatitudeTextView.setText(latitude);
                        policeLongitudeTextView.setText(longitude);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //update
        policeUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(PolicesInfo.this, UpdatePoliceActivity.class);
                updateIntent.putExtra("policeID", policeKey);
                updateIntent.putExtra("policeName", policeName);
                updateIntent.putExtra("policeDescription",policeDescription);
                updateIntent.putExtra("imageURL", policeImage);
                updateIntent.putExtra("policeLatitude",policeLatitudeTextView.getText().toString());
                updateIntent.putExtra("policeLongitude",policeLongitudeTextView.getText().toString());

                startActivity(updateIntent);

            }
        });

        //delete
        policeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference policesRef = FirebaseDatabase.getInstance().getReference().child("polices");

                Query deleteQuery = policesRef.orderByChild("name").equalTo(policeName);
                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot policeSnapshot: dataSnapshot.getChildren()){
                            policeSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(PolicesInfo.this, "Police Station deleted successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PolicesInfo.this, "Failed to delete police station", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }
}