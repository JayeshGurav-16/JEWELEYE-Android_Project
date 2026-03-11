package com.example.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CustomerFirebaseHelper firebaseHelper;
    private CustomerCartManager cartManager;
    private CustomerWishlistManager wishlistManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        // Initialize helpers
        firebaseHelper = CustomerFirebaseHelper.getInstance();
        cartManager = CustomerCartManager.getInstance(this);
        wishlistManager = CustomerWishlistManager.getInstance(this); // Initialize the wishlist manager

        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, CustomerLoginActivity.class));
            finish();
            return;
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Jewlleye");
        }

        // Set up bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new CustomerHomeFragment();
            } else if (itemId == R.id.navigation_cart) {
                selectedFragment = new CustomerCartFragment();
            } else if (itemId == R.id.navigation_wishlist) {
                selectedFragment = new CustomerWishlistFragment();
            } else if (itemId == R.id.navigation_orders) {
                selectedFragment = new CustomerOrdersFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new CustomerProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CustomerHomeFragment())
                    .commit();
        }

        // Update cart badge
        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge when returning to activity
        updateCartBadge();
    }

    private void updateCartBadge() {
        int cartItemCount = cartManager.getItemCount();

        if (cartItemCount > 0) {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
            badge.setVisible(true);
            badge.setNumber(cartItemCount);
        } else {
            BadgeDrawable badge = bottomNavigationView.getBadge(R.id.navigation_cart);
            if (badge != null) {
                badge.setVisible(false);
                badge.clearNumber();
            }
        }

        // Also update wishlist badge if needed
        int wishlistCount = wishlistManager.getWishlistCount();
        if (wishlistCount > 0) {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_wishlist);
            badge.setVisible(true);
            badge.setNumber(wishlistCount);
        } else {
            BadgeDrawable badge = bottomNavigationView.getBadge(R.id.navigation_wishlist);
            if (badge != null) {
                badge.setVisible(false);
                badge.clearNumber();
            }
        }
    }
}