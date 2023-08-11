package com.example.travelwell_v1;

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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class addReview extends AppCompatActivity {

    private ImageView ImgViewPlaceReview;
    private TextView placeNameTextView;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitReviewButton;
    private DatabaseReference reviewsRef;
    private String placeID;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // Get the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
            int rating = extras.getInt("rating");
            String comment = extras.getString("comment");
            placeID = extras.getString("placeID");

            // Initialize Database for reviews
            reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

            // Initialize views
            ImgViewPlaceReview = findViewById(R.id.ImgViewPlaceReview);
            placeNameTextView = findViewById(R.id.TxtViewPlaceReview);
            ratingBar = findViewById(R.id.ratingBar);
            commentEditText = findViewById(R.id.commentEditText);
            submitReviewButton = findViewById(R.id.submitReviewButton);

            // Set the initial rating and comment
            ratingBar.setRating(rating);
            commentEditText.setText(comment);

            // Fetch place details from the database using the placeID
            fetchPlaceDetails(placeID);

            // Check if the user has already reviewed the place
            checkUserReview();

            // Handle submit button click
            submitReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newRating = (int) ratingBar.getRating();
                    String newComment = commentEditText.getText().toString();
                    addNewReview(userID, newRating, newComment);
                }
            });
        }
    }

    private void checkUserReview() {
        DatabaseReference placeReviewsRef = reviewsRef.child(placeID);

        placeReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasReviewed = false;
                String reviewKey = "";

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null && review.getUserID().equals(userID)) {
                        // User has already reviewed the place
                        hasReviewed = true;
                        reviewKey = reviewSnapshot.getKey();
                        break;
                    }
                }

                if (hasReviewed) {
                    // User already has a review, finish the activity to prevent updating
                    finish();
                    // You can also show a message to inform the user that they can't update the review.
                    Toast.makeText(addReview.this, "You have already reviewed this place.", Toast.LENGTH_SHORT).show();
                } else {
                    // User does not have a review, continue with adding a new review
                    submitReviewButton.setEnabled(true);
                    submitReviewButton.setText("Submit Review");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
                Toast.makeText(addReview.this, "Error checking user review: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addNewReview(String userID, int rating, String comment) {
        //Validation
        if (rating == 0) {
            // Show a message to the user that they need to provide a rating
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            // Show a message to the user that they need to provide a comment
            Toast.makeText(this, "Please provide a comment", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a new Review object with the provided rating and comment
        Review newReview = new Review(userID, rating, comment);

        // Get a new unique key for the new review
        String newReviewKey = reviewsRef.child(placeID).push().getKey();

        // Save the new review to the database
        reviewsRef.child(placeID).child(newReviewKey).setValue(newReview, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Show a success message
                    Toast.makeText(addReview.this, "New review added successfully", Toast.LENGTH_SHORT).show();
                    // Finish the activity
                    finish();
                } else {
                    // Handle the error case
                    Toast.makeText(addReview.this, "Error adding new review: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
                        // Load place image and set the name of the place to placeNameTextView
                        Picasso.get().load(place.getImageURL()).into(ImgViewPlaceReview);
                        placeNameTextView.setText(place.getName());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
                Toast.makeText(addReview.this, "Error fetching place details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
