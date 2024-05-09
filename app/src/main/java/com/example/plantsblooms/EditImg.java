package com.example.plantsblooms;


import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.plantsblooms.api.ApiModule;
import com.example.plantsblooms.api.PlantPredictionResponse;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditImg extends AppCompatActivity {

    private String selectedPlantImageName; // Store the selected plant image name
    private String[] plantImageNames = {"Eucalyptus.jpg", "FicusLyrata.jpg", "SnakePlant.jpg", "Strelitzia.jpg", "StrelitziaL.jpg"};
    private Bitmap capturedImageBitmap;
    private MaterialButton button;
    private TextView name;
    private TextView description;

    private ProgressBar progressBar;
    private ImageView imageViewPopup;
//    private LinearLayout cardContainer;
    private int selectedPlantIndex = -1; // Default value indicating no plant is selected
    private static final int REQUEST_PERMISSIONS = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_img);

        name = findViewById(R.id.plantName);
        description = findViewById(R.id.plantDescription);
        progressBar = findViewById(R.id.progress);
        button = findViewById(R.id.done);
        button.setOnClickListener(v -> {
            finish();
        });

        // Find views
        imageViewPopup = findViewById(R.id.imageViewPopup);
//        cardContainer = findViewById(R.id.cardContainer);




//        Button btnSaveToGallery = findViewById(R.id.btnSaveToGallery);
//        btnSaveToGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveImageToGallery(capturedImageBitmap);
//            }
//        });


        // Get the file URI of the captured image from the intent
        Uri capturedImageUri = getIntent().getParcelableExtra("imageUri");

        // Find the ImageView in the layout
        ImageView imageViewPopup = findViewById(R.id.imageViewPopup);
        // Decode the file URI to a Bitmap and set it to the ImageView
        if (capturedImageUri != null) {
            capturedImageBitmap = decodeUriToBitmap(capturedImageUri);
            File imageFile = null;
            if (capturedImageBitmap != null) {
                imageViewPopup.setImageBitmap(capturedImageBitmap);
                 try {
                     imageFile = createFile(capturedImageUri);
                 }catch (IOException e)
                 {
                     e.printStackTrace();
                 }
            }


            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                            "image"
                            ,imageFile.getName(),
                            RequestBody.create(MediaType.parse("image/*"), imageFile)
                    ).build();
            ApiModule.getWebServices().getPrediction(body).enqueue(new Callback<PlantPredictionResponse>() {
                @Override
                public void onResponse(Call<PlantPredictionResponse> call, Response<PlantPredictionResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            name.setText(response.body().prediction);
                            description.setText(response.body().more);
                        } else {
                            Toast.makeText(getBaseContext(), "error occurred", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(getBaseContext(), "error occurred", Toast.LENGTH_LONG).show();
                        try {
                            Log.e("error", response.errorBody().string().toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    progressBar.setVisibility(View.GONE);
                    description.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<PlantPredictionResponse> call, Throwable t) {
                    Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                }
            });


            /*
             * 1->
             * success -> data -> server -> response success
             * fail -> data ->
             *
             *
             * 2->
             * */

        }
        // Set click listeners for CardViews
//        for (int i = 0; i < cardContainer.getChildCount(); i++) {
//            View cardView = cardContainer.getChildAt(i);
//            final String imageName = plantImageNames[i];
//            cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    selectedPlantImageName = imageName; // Store the selected plant image name
//                }
//            });
//        }

//        LinearLayout cardContainer = findViewById(R.id.cardContainer);

        // Loop to create and add CardView elements
        for (String imageName : plantImageNames) {
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.card_width), // Set your desired width
                    getResources().getDimensionPixelSize(R.dimen.card_height) // Set your desired height
            );
            layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.card_margin), 0, 0, 0); // Set margin
            cardView.setLayoutParams(layoutParams);
            cardView.setRadius(getResources().getDimension(R.dimen.card_corner_radius)); // Set corner radius
            cardView.setCardElevation(getResources().getDimension(R.dimen.card_elevation)); // Set elevation

            // Add plant image to the CardView
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            try {
                InputStream inputStream = getAssets().open(imageName);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageView.setImageDrawable(drawable);
                imageView.setBackgroundColor(Color.TRANSPARENT); // Set background to transparent
                // Set click listener to select the plant image
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlePlantImageSelection(imageName);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            cardView.addView(imageView);
//            cardContainer.addView(cardView);
        }

        // Set touch listener for the captured image
        imageViewPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TouchDebug", "Touch event detected!");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Get touch coordinates
                    float x = event.getX();
                    float y = event.getY();

                    // Add selected plant image at the touched position
                    addPlantImageAtPosition(x, y);
                }
                return true;
            }
        });

    }

    File createFile(Uri imageUri) throws IOException {
        File fileDir = getFilesDir();
        File file = new File(fileDir, "image.jpg");
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        FileOutputStream outPutStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outPutStream);
        return file;
    }

    private void addPlantImageAtPosition(float x, float y) {
        Log.d("AddPlantDebug", "Adding plant image at position: x=" + x + ", y=" + y);

        if (selectedPlantImageName != null) {
            // Load the selected plant image dynamically from the assets folder
            try {
                InputStream inputStream = getAssets().open(selectedPlantImageName);
                Drawable drawable = Drawable.createFromStream(inputStream, null);

                // Create an ImageView for the selected plant image
                ImageView plantImageView = new ImageView(this);
                plantImageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                plantImageView.setId(View.generateViewId()); // Set a unique ID for the ImageView
                plantImageView.setImageDrawable(drawable);

                // Increase the size of the plant image (optional)
                int newWidth = (int) (drawable.getIntrinsicWidth() * 1.4); // Increase width by 50%
                int newHeight = (int) (drawable.getIntrinsicHeight() * 1.4); // Increase height by 50%
                ViewGroup.LayoutParams layoutParams = plantImageView.getLayoutParams();
                layoutParams.width = newWidth;
                layoutParams.height = newHeight;
                plantImageView.setLayoutParams(layoutParams);

                // Add the ImageView to the parent layout
                ConstraintLayout parentLayout = findViewById(R.id.editImgLayout); // Use the parent layout containing the captured image
                parentLayout.addView(plantImageView);

                // Calculate the correct position of the plant image based on touch coordinates
                int[] parentLocation = new int[2];
                parentLayout.getLocationOnScreen(parentLocation);
                int offsetX = (int) x - parentLocation[0] - newWidth / 2; // Adjust x-coordinate to center the plant image
                int offsetY = (int) y - parentLocation[1] - newHeight / 2; // Adjust y-coordinate to center the plant image

                // Set position of the ImageView based on calculated coordinates
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);
                constraintSet.connect(plantImageView.getId(), ConstraintSet.START, parentLayout.getId(), ConstraintSet.START, offsetX);
                constraintSet.connect(plantImageView.getId(), ConstraintSet.TOP, parentLayout.getId(), ConstraintSet.TOP, offsetY);
                constraintSet.applyTo(parentLayout);

                // After adding the plant image, combine it with the captured image and save to gallery
//                combineAndSaveImages(parentLayout);

                Log.d("AddPlantDebug", "Plant image added successfully");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load plant image", Toast.LENGTH_SHORT).show();
                Log.e("AddPlantError", "Failed to load plant image: " + e.getMessage());
            }
        } else {
            Log.d("AddPlantDebug", "No plant image selected");
        }
    }


    // Combine the captured image with the added plant image and save to gallery
