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

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Places> {

    public ListAdapter(Context context, ArrayList<Places> placesArrayList){

        super(context, R.layout.list_item_place,placesArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Places places = getItem(position);

        if(convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_place,parent,false);
        }

        //TextView imageView = convertView.findViewById(R.id.placeImageView);
        TextView placeName = convertView.findViewById(R.id.placeNameTextView);
        TextView placeDesc = convertView.findViewById(R.id.placeDescTextView);

        //imageView.setText(places.imageURL);
        placeName.setText(places.getName());
        placeDesc.setText(places.getDescription());

        return convertView;
    }
}
