package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import com.example.admin.User;
import com.example.admin.FirebaseHelper;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private ImageView ivBackButton;
    private ProgressBar progressBar;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        ivBackButton = findViewById(R.id.ivBackButton);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners
        btnSignup.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> finish()); // Go back to login screen
        ivBackButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords don't match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);

        // Create user with Firebase Auth
        firebaseHelper.signUp(email, password)
                .addOnSuccessListener(authResult -> {
                    // Create user profile in database
                    String userId = authResult.getUser().getUid();
                    User user = new User(userId, name, email, "admin");

                    firebaseHelper.createUserProfile(user)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignupActivity.this, R.string.signup_successful, Toast.LENGTH_SHORT).show();

                                // Go to MainActivity
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finishAffinity(); // Close all activities
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignupActivity.this, "Failed to create profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}