package com.example.travelwell_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PoliceAdapter extends ArrayAdapter<Polices> {

    public PoliceAdapter(Context context, List<Polices> policesList) {
        super(context, 0, policesList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_police, parent, false);
        }

        Polices currentPolice = getItem(position);

        ImageView policeImageView = listItemView.findViewById(R.id.policeImageView);
        TextView policeNameTextView = listItemView.findViewById(R.id.policeNameTextView);
        TextView policeDescTextView = listItemView.findViewById(R.id.policeDescTextView);

        // Check if the image path is empty or null
        if (currentPolice.getImage() != null && !currentPolice.getImage().isEmpty()) {
            Picasso.get().load(currentPolice.getImage()).into(policeImageView);
        }

        policeNameTextView.setText(currentPolice.getName());
        policeDescTextView.setText(currentPolice.getDescription());

        return listItemView;
    }
}
