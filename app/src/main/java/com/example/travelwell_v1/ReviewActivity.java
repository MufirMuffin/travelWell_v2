package com.example.travelwell_v1;

import android.content.Intent;
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
    private Button submitReviewButton, deleteReviewButton;
    private DatabaseReference reviewsRef;
    private String placeID;
    private SharedPreferences sharedPreferences;
    private Review review; // Store the review object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize Database for review
        reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);

        // Initialize views
        ImgViewPlaceReview = findViewById(R.id.ImgViewPlaceReview);
        placeNameTextView = findViewById(R.id.TxtViewPlaceReview);
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        //TextView textViewPlaceID = findViewById(R.id.textViewPlaceID);
        deleteReviewButton = findViewById(R.id.deleteReviewButton);

        // Retrieve data from intent
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        int rating = intent.getIntExtra("rating", 0);
        String comment = intent.getStringExtra("comment");

        // Set the initial rating and comment
        ratingBar.setRating(rating);
        commentEditText.setText(comment);

        // Get the user's review reference to retrieve the placeID
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot reviewSnapshot : placeSnapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);

                        // Check if the review matches the given comment, rating, and userID
                        if (review != null &&
                                review.getComment().equals(comment) &&
                                review.getRating() == rating &&
                                review.getUserID().equals(userID)) {
                            placeID = placeSnapshot.getKey(); // Assign the value to the class-level variable
                            // Here, placeID will contain the dynamically retrieved placeID for "A Famosa"
                            // You can use the placeID as needed (e.g., show it in a TextView)
                            //textViewPlaceID.setText(placeID);
                            fetchPlaceDetails(placeID);
                            return; // Stop further iteration since we found the review
                        }
                    }
                }
                // Handle the case when the review does not exist for the given criteria
                Toast.makeText(ReviewActivity.this, "Review not found for the given criteria", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReviewActivity.this, "Error fetching reviews: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle submit button click
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReview(userID, rating);
            }
        });

        deleteReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReview(userID);
            }
        });
    }

    private void updateReview(String userId, int rating) {
        // Get the user's existing review reference
        Query query = reviewsRef.child(placeID).orderByChild("userID").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update the existing review with the new rating and comment
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        String reviewKey = reviewSnapshot.getKey();
                        if (reviewKey != null) {
                            reviewsRef.child(placeID).child(reviewKey).child("rating").setValue(rating);
                            reviewsRef.child(placeID).child(reviewKey).child("comment").setValue(commentEditText.getText().toString());
                        }
                    }

                    // Show a success message
                    Toast.makeText(ReviewActivity.this, "Review updated successfully", Toast.LENGTH_SHORT).show();

                    // Finish the activity
                    finish();
                } else {
                    // Handle the case when the review does not exist for the user
                    Toast.makeText(ReviewActivity.this, "Review not found for the user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
                Toast.makeText(ReviewActivity.this, "Error updating review: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchPlaceDetails(String placeID) {
        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference("places");
        placesRef.child(placeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Places place = dataSnapshot.getValue(Places.class);
                    if (place != null) {
                        Picasso.get().load(place.getImageURL()).into(ImgViewPlaceReview);
                        // Set the name of the place to placeNameTextView
                        placeNameTextView.setText(place.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Rest of the code remains the same
                Toast.makeText(ReviewActivity.this, "Error fetching place details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Method to delete the selected review
    private void deleteReview(String userId) {
        // Get the user's existing review reference
        Query query = reviewsRef.child(placeID).orderByChild("userID").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Delete the review
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        String reviewKey = reviewSnapshot.getKey();
                        if (reviewKey != null) {
                            reviewsRef.child(placeID).child(reviewKey).removeValue();
                        }
                    }

                    // Show a success message
                    Toast.makeText(ReviewActivity.this, "Review deleted successfully", Toast.LENGTH_SHORT).show();

                    // Finish the activity
                    finish();
                } else {
                    // Handle the case when the review does not exist for the user
                    Toast.makeText(ReviewActivity.this, "Review not found for the user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
                Toast.makeText(ReviewActivity.this, "Error deleting review: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
