package com.example.travelwell_v1;

import static com.example.travelwell_v1.NearbyPlacesCalculator.calculateNearbyPlaces;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView textName;
    ListView listPlaces;
    Button mapViewButton, getLocationButton, emergencyButton;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Location previousLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private double currentLatitude;
    private double currentLongitude;
    private double maxDistance = 5000; // Maximum distance for nearby places
    private int lastSelectedItemIndex = -1;

    private SharedPreferences sharedPreferences;
    private Map<String, List<Review>> reviewsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textName = findViewById(R.id.textName);
        listPlaces = findViewById(R.id.listPlaces);
        mapViewButton = findViewById(R.id.mapViewButton);
        getLocationButton = findViewById(R.id.getLocationButton);
        emergencyButton = findViewById(R.id.emergencyButton);

        //sharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String loggedInUser = getLoggedInUser();
        textName.setText(loggedInUser);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    // Use the obtained latitude and longitude for your desired functionality
                    //Toast.makeText(MainActivity.this, "Latitude: " + currentLatitude + ", Longitude: " + currentLongitude, Toast.LENGTH_SHORT).show();

                    // Call the method to calculate nearby places with the updated latitude and longitude
                    calculateAndDisplayNearbyPlaces();
                }
            }
        };

        String username = getIntent().getStringExtra("username");
        textName = findViewById(R.id.textName);
        textName.setText(username);


        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdates();
            }
        });


        MapsInitializer.initialize(this);

        //ReviewRef
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewsMap = new HashMap<>();

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String placeKey = reviewSnapshot.getKey();
                    List<Review> placeReviews = new ArrayList<>();

                    for (DataSnapshot childSnapshot : reviewSnapshot.getChildren()) {
                        Review review = childSnapshot.getValue(Review.class);
                        placeReviews.add(review);
                    }

                    reviewsMap.put(placeKey, placeReviews);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //PlaceRef
        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");
        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Places> placesList = new ArrayList<>();

                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()){
                    String placeKey = placeSnapshot.getKey();
                    Places places = placeSnapshot.getValue(Places.class);
                    places.setPlaceKey(placeKey); //kiv
                    placesList.add(places);
                }

                // Calculate and display nearby places based on current location
                calculateAndDisplayNearbyPlaces();

                listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected place from the adapter
                        Places selectedPlace = (Places) parent.getItemAtPosition(position);

                        // Retrieve the place key indirectly
                        String placeKey = selectedPlace.getPlaceKey(); // Assuming placeKey is a public field

                        // Create intent to start the PlacesInfo activity
                        Intent intent = new Intent(MainActivity.this, userPlacesInfo.class);
                        intent.putExtra("placeKey", placeKey);
                        intent.putExtra("placeName", selectedPlace.getName());
                        intent.putExtra("placeDescription", selectedPlace.getDescription());
                        intent.putExtra("imageURL", selectedPlace.getImageURL());
                        intent.putExtra("placeLong",selectedPlace.getLongitude());
                        intent.putExtra("placeLat",selectedPlace.getLatitude());
                        // Add other place details to the intent if needed

                        // Assuming reviewsMap is a Map<String, List<Review>> containing the reviews for each place
                        List<Review> reviewsList = reviewsMap.get(selectedPlace.getPlaceKey());

                        if (reviewsList != null && !reviewsList.isEmpty()) {
                            intent.putExtra("reviewsList", new ArrayList<>(reviewsList));
                        }

                        startActivity(intent);
                        lastSelectedItemIndex = position; // Update the lastSelectedItemIndex
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mapViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, MapView.class);
                startActivity(intent);
            }
        });

        textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, userEmergencyServices.class);
                startActivity(intent);
            }
        });

    }

    private void calculateAndDisplayNearbyPlaces() {
        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");

        placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Places> placesList = new ArrayList<>();

                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()){
                    String placeKey = placeSnapshot.getKey();
                    Places places = placeSnapshot.getValue(Places.class);
                    places.setPlaceKey(placeKey);
                    placesList.add(places);
                }

                reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, List<Review>> reviewsMap = new HashMap<>();

                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()){
                            String placeKey = reviewSnapshot.getKey();
                            List<Review> placeReviews = new ArrayList<>();

                            for (DataSnapshot childSnapshot : reviewSnapshot.getChildren()){
                                Review review = childSnapshot.getValue(Review.class);
                                placeReviews.add(review);
                            }
                            reviewsMap.put(placeKey,placeReviews);
                        }

                        // Calculate nearby places based on current location
                        List<Places> nearbyPlaces = calculateNearbyPlaces(placesList, currentLatitude, currentLongitude, maxDistance);

                        // Sort the nearby places based on distance
                        Collections.sort(nearbyPlaces, new Comparator<Places>() {
                            @Override
                            public int compare(Places place1, Places place2) {
                                // Calculate the distance between current location and each place
                                double distance1 = calculateDistance(currentLatitude, currentLongitude, place1.getLatitude(), place1.getLongitude());
                                double distance2 = calculateDistance(currentLatitude, currentLongitude, place2.getLatitude(), place2.getLongitude());

                                // Compare the distances
                                return Double.compare(distance1, distance2);
                            }
                        });

                        // Create and set the custom adapter for the ListView with nearby places
                        CustomPlaceAdapter adapter = new CustomPlaceAdapter(MainActivity.this, nearbyPlaces, currentLatitude, currentLongitude, reviewsMap);
                        listPlaces.setAdapter(adapter);

                        // Restore the last selected item if it exists
                        if (lastSelectedItemIndex != -1) {
                            listPlaces.setSelection(lastSelectedItemIndex);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
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

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    //sharedPreferences
    private String getLoggedInUser() {
        return sharedPreferences.getString("loggedInUser", "");
    }
}
