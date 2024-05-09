package com.example.plantsblooms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlantGuideActivity extends AppCompatActivity {

    // Declare a variable to store the original list of plants
    private List<Plant> originalPlantsList;

    // Declare RecyclerView as a class-level variable
    private RecyclerView recyclerView;

    private ImageView backArrow;

    private TextView title;

    private ImageButton profileButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_guide);


        // Find the ImageButton with ID home
        ImageButton homeButton = findViewById(R.id.home);

        // Set an OnClickListener for the homeButton
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to PlantsActivityHome
                Intent intent = new Intent(PlantGuideActivity.this, PlantsActivityHome.class);
                // Start the PlantsActivityHome
                startActivity(intent);
                finish();
            }
        });

        profileButton = findViewById(R.id.profile);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopupMenu(v);
            }
        });
        // Get user email from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user_email")) {
            String userEmail = intent.getStringExtra("user_email");
            // Use userEmail as needed (e.g., displaying in the profile menu)
            Toast.makeText(PlantGuideActivity.this, "User Email: " + userEmail, Toast.LENGTH_SHORT).show();
        }

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopupMenu(v);
            }
        });

        // Set up the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantGuideActivity.this, PlantsActivityHome.class);
                startActivity(intent);
                finish();
            }
        });

        // Set click listener to the toolbar's back arrow
         backArrow = findViewById(R.id.back_arrow);

        // Set click listener to the toolbar's title
         title = findViewById(R.id.toolbar_title);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantGuideActivity.this, PlantsActivityHome.class);
                startActivity(intent);
                finish();
            }
        });


        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantGuideActivity.this, PlantsActivityHome.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up the SearchView
        SearchView searchView = findViewById(R.id.searchView);
        setupSearchView(searchView);



        // Get the filter information from the intent
        String filter = getIntent().getStringExtra("filter");

        Log.d("FilterValue", "Filter value receivedddddddddddddddddddddddddd: " + filter);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        originalPlantsList = loadPlantsFromJson("plants.json");

        if (originalPlantsList != null) {
            if (filter != null && !filter.isEmpty()) {
                filterAndShowPlantss(filter);
            }

            // Create and set the adapter
            PlantAdapter plantAdapter = new PlantAdapter(originalPlantsList, this); // Passing Context
            recyclerView.setAdapter(plantAdapter);
        } else {
            // Handle the case where loading plants failed
            Toast.makeText(this, "Failed to load plants", Toast.LENGTH_SHORT).show();
        }



        // Set up Top Pick button to show three random plants if no filter is specified
        Button buttonTopPick = findViewById(R.id.buttonTopPick);
        buttonTopPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTopPicks();
            }
        });

        // Set up Indoor button to show indoor plants
        Button buttonIndoor = findViewById(R.id.buttonIndoor);
        buttonIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterAndShowPlants("Indoor");
            }
        });

        // Set up Outdoor button to show outdoor plants
        Button buttonOutdoor = findViewById(R.id.buttonOutdoor);
        buttonOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterAndShowPlants("Outdoor");
            }
        });
    }

    private void setupSearchView(androidx.appcompat.widget.SearchView searchView) {
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the submission of the search query
                // Perform the search or any other action you desire
                Toast.makeText(PlantGuideActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle changes in the search query text
                // Filter the original list based on the entered text
                List<Plant> filteredPlants = filterPlants(originalPlantsList, newText);

                // Update the RecyclerView with the filtered list
                updateRecyclerView(filteredPlants);
                // You may want to filter your data based on the new text
                Toast.makeText(PlantGuideActivity.this, newText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false); // Expand the SearchView when clicked
            }
        });
    }

    // Helper method to filter the plant list based on the entered text
    private List<Plant> filterPlants(List<Plant> plants, String query) {
        List<Plant> filteredList = new ArrayList<>();

        for (Plant plant : plants) {
            // Check if the plant name contains the query (case-insensitive)
            if (plant.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(plant);
            }
        }

        return filteredList;
    }

    // Helper method to update the RecyclerView with a new list of plants
    private void updateRecyclerView(List<Plant> plants) {
        PlantAdapter plantAdapter = new PlantAdapter(plants, this); // Passing Context
        recyclerView.setAdapter(plantAdapter);
    }


    // Helper method to show three random plants
    private void showTopPicks() {
        List<Plant> topPicks = new ArrayList<>();
        Random random = new Random();

        // Shuffle the original list
        List<Plant> shuffledList = new ArrayList<>(originalPlantsList);
        Collections.shuffle(shuffledList);

        // Add three random plants to the top picks list
        for (int i = 0; i < Math.min(3, shuffledList.size()); i++) {
            topPicks.add(shuffledList.get(i));
        }

        // Update the RecyclerView with the top picks
        updateRecyclerView(topPicks);
    }

    // Helper method to filter and show plants based on type (Indoor/Outdoor)
    private void filterAndShowPlants(String plantType) {
        List<Plant> filteredPlants = new ArrayList<>();

        for (Plant plant : originalPlantsList) {
            if (plantType.equalsIgnoreCase(plant.getType())) {
                filteredPlants.add(plant);
            }
        }

        // Update the RecyclerView with filtered plants
        updateRecyclerView(filteredPlants);
    }

    private void filterAndShowPlantss(String filter) {
        List<Plant> filteredPlants = new ArrayList<>();

        for (Plant plant : originalPlantsList) {
            if (filter.equalsIgnoreCase(plant.getType())) {
                filteredPlants.add(plant);
            }
        }

        // Update the RecyclerView with filtered plants
        updateRecyclerView(filteredPlants);
    }

    public List<Plant> loadPlantsFromJson(String jsonFilePath) {
        List<Plant> plants = null;

        try {
            // Use AssetManager to open the file
            InputStream inputStream = getAssets().open(jsonFilePath);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use Gson to parse the JSON file and return a list of plants
            Gson gson = new Gson();
            Type plantListType = new TypeToken<List<Plant>>() {}.getType();
            plants = gson.fromJson(reader, plantListType);

            // Close the streams
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return plants;
    }

    private void showProfilePopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        SharedPreferences sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userName = sharedPref.getString("user_name", ""); // Retrieve the user's name using the key "user_name"
        // Inflate the menu items
        popupMenu.getMenu().add(0, R.id.action_view_profile, 0, "View Profile");
        popupMenu.getMenu().add(0, R.id.action_logout, 1, "Logout");
        // Add an event listener to handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks using if-else statements
                if (item.getItemId() == R.id.action_view_profile) {
                    // Implement the action to view the user profile
                    Toast.makeText(PlantGuideActivity.this, "View Profile (" + userName + ")", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    // Implement the action to log out
                    Toast.makeText(PlantGuideActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    logout();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void logout() {
        // Clear user session or perform logout actions
        // For example, you can navigate to the sign-in page after logging out
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to remove it from the stack
    }



}
