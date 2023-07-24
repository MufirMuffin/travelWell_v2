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

public class AdminEmergency extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_emergency);

        ListView policeStationListView = findViewById(R.id.policeStationListView);
        ListView hospitalListView = findViewById(R.id.hospitalListView);
        Button addPoliceButton = findViewById(R.id.addPoliceButton);
        Button addHospitalButton = findViewById(R.id.addHospitalButton);

        DatabaseReference policeRef = FirebaseDatabase.getInstance().getReference().child("polices");
        DatabaseReference hospitalRef = FirebaseDatabase.getInstance().getReference().child("hospitals");

        policeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Polices> policesList = new ArrayList<>();

                for (DataSnapshot policeSnapshot : dataSnapshot.getChildren()){
                    Polices polices = policeSnapshot.getValue(Polices.class);
                    policesList.add(polices);
                }

                // Create and set the custom adapter for the ListView
                PoliceAdapter adapter = new PoliceAdapter(AdminEmergency.this, policesList);
                policeStationListView.setAdapter(adapter);

                // Set click listener for ListView items
                policeStationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected place
                        Polices selectedPolice = policesList.get(position);
                        DataSnapshot policeSnapshot = dataSnapshot.getChildren().iterator().next();
                        String policeKey = policeSnapshot.getKey();
                        // Create intent to start the PlacesInfo activity
                        Intent intent = new Intent(AdminEmergency.this, PolicesInfo.class);
                        intent.putExtra("policeKey",policeKey);
                        intent.putExtra("policeName", selectedPolice.getName());
                        intent.putExtra("policeDescription", selectedPolice.getDescription());
                        intent.putExtra("policeImage", selectedPolice.getImage());
                        intent.putExtra("policeNumber", selectedPolice.getNumber());
                        // Add other police details to the intent if needed
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        hospitalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Hospitals> hospitalList = new ArrayList<>();

                for (DataSnapshot hospitalSnapshot : snapshot.getChildren()){
                    Hospitals hospitals = hospitalSnapshot.getValue(Hospitals.class);
                    hospitalList.add(hospitals);
                }

                // Create and set the custom adapter for the ListView
                HospitalAdapter adapter2 = new HospitalAdapter(AdminEmergency.this, hospitalList);
                hospitalListView.setAdapter(adapter2);

                // Set click listener for ListView items
                hospitalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected place
                        Hospitals selectedHospital = hospitalList.get(position);
                        DataSnapshot hospitalSnapshot = snapshot.getChildren().iterator().next();
                        String hospitalKey = hospitalSnapshot.getKey();
                        // Create intent to start the PlacesInfo activity
                        Intent intent = new Intent(AdminEmergency.this, HospitalInfo.class);
                        intent.putExtra("hospitalKey",hospitalKey);
                        intent.putExtra("hospitalName", selectedHospital.getName());
                        intent.putExtra("hospitalDescription", selectedHospital.getDescription());
                        intent.putExtra("hospitalImage", selectedHospital.getImage());
                        intent.putExtra("hospitalNumber", selectedHospital.getNumber());

                        // Add other police details to the intent if needed

                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addPoliceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminEmergency.this, AddPolices.class);
                startActivity(intent);
            }
        });

        addHospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminEmergency.this, AddHospitals.class);
                startActivity(intent);
            }
        });

    }
}