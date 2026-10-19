package com.cst2335.lab7;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CST2335 Lab 7");
        }

        if (savedInstanceState == null) {
            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
