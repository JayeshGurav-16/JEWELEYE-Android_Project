package com.example.admin;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.example.admin.FirebaseHelper;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignup,CUSTOMERLOGIN;
    private ProgressBar progressBar;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();

        // Check if user is already signed in
        if (firebaseHelper.getCurrentUser() != null) {
            // User is already signed in, go to MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignup = findViewById(R.id.tvSignup);
        progressBar = findViewById(R.id.progressBar);
        CUSTOMERLOGIN=findViewById(R.id.CUSTOMERLOGIN);

        // Set click listeners
        btnLogin.setOnClickListener(v -> loginUser());
        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        CUSTOMERLOGIN.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CustomerLoginActivity.class));
        });
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
                    Toast.makeText(LoginActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();

                    // Go to MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showForgotPasswordDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        TextInputEditText etDialogEmail = view.findViewById(R.id.etDialogEmail);

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setView(view)
                .setPositiveButton("Reset", (dialog, which) -> {
                    String email = etDialogEmail.getText().toString().trim();
                    if (!TextUtils.isEmpty(email)) {
                        resetPassword(email);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.resetPassword(email)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
