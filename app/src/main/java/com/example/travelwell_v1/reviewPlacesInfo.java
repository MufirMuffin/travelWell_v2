package com.example.travelwell_v1;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class reviewPlacesInfo extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ListView userReviewList;
    private List<Review> reviewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_places_info);

        userReviewList = findViewById(R.id.userReviewList);
        reviewList = new ArrayList<>();


        // Retrieve the user ID from SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String loggedInUser = getLoggedInUser();

        //
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot userReviewSnapshot : reviewSnapshot.getChildren()) {
                        Review review = userReviewSnapshot.getValue(Review.class);
                        if (review != null && review.getUserID().equals(loggedInUser)) {
                            reviewList.add(review);
                        }
                    }
                }
                // Create and set the custom ArrayAdapter to populate the ListView
                ReviewAdapter2 reviewAdapter2 = new ReviewAdapter2(reviewPlacesInfo.this, reviewList);
                userReviewList.setAdapter(reviewAdapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //sharedPreferences
    private String getLoggedInUser() {
        return sharedPreferences.getString("loggedInUser", "");
    }
}