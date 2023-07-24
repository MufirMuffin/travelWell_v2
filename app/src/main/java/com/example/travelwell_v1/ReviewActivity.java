package com.example.travelwell_v1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ReviewActivity extends AppCompatActivity {

    private ImageView ImgViewPlaceReview;
    private TextView placeNameTextView;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitReviewButton;
    private DatabaseReference reviewsRef;
    private String placeKey;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        reviewsRef = firebaseDatabase.getReference("reviews");

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);

        // Initialize views
        ImgViewPlaceReview = findViewById(R.id.ImgViewPlaceReview);
        placeNameTextView = findViewById(R.id.TxtViewPlaceReview);
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        // Retrieve data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String placeName = extras.getString("placeName");
            String imageURL = extras.getString("imageURL");
            placeKey = extras.getString("placeKey");

            // Set data to views
            placeNameTextView.setText(placeName);
            Picasso.get().load(imageURL).into(ImgViewPlaceReview);

            // Check if the user has already reviewed the place and fetch the review
            String userId = sharedPreferences.getString("loggedInUserId", "");
            if (!userId.isEmpty()) {
                fetchUserReview(userId, placeKey);
            }
        }

        // Handle submit button click
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        // Get the selected rating and comment
        int rating = Math.round(ratingBar.getRating());
        String comment = commentEditText.getText().toString().trim();

        // Validate the review
        if (rating == 0) {
            Toast.makeText(this, "Please rate the place", Toast.LENGTH_SHORT).show();
            return;
        }
        if (comment.isEmpty()) {
            Toast.makeText(this, "Please enter your comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user ID
        String userId = sharedPreferences.getString("loggedInUserId", "");
        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user has already reviewed the place
        checkUserReviewedPlace(userId, placeKey, new ReviewCheckListener() {
            @Override
            public void onReviewCheckResult(boolean hasReviewed) {
                if (hasReviewed) {
                    // User has already reviewed the place, update the existing review
                    updateReview(userId, rating, comment);
                } else {
                    // User has not reviewed the place, submit a new review
                    submitNewReview(userId, rating, comment);
                }
            }
        });
    }

    private void checkUserReviewedPlace(String userId, String placeKey, final ReviewCheckListener listener) {
        Query query = reviewsRef.child(placeKey).orderByChild("userID").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasReviewed = dataSnapshot.exists();
                listener.onReviewCheckResult(hasReviewed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
                listener.onReviewCheckResult(false);
            }
        });
    }
    private interface ReviewCheckListener {
        void onReviewCheckResult(boolean hasReviewed);
    }
    private void fetchUserReview(String userId, String placeKey) {
        Query query = reviewsRef.child(placeKey).orderByChild("userID").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User has already reviewed the place, fetch the review and display it
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);
                        if (review != null) {
                            ratingBar.setRating(review.getRating());
                            commentEditText.setText(review.getComment());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
            }
        });
    }

    private void submitNewReview(String userId, int rating, String comment) {
        // Create a new review object
        Review review = new Review(userId, rating, comment);

        // Save the review to the database
        reviewsRef.child(placeKey).push().setValue(review);

        // Show a success message
        Toast.makeText(ReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();

        // Finish the activity
        finish();
    }
    private void updateReview(String userId, int rating, String comment) {
        // Get the reference to the user's existing review
        Query query = reviewsRef.child(placeKey).orderByChild("userID").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update the existing review with the new rating and comment
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        String reviewKey = reviewSnapshot.getKey();
                        if (reviewKey != null) {
                            reviewsRef.child(placeKey).child(reviewKey).child("rating").setValue(rating);
                            reviewsRef.child(placeKey).child(reviewKey).child("comment").setValue(comment);
                        }
                    }

                    // Show a success message
                    Toast.makeText(ReviewActivity.this, "Review updated successfully", Toast.LENGTH_SHORT).show();

                    // Finish the activity
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
            }
        });
    }
}
