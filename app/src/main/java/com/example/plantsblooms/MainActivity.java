package com.example.plantsblooms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageButton googleSignInButton;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private DatabaseHelper dbHelper;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();

        textInputLayoutEmail = findViewById(R.id.editTextEmail);
        textInputLayoutPassword = findViewById(R.id.etPasswordLayout);

        Button signInButton = findViewById(R.id.buttonSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        ImageButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        // Configure Google One Tap sign-in
        oneTapClient = Identity.getSignInClient(this);

        // Build the request for Google One Tap sign-in
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }



    private void signInWithGoogle() {
        // Start the One Tap sign-in flow
        Task<BeginSignInResult> beginSignInResultTask = oneTapClient.beginSignIn(signInRequest)
                .addOnCompleteListener(this, new OnCompleteListener<BeginSignInResult>() {
                    @Override
                    public void onComplete(@NonNull Task<BeginSignInResult> task) {
                        if (task.isSuccessful()) {
                            // Successfully got the sign-in result
                            BeginSignInResult result = task.getResult();
                            if (result != null) {
                                PendingIntent signInIntent = result.getPendingIntent();

                                if (signInIntent != null) {
                                    // Start the sign-in intent
                                    try {
                                        // This should be startActivityForResult from MainActivity
                                        startIntentSenderForResult(signInIntent.getIntentSender(), 1234, null, 0, 0, 0);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    // Handle case where PendingIntent is null
                                    Toast.makeText(MainActivity.this, "Sign-in intent is null", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle case where BeginSignInResult is null
                                Toast.makeText(MainActivity.this, "Sign-in result is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Request failed
                            Exception e = task.getException();
                            Toast.makeText(MainActivity.this, "Failed to start sign-in flow: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 1234) {
            // Handle the result of Google One Tap sign-in
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                if (credential != null) {
                    // Extract the Google ID token
                    String googleIdToken = credential.getGoogleIdToken();
                    if (googleIdToken != null) {
                        // Use the Google ID token for Firebase authentication
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleIdToken, null);
                        mAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                       // FirebaseUser user = null;
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                            if (user != null) {
                                                // Create an Intent to start GuideActivity


                                                // User is signed in, navigate to the main activity
                                                Intent plantsIntent  = new Intent(MainActivity.this, PlantsActivityHome.class);
                                                plantsIntent .putExtra("user_email", user.getEmail());

                                                startActivity(plantsIntent);
                                                finish();

                                                SharedPreferences sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("user_email", user.getEmail());
                                                editor.apply();
                                            }
                                        } else {
                                            Log.e("Authentication", "Failed to sign in: " + task.getException().getMessage());
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // Google ID token is null
                        Toast.makeText(MainActivity.this, "Failed to get Google ID token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // SignInCredential is null
                    Toast.makeText(MainActivity.this, "Failed to get sign-in credential", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                // Handle ApiException
                Toast.makeText(MainActivity.this, "ApiException: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(MainActivity.this, "data null", Toast.LENGTH_SHORT).show();
        }
    }


    private void signIn() {
        String email = Objects.requireNonNull(textInputLayoutEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(textInputLayoutPassword.getEditText()).getText().toString().trim();

        // Check if email and password are not empty
        if (!email.isEmpty() && !password.isEmpty()) {
            // Validate the credentials by checking with the database
            if (validateCredentials(email, password)) {
                // User signed in successfully
                Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();

                // Create an Intent to start PlantsActivityHome
                Intent plantsIntent = new Intent(MainActivity.this, PlantsActivityHome.class);
                plantsIntent.putExtra("user_email", email); // Assuming email is the user's email
                startActivity(plantsIntent);
                finish(); // Finish the current activity

                SharedPreferences sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("user_email", email);
                editor.apply();




            } else {
                // Invalid credentials, show a toast
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle empty fields
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateCredentials(String email, String password) {
        // Check the entered email and password against the database
        // You need to implement this method based on your database structure
        // Return true if the credentials are valid, false otherwise
        return dbHelper.checkUserCredentials(email, password);
    }

    public void onSignUpClick(View view) {
        // Handle the sign-up click event here
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


}
