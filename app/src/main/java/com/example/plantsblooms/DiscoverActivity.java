package com.example.plantsblooms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

public class DiscoverActivity extends AppCompatActivity {


    private ImageView backArrow;

    private TextView title;

    private ImageButton profileButton;
    private static final String TAG = "DiscoverActivity"; // Define a tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Find the ImageButton with ID home
        ImageButton homeButton = findViewById(R.id.home);

        // Set an OnClickListener for the homeButton
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to PlantsActivityHome
                Intent intent = new Intent(DiscoverActivity.this, PlantsActivityHome.class);
                // Start the PlantsActivityHome
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
                Intent intent = new Intent(DiscoverActivity.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });


        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverActivity.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });

        profileButton = findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopupMenu(v);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace YourOtherActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(DiscoverActivity.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });



        // Get plant object from intent extras
        Intent intent = getIntent();
        if (intent != null) {
            Plant plantObject = (Plant) intent.getSerializableExtra("plant_object");
            if (plantObject != null) {
                // Populate views with plant details
                TextView titleTextView = findViewById(R.id.titleTextView);
                TextView descriptionTextView = findViewById(R.id.descriptionTextView);
                ImageView imageView = findViewById(R.id.imageView);

                // Set plant details to views
                titleTextView.setText(plantObject.getTitle());
                descriptionTextView.setText(plantObject.getDescription());
                Picasso.get().load(plantObject.getImageUrl()).into(imageView);
            }
        }
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
                    Toast.makeText(DiscoverActivity.this, "View Profile (" + userName + ")", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    // Implement the action to log out
                    Toast.makeText(DiscoverActivity.this, "Logout", Toast.LENGTH_SHORT).show();
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
