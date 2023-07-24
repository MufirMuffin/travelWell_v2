package com.example.travelwell_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class userHospitals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_hospitals);

        ListView hospitalList = findViewById(R.id.hospitalList);

        DatabaseReference hospitalRef = FirebaseDatabase.getInstance().getReference().child("hospitals");

        hospitalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Hospitals> hospitalsList = new ArrayList<>();

                for (DataSnapshot hospitalSnapshot : snapshot.getChildren()){
                    Hospitals hospitals = hospitalSnapshot.getValue(Hospitals.class);
                    hospitalsList.add(hospitals);
                }

                HospitalAdapter adapter = new HospitalAdapter(userHospitals.this, hospitalsList);
                hospitalList.setAdapter(adapter);

                //
                hospitalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Hospitals selectedHospital = hospitalsList.get(position);
                        DataSnapshot hospitalSnapshot = snapshot.getChildren().iterator().next();
                        String hospitalKey = hospitalSnapshot.getKey();
                        //Intent
                        Intent intent = new Intent(userHospitals.this, userHospitals.class);
                        intent.putExtra("hospitalKey",hospitalKey);
                        intent.putExtra("hospitalName",selectedHospital.getName());
                        intent.putExtra("hospitalDescription",selectedHospital.getDescription());
                        intent.putExtra("hospitalImage",selectedHospital.getImage());
                        intent.putExtra("hospitalNumber",selectedHospital.getNumber());
                        intent.putExtra("hospitalLong",selectedHospital.getLongitude());
                        intent.putExtra("hospitalLat",selectedHospital.getLatitude());

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