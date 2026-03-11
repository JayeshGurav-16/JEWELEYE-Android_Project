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
import com.example.admin.CustomerFirebaseHelper;

public class CustomerLoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;
    private CustomerFirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        // Initialize Firebase helper
        firebaseHelper = CustomerFirebaseHelper.getInstance();

        // Check if user is already signed in
        if (firebaseHelper.getCurrentUser() != null) {
            startActivity(new Intent(this, CustomerMainActivity.class));
            finish();
            return;
        }

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners
        btnLogin.setOnClickListener(v -> loginUser());
        tvForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
        tvRegister.setOnClickListener(v -> navigateToRegister());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
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

        // Show progress
        progressBar.setVisibility(View.VISIBLE);

        // Authenticate with Firebase
        firebaseHelper.signIn(email, password)
                .addOnSuccessListener(authResult -> {
                    progressBar.setVisibility(View.GONE);

                    // Update last login timestamp
                    String userId = authResult.getUser().getUid();
                    firebaseHelper.updateUserLastLogin(userId);

                    // Start main activity
                    startActivity(new Intent(CustomerLoginActivity.this, CustomerMainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CustomerLoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToForgotPassword() {
        startActivity(new Intent(this, CustomerForgotPasswordActivity.class));
    }

    private void navigateToRegister() {
        startActivity(new Intent(this, CustomerRegisterActivity.class));
    }
}