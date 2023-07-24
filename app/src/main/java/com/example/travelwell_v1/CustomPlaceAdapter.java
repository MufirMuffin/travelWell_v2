package com.example.travelwell_v1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomPlaceAdapter extends ArrayAdapter<Places> {
    private Context context;
    private List<Places> placesList;
    private double currentLatitude;
    private double currentLongitude;
    private Map<String, List<Review>> reviewsMap;

    public CustomPlaceAdapter(Context context, List<Places> placesList, double currentLatitude, double currentLongitude, Map<String, List<Review>> reviewsMap) {
        super(context, 0, placesList);
        this.context = context;
        this.placesList = placesList;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.reviewsMap = reviewsMap;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_place2, parent, false);
        }

        Places place = placesList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.placeNameTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.placeDescTextView);
        TextView distanceTextView = convertView.findViewById(R.id.placeDistanceTextView);
        TextView totalRatingTextView = convertView.findViewById(R.id.totalRatingTextView);
        ImageView placeImageView = convertView.findViewById(R.id.placeImageView);

        nameTextView.setText(place.getName());
        descriptionTextView.setText(place.getDescription());
        // Load Image using Picasso
        Picasso.get().load(place.getImageURL()).into(placeImageView);

        double distance = calculateDistance(currentLatitude, currentLongitude, place.getLatitude(), place.getLongitude());
        distanceTextView.setText(String.format("%.2f km", distance));

        //calculate total rating
        List<Review> placeReviews = reviewsMap.get(place.getPlaceKey());
        double totalRating=0;
        if (placeReviews != null && !placeReviews.isEmpty()){
            for (Review review : placeReviews){
                totalRating += review.getRating();
            }
            double averageRating = totalRating/placeReviews.size();
            totalRatingTextView.setText(String.format(Locale.getDefault(),"%.1f",averageRating));
        }

        return convertView;
    }

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
