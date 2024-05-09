import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;


import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class PlantsActivity extends AppCompatActivity {

    // Declare a variable to store the original list of plants
    private List<Plant> originalPlantsList;

    // Declare RecyclerView as a class-level variable
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_guide);

        // Set up the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display "Hello" in the ActionBar
       // getSupportActionBar().setTitle("Plant Guide");

        // Set up the SearchView
        SearchView searchView = findViewById(R.id.searchView);
        setupSearchView(searchView);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Load plants from JSON file and store the original list
        originalPlantsList = loadPlantsFromJson("plants.json");

        if (originalPlantsList != null) {
            // Create and set the adapter
            PlantAdapter plantAdapter = new PlantAdapter(originalPlantsList);
            recyclerView.setAdapter(plantAdapter);
        } else {
            // Handle the case where loading plants failed
            Toast.makeText(this, "Failed to load plants", Toast.LENGTH_SHORT).show();
        }

    }



    private void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the submission of the search query
                // Perform the search or any other action you desire
                Toast.makeText(PlantsActivity.this, query, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PlantsActivity.this, newText, Toast.LENGTH_SHORT).show();
                return true;
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
        PlantAdapter plantAdapter = new PlantAdapter(plants);
        recyclerView.setAdapter(plantAdapter);
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





}