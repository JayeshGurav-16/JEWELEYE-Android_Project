package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.example.admin.CustomerUser;
import com.example.admin.CustomerFirebaseHelper;

public class CustomerRegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private CustomerFirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        // Initialize Firebase helper
        firebaseHelper = CustomerFirebaseHelper.getInstance();

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners
        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> {
            finish(); // Go back to login screen
        });
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
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);

        // Create user with Firebase Authentication
        firebaseHelper.signUp(email, password)
                .addOnSuccessListener(authResult -> {
                    // Create user profile in database
                    String userId = authResult.getUser().getUid();
                    CustomerUser newUser = new CustomerUser(userId, name, email, "customer");
                    newUser.setCreatedAt(System.currentTimeMillis());
                    newUser.setActive(true);

                    firebaseHelper.createUserProfile(newUser)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CustomerRegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                // Go to main activity
                                startActivity(new Intent(CustomerRegisterActivity.this, CustomerMainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CustomerRegisterActivity.this, "Failed to create profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CustomerRegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}