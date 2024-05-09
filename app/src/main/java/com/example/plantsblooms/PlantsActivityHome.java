
package com.example.plantsblooms;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class PlantsActivityHome extends AppCompatActivity implements View.OnClickListener {
    private CardView guideCard, cameraCard, blogsCard;
    private File photoFile;
    private GridLayout buttonsLayout;
    public ImageView indoorButton, outdoorButton ,imag;
    private ImageButton profileButton;
    private List<Plant> plantList;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 200;
    private RecyclerView recyclerView;
    private List<Plant> originalPlantsList;
    private PlantAdapter plantAdapter;
    private TextView txt;
    private HorizontalScrollView hh;
    ActivityResultLauncher<Intent> launcher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                if(result.getResultCode()==RESULT_OK){
                    Uri uri=result.getData().getData();
                    Intent intent = new Intent(this , EditImg.class);
                    intent.putExtra("imageUri",uri);
                    startActivity(intent);
                }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                    Toast.makeText(this , "error" , Toast.LENGTH_SHORT).show();
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plants);
        Toolbar toolbar = findViewById(R.id.toolbar);
        guideCard = findViewById(R.id.guideCard);
        cameraCard = findViewById(R.id.cameraCard);
        blogsCard = findViewById(R.id.blogsCard);
        indoorButton = findViewById(R.id.indoorButton);
        outdoorButton = findViewById(R.id.outdoorButton);
        profileButton = findViewById(R.id.profile);
        loadPlantDataFromJson();
        guideCard.setOnClickListener(this);
        cameraCard.setOnClickListener(this);
        blogsCard.setOnClickListener(this);
        indoorButton.setOnClickListener(this);
        outdoorButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        String filter = getIntent().getStringExtra("filter");
        recyclerView = findViewById(R.id.recyclerView);
        buttonsLayout = findViewById(R.id.buttonsLayout);
        imag = findViewById(R.id.imag);
        txt = findViewById(R.id.txt);
        hh = findViewById(R.id.hh);
        SearchView searchView = findViewById(R.id.searchView);
        setupSearchView(searchView);
        ImageButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantsActivityHome.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });

        plantAdapter = new PlantAdapter(plantList, this); // Assuming "this" refers to the context
        recyclerView.setAdapter(plantAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        originalPlantsList = loadPlantsFromJson("plants.json");
        if (originalPlantsList != null) {
            if (filter != null && !filter.isEmpty()) {
                filterAndShowPlantss(filter);
            }
            PlantAdapter plantAdapter = new PlantAdapter(originalPlantsList, this);
            recyclerView.setAdapter(plantAdapter);
        } else {
            Toast.makeText(this, "Failed to load plants", Toast.LENGTH_SHORT).show();
        }
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopupMenu(v);
            }
        });
        SharedPreferences sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userName = sharedPref.getString("user_name", ""); // Retrieve the user's name using the key "user_name"
            Toast.makeText(PlantsActivityHome.this, "Hello: " + userName, Toast.LENGTH_SHORT).show();
        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
        guideCard.setOnClickListener(this);
        blogsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantsActivityHome.this, Blogs.class);
                startActivity(intent);
            }
        });
        indoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlantGuideActivity("Indoor");
            }
        });
        outdoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlantGuideActivity("Outdoor");
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopupMenu(v);
            }
        });

        imag.setOnClickListener(v -> {
            Intent intent = new Intent(PlantsActivityHome.this,com.example.plantsblooms.AugmentedRealityActivity.class);
            startActivity(intent);
        });
    }
    private void setupSearchView(androidx.appcompat.widget.SearchView searchView) {
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(PlantsActivityHome.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            private void updateRecyclerView(List<Plant> plants, Context context) {
                PlantAdapter plantAdapter = new PlantAdapter(plants, context);
                recyclerView.setAdapter(plantAdapter);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.smoothScrollToPosition(0);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Plant> filteredPlants = filterPlants(originalPlantsList, newText);
                updateRecyclerView(filteredPlants, PlantsActivityHome.this); // Pass the context
                Toast.makeText(PlantsActivityHome.this, newText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.VISIBLE);
                imag.setVisibility(View.VISIBLE);
                txt.setVisibility(View.VISIBLE);
                hh.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false); // Expand the SearchView when clicked
                buttonsLayout.setVisibility(View.GONE);
                imag.setVisibility(View.GONE);
                txt.setVisibility(View.GONE);
                hh.setVisibility(View.GONE);
            }
        });
    }
    private List<Plant> filterPlants(List<Plant> plants, String query) {
        List<Plant> filteredList = new ArrayList<>();

        for (Plant plant : plants) {
            if (plant.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(plant);
            }
        }
        return filteredList;
    }
    private void updateRecyclerView(List<Plant> plants) {
        PlantAdapter plantAdapter = new PlantAdapter(plants, this);
        recyclerView.setAdapter(plantAdapter);
    }
    public List<Plant> loadPlantsFromJson(String jsonFilePath) {
        List<Plant> plants = null;

        try {
            InputStream inputStream = getAssets().open(jsonFilePath);
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new Gson();
            Type plantListType = new TypeToken<List<Plant>>() {}.getType();
            plants = gson.fromJson(reader, plantListType);
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return plants;
    }
    private void filterAndShowPlantss(String filter) {
        List<Plant> filteredPlants = new ArrayList<>();

        for (Plant plant : originalPlantsList) {
            if (filter.equalsIgnoreCase(plant.getType())) {
                filteredPlants.add(plant);
            }
        }
        updateRecyclerView(filteredPlants);
    }
    private void loadPlantDataFromJson() {
        // Read JSON file from assets folder
        try {
            InputStream inputStream = getAssets().open("plants.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");
            // Parse JSON using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Plant>>() {}.getType();
            plantList = gson.fromJson(json, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void openPlantGuideActivity(String filter) {
        Intent intent = new Intent(this, PlantGuideActivity.class);
        intent.putExtra("filter", filter);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity to remove it from the stack
    }
    //////////////////////////////////////////////////////////////////////////////////////////////


    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }
    void openCamera()
    {
        ImagePicker
                .Companion
                .with(this)
                .bothCameraGallery()
                .createIntentFromDialog(new Function1(){
                    public Object invoke(Object var1){
                        this.invoke((Intent)var1);
                        return Unit.INSTANCE;
                    }
                    public void invoke(@NotNull Intent it){
                        Intrinsics.checkNotNullParameter(it,"it");
                        launcher.launch(it);
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, open the camera
                openCamera();
            } else {
                // Camera permission denied, show a message or handle accordingly
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted, save the image
//                saveImageToGallery();
            } else {
                // Storage permission denied, show a message or handle accordingly
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Image captured successfully, retrieve the image from the file
            if (photoFile != null) {
                // Start the EditImg activity and pass the captured image file URI
                Intent intent = new Intent(this, EditImg.class);
                intent.putExtra("imageUri", Uri.fromFile(photoFile));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: No photo file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to remove it from the stack
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
                    Toast.makeText(PlantsActivityHome.this, "View Profile (" + userName + ")", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    // Implement the action to log out
                    Toast.makeText(PlantsActivityHome.this, "Logout", Toast.LENGTH_SHORT).show();
                    logout();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.guideCard) {
            Intent i = new Intent(this, PlantGuideActivity.class);
            startActivity(i);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
