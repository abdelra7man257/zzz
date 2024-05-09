package com.example.plantsblooms;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Blogs extends AppCompatActivity {


    private BlogsListAdapter adapter;

    private View dialogView;

    private static final int PICK_IMAGE_REQUEST = 1002;

    private static final int GALLERY_REQUEST_CODE = 1001;

    private ImageButton profileButton;
    private RecyclerView recyclerViewBlog;
    private List<BlogsList> blogsList;
    private DatabaseHelper databaseHelper;
    private String userName;

    private ImageView backArrow;
    private Uri imageUri;

    private TextView title;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs);
        // Check runtime permissions for Android 6.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Request the permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
            }
        }



// Update the RecyclerView with the retrieved blogs data and the image URI





        // Find the ImageButton with ID home
        ImageButton homeButton = findViewById(R.id.home);

        // Set an OnClickListener for the homeButton
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to PlantsActivityHome
                Intent intent = new Intent(Blogs.this, PlantsActivityHome.class);
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
        if (intent != null && intent.hasExtra("user_name")) {
            String user_name = intent.getStringExtra("user_name");
            // Use userEmail as needed (e.g., displaying in the profile menu)
            Toast.makeText(Blogs.this, "User Name: " + user_name, Toast.LENGTH_SHORT).show();
        }
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopupMenu(v);
            }
        });

        // Set up the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace YourOtherActivity.class with the class of the activity you want to navigate to
                Intent intent = new Intent(Blogs.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });



        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Blogs.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });

        // Set click listener to the toolbar's back arrow
        backArrow = findViewById(R.id.back_arrow);

        // Set click listener to the toolbar's title
        title = findViewById(R.id.toolbar_title);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Blogs.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });


        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Blogs.this, PlantsActivityHome.class);
                startActivity(intent);
            }
        });



        // Set up RecyclerView
        recyclerViewBlog = findViewById(R.id.recyclerViewBlog);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewBlog.setLayoutManager(layoutManager);
// Instantiate the adapter and pass the image URI
        // Instantiate the adapter and pass the image URI
        adapter = new BlogsListAdapter(this, blogsList, imageUri);



        // Set the adapter to your RecyclerView
        recyclerViewBlog.setAdapter(adapter);

        // Retrieve signed-in user email
        SharedPreferences sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        userName = sharedPref.getString("user_name", "");

        // Retrieve blogs data from the database
        databaseHelper = new DatabaseHelper(this);
        blogsList = databaseHelper.getBlogsForUser(userName);

        // Update the RecyclerView with the retrieved blogs data
        updateRecyclerView(blogsList, imageUri);













        // Set up FloatingActionButton click listener
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddBlog = findViewById(R.id.fabAddBlog);
        fabAddBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddBlogDialog();
            }
        });

    }



    // Inside your Blogs class
    private void updateRecyclerView(List<BlogsList> blogsList, Uri imageUri) {
        BlogsListAdapter blogsListAdapter = new BlogsListAdapter(this, blogsList, imageUri);
        recyclerViewBlog.setAdapter(blogsListAdapter);
    }


    private void showAddBlogDialog() {
        // Create a custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        dialogView = inflater.inflate(R.layout.dialog_add_blog, null); // Assign the inflated view to dialogView

        // Find views in the custom layout
        EditText editTextBlog = dialogView.findViewById(R.id.editTextBlog);
        Button buttonSubmitBlog = dialogView.findViewById(R.id.buttonSubmitBlog);
        Button buttonAddImage = dialogView.findViewById(R.id.buttonAddImage); // Add this line

        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        // Set up the button click listener
        buttonSubmitBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String blogText = editTextBlog.getText().toString().trim();
                if (!blogText.isEmpty()) {
                    // Insert the new blog entry into the database with the signed-in user's email
                    boolean inserted = databaseHelper.insertBlog(userName, blogText);
                    if (inserted) {
                        // Refresh the RecyclerView with the updated blogs data
                        blogsList = databaseHelper.getBlogsForUser(userName);
                        // Update the RecyclerView with the retrieved blogs data and the image URI
                        updateRecyclerView(blogsList, imageUri);

                        alertDialog.dismiss();
                    }
                }
            }
        });

        // Set up the button click listener for adding image
        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        // Show the AlertDialog
        alertDialog.show();
    }

    private void pickImage() {
        // Create an intent to pick images from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
                    Toast.makeText(Blogs.this, "View Profile (" + userName + ")", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.action_logout) {
                    // Implement the action to log out
                    Toast.makeText(Blogs.this, "Logout", Toast.LENGTH_SHORT).show();
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            imageUri = data.getData();

            // Notify the adapter that the image URI has changed
            adapter.setImageUri(imageUri);

            // Save the image URI to SharedPreferences
            saveImageUriToPreferences(imageUri);

            // Show a toast message indicating successful image selection
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Show a toast message indicating failure to select an image
            Toast.makeText(this, "Failed to select image from gallery", Toast.LENGTH_SHORT).show();
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call super method

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing the gallery
                Toast.makeText(this, "Access to gallery permission granted", Toast.LENGTH_SHORT).show();
                // Start the intent to pick an image from the gallery
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Permission denied. Cannot access gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }







    private void saveImageUriToPreferences(Uri imageUri) {
        if (imageUri != null) {
            Log.d("Blogs", "Saving image URI: " + imageUri.toString());
            SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("image_uri", imageUri.toString());
            editor.apply();
        } else {
            Log.e("Blogs", "Image URI is null. Cannot save to SharedPreferences.");
        }
    }


}
