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

public class userPolices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_polices);

        ListView policeStationList = findViewById(R.id.policeStationList);

        DatabaseReference policeRef = FirebaseDatabase.getInstance().getReference().child("polices");

        policeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Polices> policesList = new ArrayList<>();

                for (DataSnapshot policeSnapshot : snapshot.getChildren()){
                    Polices polices = policeSnapshot.getValue(Polices.class);
                    policesList.add(polices);
                }

                // Create and set the custom adapter for the ListView
                PoliceAdapter adapter = new PoliceAdapter(userPolices.this, policesList);
                policeStationList.setAdapter(adapter);

                //Set on click listener for ListView items
                policeStationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        // Get the selected police
                        Polices selectedPolice = policesList.get(position);
                        DataSnapshot policeSnapshot = snapshot.getChildren().iterator().next();
                        String policeKey = policeSnapshot.getKey();
                        //Create intent to start PlacesInfo activity
                        Intent intent = new Intent(userPolices.this, userPolicesInfo.class);
                        intent.putExtra("policeKey",policeKey);
                        intent.putExtra("policeName", selectedPolice.getName());
                        intent.putExtra("policeDescription", selectedPolice.getDescription());
                        intent.putExtra("policeImage", selectedPolice.getImage());
                        intent.putExtra("policeNumber", selectedPolice.getNumber());
                        intent.putExtra("policeLong", selectedPolice.getLongitude());
                        intent.putExtra("policeLat", selectedPolice.getLatitude());
                        //Start intent
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