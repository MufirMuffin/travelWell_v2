package com.example.travelwell_v1;

import static android.content.Context.MODE_PRIVATE;
import static com.example.travelwell_v1.NearbyPlacesCalculator.calculateNearbyPlaces;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

public class AttractionPlacesFragment extends Fragment {

    private ListView listPlaces;
    private Button mapViewButton, getLocationButton, emergencyButton;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attraction_places, container, false);

        listPlaces = view.findViewById(R.id.listPlaces);
        mapViewButton = view.findViewById(R.id.mapViewButton);
        getLocationButton = view.findViewById(R.id.getLocationButton);
        emergencyButton = view.findViewById(R.id.emergencyButton);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

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

                    // Call the method to calculate nearby places with the updated latitude and longitude
                    calculateAndDisplayNearbyPlaces();
                }
            }
        };

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", MODE_PRIVATE);

        MapsInitializer.initialize(requireContext());

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

                calculateAndDisplayNearbyPlaces();

                listPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Places selectedPlace = (Places) parent.getItemAtPosition(position);
                        String placeKey = selectedPlace.getPlaceKey();

                        Intent intent = new Intent(requireContext(), userPlacesInfo.class);
                        intent.putExtra("placeKey", placeKey);
                        intent.putExtra("placeName", selectedPlace.getName());
                        intent.putExtra("placeDescription", selectedPlace.getDescription());
                        intent.putExtra("imageURL", selectedPlace.getImageURL());
                        intent.putExtra("placeLong", selectedPlace.getLongitude());
                        intent.putExtra("placeLat", selectedPlace.getLatitude());

                        List<Review> reviewsList = reviewsMap.get(selectedPlace.getPlaceKey());
                        if (reviewsList != null && !reviewsList.isEmpty()) {
                            intent.putExtra("reviewsList", new ArrayList<>(reviewsList));
                        }

                        startActivity(intent);
                        lastSelectedItemIndex = position;
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
                Intent intent = new Intent(requireContext(), MapView.class);
                startActivity(intent);
            }
        });

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdates();
            }
        });

        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), userEmergencyServices.class);
                startActivity(intent);
            }
        });

        return view;
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

                        List<Places> nearbyPlaces = calculateNearbyPlaces(placesList, currentLatitude, currentLongitude, maxDistance);

                        Collections.sort(nearbyPlaces, new Comparator<Places>() {
                            @Override
                            public int compare(Places place1, Places place2) {
                                double distance1 = calculateDistance(currentLatitude, currentLongitude, place1.getLatitude(), place1.getLongitude());
                                double distance2 = calculateDistance(currentLatitude, currentLongitude, place2.getLatitude(), place2.getLongitude());
                                return Double.compare(distance1, distance2);
                            }
                        });

                        CustomPlaceAdapter adapter = new CustomPlaceAdapter(requireContext(), nearbyPlaces, currentLatitude, currentLongitude, reviewsMap);
                        listPlaces.setAdapter(adapter);

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

            }
        });
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return distance;
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
                Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
