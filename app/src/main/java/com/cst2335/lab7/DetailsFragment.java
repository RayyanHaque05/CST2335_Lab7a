package com.cst2335.lab7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {

    public static final String ARG_NAME = "NAME";
    public static final String ARG_HEIGHT = "HEIGHT";
    public static final String ARG_MASS = "MASS";

    private TextView tvName;
    private TextView tvHeight;
    private TextView tvMass;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        tvName = view.findViewById(R.id.tv_name);
        tvHeight = view.findViewById(R.id.tv_height);
        tvMass = view.findViewById(R.id.tv_mass);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString(ARG_NAME, "fill_me");
            String height = args.getString(ARG_HEIGHT, "fill_me");
            String mass = args.getString(ARG_MASS, "fill_me");

            tvName.setText(name);
            tvHeight.setText(height);
            tvMass.setText(mass);
        }

        return view;
    }
}
