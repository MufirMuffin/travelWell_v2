package com.example.travelwell_v1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class userPolices extends AppCompatActivity {

    private ListView policeStationList;
    private double currentLatitude;
    private double currentLongitude;
    private double maxDistance = 5000; // Maximum distance for nearby police stations

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_polices);

        policeStationList = findViewById(R.id.policeStationList);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    // Call the method to calculate nearby police stations with the updated latitude and longitude
                    calculateAndDisplayNearbyPoliceStations();
                }
            }
        };

        // Check for location permissions and request if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private void requestLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateAndDisplayNearbyPoliceStations() {
        DatabaseReference policeRef = FirebaseDatabase.getInstance().getReference().child("polices");

        policeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Polices> policeList = new ArrayList<>();

                for (DataSnapshot policeSnapshot : snapshot.getChildren()) {
                    Polices police = policeSnapshot.getValue(Polices.class);

                    // Calculate the distance between the user and the police station
                    double distanceToPolice = calculateDistance(currentLatitude, currentLongitude, police.getLatitude(), police.getLongitude());

                    // Add the distance to the police station object
                    police.setDistance(distanceToPolice);

                    policeList.add(police);
                }

                // Sort the police stations based on their distances (nearest to farthest)
                Collections.sort(policeList, new Comparator<Polices>() {
                    @Override
                    public int compare(Polices police1, Polices police2) {
                        return Double.compare(police1.getDistance(), police2.getDistance());
                    }
                });

                // Create and set the custom adapter for the ListView
                PoliceAdapter adapter = new PoliceAdapter(userPolices.this, policeList);
                policeStationList.setAdapter(adapter);

                // Set onItemClick listener for ListView items
                policeStationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected police station from the adapter
                        Polices selectedPolice = (Polices) parent.getItemAtPosition(position);

                        // Create an intent to start the userPolicesInfo activity
                        Intent intent = new Intent(userPolices.this, userPolicesInfo.class);
                        intent.putExtra("policeName", selectedPolice.getName());
                        intent.putExtra("policeDescription", selectedPolice.getDescription());
                        intent.putExtra("policeImage", selectedPolice.getImage());
                        intent.putExtra("policeNumber", selectedPolice.getNumber());
                        intent.putExtra("policeLong", selectedPolice.getLongitude());
                        intent.putExtra("policeLat", selectedPolice.getLatitude());

                        // Add other police station details to the intent if needed

                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error case
            }
        });
    }

    // Helper method to calculate the distance between two coordinates (latitude and longitude)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in kilometers
        double earthRadius = 6371;

        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the differences between the coordinates
        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;

        // Calculate the distance using the Haversine formula
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance in kilometers
        double distance = earthRadius * c;

        return distance;
    }

}
