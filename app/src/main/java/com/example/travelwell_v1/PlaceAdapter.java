package com.example.travelwell_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlaceAdapter extends ArrayAdapter<Places> {

    public PlaceAdapter(Context context, List<Places> places) {
        super(context, 0, places);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_place, parent, false);
        }

        // Get the current place object
        Places places = getItem(position);

        // Set the place name and description in the respective TextViews
        TextView nameTextView = convertView.findViewById(R.id.placeNameTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.placeDescTextView);
        ImageView imageView = convertView.findViewById(R.id.placeImageView);

        nameTextView.setText(places.getName());
        descriptionTextView.setText(places.getDescription());
        //Load Image using Picasso
        Picasso.get().load(places.getImageURL()).into(imageView);

        return convertView;
    }
}

