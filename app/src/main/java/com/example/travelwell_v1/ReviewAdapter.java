package com.example.travelwell_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ReviewAdapter extends ArrayAdapter<Places> {

    public ReviewAdapter(Context context, List<Places> places) {
        super(context,0,places);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review_place, parent,false);
        }

        //Get current place object
        Places places = getItem(position);

        //set place name
        TextView nameTextView = convertView.findViewById(R.id.ReviewplaceNameTextView);
        ImageView imageView = convertView.findViewById(R.id.ReviewplaceImageView);

        nameTextView.setText(places.getName());
        Picasso.get().load(places.getImageURL()).into(imageView);

        return convertView;
    }
}
