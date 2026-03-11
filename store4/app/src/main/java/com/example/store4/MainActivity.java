package com.example.store4;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.store4.databinding.ActivityMainBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private SearchView searchView;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Set the default fragment
        replaceFragment(new com.example.store4.homeFragment());

        // Remove the background of the bottom navigation view
        binding.bottomNavigationView.setBackground(null);

        // Set up the bottom navigation view item selection listener
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new com.example.store4.homeFragment());
            } else if (itemId == R.id.product) {
                replaceFragment(new com.example.store4.productFragment());
            } else if (itemId == R.id.cart) {
                replaceFragment(new com.example.store4.cartFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new com.example.store4.profileFragment());
            }
            return true; // Return true to indicate the item selection is handled
        });

        // fab Button -> open try on fragment

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);

        // Set click listener for FAB
        fab.setOnClickListener(view -> {
            // Replace the container with the fragment
            replaceFragment(new try_onFragment());
        });


        //top bar -> like , notification , setting

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menuFavorite) {
                    Toast.makeText(MainActivity.this, "add favorite item", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.menuSetting) {
                    Toast.makeText(MainActivity.this, "open setting", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.menuNotification) {
                    Toast.makeText(MainActivity.this, "see notification", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });



    // Initialize the SearchView
    searchView = findViewById(R.id.searchView);

    // Set up the SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // Handle the search query when the user submits the search
            performSearch(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // Handle the search query as the user types (optional)
            // You can implement live search functionality here if needed
            return false;
        }
    });
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_bar, menu);
        return true;
    }

private void performSearch(String query) {
    // Implement your search logic here
    // For example, you can filter a list, make a network request, etc.
    Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();

    // Example: If you have a RecyclerView or ListView, you can filter the data here
    // adapter.getFilter().filter(query);
}


    // Method to replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}