//    private void combineAndSaveImages(ConstraintLayout parentLayout) {
//        // Create a bitmap with the same dimensions as the captured image
//        Bitmap combinedBitmap = Bitmap.createBitmap(capturedImageBitmap.getWidth(), capturedImageBitmap.getHeight(), capturedImageBitmap.getConfig());
//
//        // Create a canvas with the combined bitmap
//        Canvas canvas = new Canvas(combinedBitmap);
//        canvas.drawBitmap(capturedImageBitmap, new Matrix(), null); // Draw the captured image
//
//        // Iterate through all views in the parent layout
//        for (int i = 0; i < parentLayout.getChildCount(); i++) {
//            View childView = parentLayout.getChildAt(i);
//            if (childView instanceof ImageView) {
//                ImageView imageView = (ImageView) childView;
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                Bitmap plantBitmap = drawable.getBitmap();
//                // Get the position of the plant image relative to the parent layout
//                int[] location = new int[2];
//                imageView.getLocationInWindow(location);
//                int x = location[0];
//                int y = location[1];
//                // Draw the plant image onto the canvas at its position
//                canvas.drawBitmap(plantBitmap, x, y, null);
//            }
//        }
//
//        // Save the combined bitmap to the gallery
//        saveImageToGallery(combinedBitmap);
//    }


    private Bitmap decodeUriToBitmap(Uri uri) {
        try {
            // Decode the file URI to a Bitmap
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleImageClick(View view) {
        // Handle the click on the captured image view
        if (capturedImageBitmap != null) {
            float x = view.getX();
            float y = view.getY();

            // Get the dimensions of the captured image view
            int capturedImageWidth = view.getWidth();
            int capturedImageHeight = view.getHeight();

            // Calculate the position relative to the captured image dimensions
            float relativeX = x / capturedImageWidth;
            float relativeY = y / capturedImageHeight;

            // Now you have the relative coordinates (relativeX, relativeY) where the user clicked on the captured image
            // Use these coordinates to determine where to place the selected plant image

            // For example, you can calculate the position in pixels
            int plantImageX = (int) (relativeX * capturedImageBitmap.getWidth());
            int plantImageY = (int) (relativeY * capturedImageBitmap.getHeight());

            // Now you can allocate the selected plant image at the calculated position (plantImageX, plantImageY)
            // You might want to adjust the position based on the size of the selected plant image

            // For example, you can show a toast with the calculated position
            Toast.makeText(this, "Plant image position: x=" + plantImageX + ", y=" + plantImageY, Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePlantImageSelection(String imageName) {
        // You can implement this method to handle the selection of plant images from the CardViews
        // For example, you can store the selected plant image name or index for later use
        // You may also update the UI to indicate the selected plant image to the user
        selectedPlantImageName = imageName;
        Toast.makeText(this, "Selected plant image: " + imageName, Toast.LENGTH_SHORT).show();
        Log.d("PlantSelection", "Selected plant image: " + imageName);
    }

//    private void saveImageToGallery(Bitmap bitmap) {
//        // Prepare the image metadata
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, "captured_image_with_plant");
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//
//        // Insert the image metadata into the MediaStore
//        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        if (imageUri != null) {
//            try {
//                // Open an output stream to write the bitmap data to the gallery
//                OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
//                if (outputStream != null) {
//                    // Compress and write the bitmap data to the output stream
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                    outputStream.close();
////                    Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
//        }
//    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted, save the image
//                saveImageToGallery(capturedImageBitmap);
            } else {
                // Storage permission denied, show a message or handle accordingly
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
