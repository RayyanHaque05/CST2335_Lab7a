package com.cst2335.lab7;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://swapi.dev/api/people/?format=json";

    private ListView characterListView;
    private ProgressBar progressBar;
    private ArrayList<Character> characterList = new ArrayList<>();
    private CharacterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CST2335 Lab 7");
        }

        progressBar = findViewById(R.id.progress_bar);
        characterListView = findViewById(R.id.character_list_view);

        adapter = new CharacterAdapter();
        characterListView.setAdapter(adapter);

        // Handle item selection: Detect Phone vs Tablet
        characterListView.setOnItemClickListener((parent, view, position, id) -> {
            Character selectedCharacter = characterList.get(position);

            Bundle bundle = new Bundle();
            bundle.putString(DetailsFragment.ARG_NAME, selectedCharacter.getName());
            bundle.putString(DetailsFragment.ARG_HEIGHT, selectedCharacter.getHeight());
            bundle.putString(DetailsFragment.ARG_MASS, selectedCharacter.getMass());

            // Check if FrameLayout exists in layout (null on phone, present on tablet)
            View fragmentContainer = findViewById(R.id.fragment_container);

            if (fragmentContainer == null) {
                // PHONE MODE: Launch EmptyActivity to display DetailsFragment full screen
                Intent intent = new Intent(MainActivity.this, EmptyActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                // TABLET MODE: Replace FrameLayout beside ListView directly using FragmentManager
                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailsFragment)
                        .commit();
            }
        });

        // Query Star Wars API using AsyncTask
        new StarWarsQueryTask().execute(API_URL);
    }

    /**
     * AsyncTask to fetch characters from SWAPI on background thread.
     */
    private class StarWarsQueryTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    return builder.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception ignored) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            if (result != null) {
                try {
                    JSONObject rootObject = new JSONObject(result);
                    JSONArray resultsArray = rootObject.getJSONArray("results");

                    characterList.clear();
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject charObj = resultsArray.getJSONObject(i);
                        String name = charObj.optString("name", "Unknown");
                        String height = charObj.optString("height", "N/A");
                        String mass = charObj.optString("mass", "N/A");

                        characterList.add(new Character(name, height, mass));
                    }
                    adapter.notifyDataSetChanged();

                    // Tablet mode: Automatically display the first character
                    View fragmentContainer = findViewById(R.id.fragment_container);
                    if (fragmentContainer != null && !characterList.isEmpty()) {
                        characterListView.performItemClick(null, 0, adapter.getItemId(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, R.string.api_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.api_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Custom BaseAdapter to populate ListView with character names.
     */
    private class CharacterAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return characterList.size();
        }

        @Override
        public Character getItem(int position) {
            return characterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_character, parent, false);
            }

            TextView tvName = view.findViewById(R.id.tv_character_name);
            Character character = getItem(position);
            if (character != null) {
                tvName.setText(character.getName());
            }

            return view;
        }
    }
}
