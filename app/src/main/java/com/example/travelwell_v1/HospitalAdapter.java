package com.example.travelwell_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class HospitalAdapter extends BaseAdapter {
    private Context context;
    private List<Hospitals> hospitalsList;

    public HospitalAdapter(Context context, List<Hospitals> hospitalsList) {
        this.context = context;
        this.hospitalsList = hospitalsList;
    }

    @Override
    public int getCount() {
        return hospitalsList.size();
    }

    @Override
    public Object getItem(int position) {
        return hospitalsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_hospital, parent, false);
        }

        Hospitals hospital = hospitalsList.get(position);

        ImageView hospitalImageView = convertView.findViewById(R.id.hospitalImageView);
        TextView hospitalNameTextView = convertView.findViewById(R.id.hospitalNameTextView);
        TextView hospitalDescTextView = convertView.findViewById(R.id.hospitalDescTextView);
        TextView distanceTextView = convertView.findViewById(R.id.distanceTextView); // Add this line for the distance

        // Set the data to the views
        Picasso.get().load(hospital.getImage()).into(hospitalImageView);
        hospitalNameTextView.setText(hospital.getName());
        hospitalDescTextView.setText(hospital.getDescription());
        distanceTextView.setText(String.format(Locale.getDefault(), "%.2f km", hospital.getDistance())); // Set the distance with formatting

        return convertView;
    }
}

