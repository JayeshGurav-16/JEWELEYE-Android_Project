package com.example.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class CustomerForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private TextView tvBackToLogin;
    private ProgressBar progressBar;
    private CustomerFirebaseHelper firebaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_forgot_password);

        // Initialize Firebase helper
        firebaseHelper = CustomerFirebaseHelper.getInstance();

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners
        btnResetPassword.setOnClickListener(v -> resetPassword());
        tvBackToLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);

        // Send password reset email
        firebaseHelper.resetPassword(email)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CustomerForgotPasswordActivity.this,
                            "Password reset email sent to " + email,
                            Toast.LENGTH_LONG).show();

                    // Return to login screen after short delay
                    etEmail.setText("");

                    // Navigate back to login screen
                    new android.os.Handler().postDelayed(() -> {
                        navigateToLogin();
                    }, 2000);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CustomerForgotPasswordActivity.this,
                            "Failed to send reset email: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, CustomerLoginActivity.class));
        finish();
    }
}