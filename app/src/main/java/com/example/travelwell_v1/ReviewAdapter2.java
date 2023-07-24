package com.example.travelwell_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter2 extends ArrayAdapter<Review> {
    private Context context;
    private List<Review> reviews;
    public ReviewAdapter2(Context context, List<Review> reviews) {

        super(context, 0, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the review for this position
        Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        }

        // Find views within the layout
        TextView reviewUserTextView = convertView.findViewById(R.id.reviewUserTextView);
        TextView reviewCommentTextView = convertView.findViewById(R.id.reviewCommentTextView);
        TextView reviewRatingTextView = convertView.findViewById(R.id.reviewRatingTextView);

        // Set the review details in the views
        reviewUserTextView.setText(review.getUserID());
        reviewCommentTextView.setText(review.getComment());
        reviewRatingTextView.setText(String.valueOf(review.getRating()));

        return convertView;
    }

}
