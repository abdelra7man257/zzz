package com.example.plantsblooms;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;



public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword, textInputLayoutRetypePassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dbHelper = new DatabaseHelper(this);
        textInputLayoutEmail = findViewById(R.id.editTextEmail);
        textInputLayoutPassword = findViewById(R.id.etPasswordLayout);
        textInputLayoutRetypePassword = findViewById(R.id.etRetypePasswordLayout);
        textInputLayoutName = findViewById(R.id.userNameTextView);

        Button signUpButton = findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = Objects.requireNonNull(textInputLayoutName.getEditText()).getText().toString().trim();
        String email = Objects.requireNonNull(textInputLayoutEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(textInputLayoutPassword.getEditText()).getText().toString().trim();
        String retypePassword = Objects.requireNonNull(textInputLayoutRetypePassword.getEditText()).getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty() && !retypePassword.isEmpty()) {
            // Check if password and retype password match
            if (password.equals(retypePassword)) {
                // Insert user into database
                User user = new User(name,email, password);
                if (dbHelper.insertUser(user)) {
                    // User added successfully
                    Toast.makeText(SignUpActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to the main activity
                    Intent intent = new Intent(SignUpActivity.this, PlantsActivityHome.class);
                    intent.putExtra("user_name", name); // Pass the user's email to the next activity
                    startActivity(intent);

                    SharedPreferences sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("user_name", name); // Use the key "user_name" to store the user's name
                    editor.apply();



                    finish(); // Finish the current activity
                } else {
                    // User not added, handle accordingly
                    Toast.makeText(SignUpActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Passwords do not match, handle accordingly
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle empty fields
            Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean insertUser(User user) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
            long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
            db.close();

            if (result != -1) {
                // User added successfully
                return true;
            } else {
                // User not added
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log the exception message
            Log.e("SignUpActivity", "Error inserting user: " + e.getMessage());
            return false;
        }
    }


}