package com.example.admin;;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.admin.User;
import com.example.admin.FirebaseHelper;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnUpdateProfile, btnChangePassword, btnLogout;
    private SwitchCompat switchNotifications, switchDarkMode;
    private ProgressBar progressBar;

    private FirebaseHelper firebaseHelper;
    private FirebaseUser currentUser;
    private User userProfile;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NOTIFICATIONS = "pref_notifications";
    private static final String PREF_DARK_MODE = "pref_dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();
        currentUser = firebaseHelper.getCurrentUser();

        // Check if user is signed in
        if (currentUser == null) {
            // Not signed in, launch the Login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        progressBar = findViewById(R.id.progressBar);

        // Set email field (non-editable)
        etEmail.setText(currentUser.getEmail());

        // Load user profile
        loadUserProfile();

        // Load preference settings
        boolean notificationsEnabled = sharedPreferences.getBoolean(PREF_NOTIFICATIONS, true);
        boolean darkModeEnabled = sharedPreferences.getBoolean(PREF_DARK_MODE, false);

        switchNotifications.setChecked(notificationsEnabled);
        switchDarkMode.setChecked(darkModeEnabled);

        // Set up click listeners
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnLogout.setOnClickListener(v -> logout());

        // Set up preference change listeners
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(PREF_NOTIFICATIONS, isChecked).apply();
            Toast.makeText(SettingsActivity.this,
                    isChecked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(PREF_DARK_MODE, isChecked).apply();

            // Apply dark mode
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.getUserRef(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {
                    userProfile = dataSnapshot.getValue(User.class);
                    if (userProfile != null) {
                        etName.setText(userProfile.getName());
                        // Phone field might be null or missing in our data model
                        if (dataSnapshot.child("phone").exists()) {
                            etPhone.setText(dataSnapshot.child("phone").getValue(String.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SettingsActivity.this, "Failed to load profile: " +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Update user profile in database
        if (userProfile != null) {
            userProfile.setName(name);

            // Save profile
            firebaseHelper.getUserRef(currentUser.getUid()).child("name").setValue(name)
                    .addOnSuccessListener(aVoid -> {
                        // Try to save phone if available
                        if (!TextUtils.isEmpty(phone)) {
                            firebaseHelper.getUserRef(currentUser.getUid()).child("phone").setValue(phone)
                                    .addOnCompleteListener(task -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SettingsActivity.this,
                                                "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SettingsActivity.this,
                                    "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SettingsActivity.this,
                                "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "User profile not loaded correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords don't match");
            etConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Re-authenticate user before changing password
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
        currentUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // User re-authenticated, now change password
                    currentUser.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SettingsActivity.this,
                                        "Password updated successfully", Toast.LENGTH_SHORT).show();

                                // Clear password fields
                                etCurrentPassword.setText("");
                                etNewPassword.setText("");
                                etConfirmPassword.setText("");
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SettingsActivity.this,
                                        "Failed to change password: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SettingsActivity.this,
                            "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    firebaseHelper.signOut();
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    finishAffinity(); // Close all activities
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
