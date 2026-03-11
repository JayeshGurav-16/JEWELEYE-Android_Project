package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CustomerProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvPhone;
    private Button btnEditProfile, btnLogout;

    private CustomerFirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        // Initialize Firebase helper
        firebaseHelper = CustomerFirebaseHelper.getInstance();

        // Initialize UI components
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Set click listeners
        btnEditProfile.setOnClickListener(v -> {
            // Navigate to edit profile activity
            // startActivity(new Intent(getActivity(), CustomerEditProfileActivity.class));
        });

        btnLogout.setOnClickListener(v -> logout());

        // Load user profile
        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Display email from Firebase Auth
            tvEmail.setText(currentUser.getEmail());

            // Load additional user details from Realtime Database
            firebaseHelper.getUsersRef().child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                String phone = snapshot.child("phone").getValue(String.class);

                                if (name != null) {
                                    tvName.setText(name);
                                }

                                if (phone != null) {
                                    tvPhone.setText(phone);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // Navigate to login activity
        startActivity(new Intent(getActivity(), CustomerLoginActivity.class));
        getActivity().finish();
    }
}