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

public class HospitalInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_info);

        //Retrieve
        Intent intent = getIntent();
        String hospitalKey = intent.getStringExtra("hospitalKey");
        String hospitalName = intent.getStringExtra("hospitalName");
        String hospitalDescription = intent.getStringExtra("hospitalDescription");
        String hospitalNumber = intent.getStringExtra("hospitalNumber");
        String hospitalImage = intent.getStringExtra("hospitalImage");

        //Find the TextViews in the layout file
        TextView hospitalNameTextView = findViewById(R.id.hospitalNameTextView);
        TextView hospitalDescriptionTextView = findViewById(R.id.hospitalDescriptionTextView);
        TextView hospitalNumberTextView = findViewById(R.id.hospitalNumberTextView);
        Button hospitalDeleteButton = findViewById(R.id.hospitalDeleteButton);
        ImageView hospitalImageView = findViewById(R.id.hospitalImageView);

        hospitalNameTextView.setText(hospitalName);
        hospitalDescriptionTextView.setText(hospitalDescription);
        hospitalNumberTextView.setText(hospitalNumber);

        Picasso.get().load(hospitalImage).into(hospitalImageView);

        hospitalDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference().child("hospitals");

                Query deleteQuery = hospitalsRef.orderByChild("name").equalTo(hospitalName);
                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot hospitalSnapshot : snapshot.getChildren()){
                            hospitalSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(HospitalInfo.this, "Hospital deleted successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(HospitalInfo.this, "Failed to delete hospital", Toast.LENGTH_SHORT).show();
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