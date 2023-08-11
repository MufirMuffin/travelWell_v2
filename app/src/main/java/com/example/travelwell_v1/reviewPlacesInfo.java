package com.example.travelwell_v1;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class reviewPlacesInfo extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ListView userReviewList;
    private List<Review> reviewList;
    private DatabaseReference reviewsRef;
    private Button newReviewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_places_info);

        userReviewList = findViewById(R.id.userReviewList);
        reviewList = new ArrayList<>();
        newReviewBtn = findViewById(R.id.newReviewBtn);

        // Retrieve the user ID from SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String loggedInUser = getLoggedInUser();

        //Review
        reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");
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

        userReviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the position
                Review clickedReview = reviewList.get(position);

                // Redirect to ReviewActivity to update the review
                Intent intent = new Intent(reviewPlacesInfo.this, ReviewActivity.class);
                intent.putExtra("reviewID", clickedReview.getReviewID());
                intent.putExtra("userID", clickedReview.getUserID());
                intent.putExtra("rating", clickedReview.getRating());
                intent.putExtra("comment", clickedReview.getComment());
                startActivity(intent);
            }
        });

        newReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reviewPlacesInfo.this, userAddReview.class);
                startActivity(intent);
            }
        });
    }

    // Shared Preferences
    private String getLoggedInUser() {
        return sharedPreferences.getString("loggedInUser", "");
    }
}
