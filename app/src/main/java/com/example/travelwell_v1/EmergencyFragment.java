package com.example.travelwell_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class EmergencyFragment extends Fragment {

    private CardView cardViewPolice;
    private CardView cardViewHospital;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        cardViewPolice = view.findViewById(R.id.cardViewPolice);
        cardViewHospital = view.findViewById(R.id.cardViewHospital);

        // Set click listener for Police's CardView
        cardViewPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to userPolices activity
                Intent intent = new Intent(requireContext(), userPolices.class);
                startActivity(intent);
            }
        });

        // Set click listener for Hospital's CardView
        cardViewHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to userHospitals activity
                Intent intent = new Intent(requireContext(), userHospitals.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
