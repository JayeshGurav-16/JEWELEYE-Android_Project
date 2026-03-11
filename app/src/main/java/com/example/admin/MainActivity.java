package com.example.admin;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.example.admin.OrderAdapter;
import com.example.admin.Order;
import com.example.admin.User;
import com.example.admin.FirebaseHelper;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OrderAdapter.OrderActionListener {

    private TextView tvWelcome, tvDate, tvUserCount, tvProductCount, tvOrderCount, tvRevenue, tvNoRecentOrders;
    private RecyclerView recyclerViewRecentOrders;
    private Button btnAddProduct, btnViewOrders;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;

    private FirebaseHelper firebaseHelper;
    private OrderAdapter orderAdapter;
    private List<Order> recentOrdersList;

    private String adminName = "Admin"; // Default name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();

        // Check if user is signed in
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser == null) {
            // Not signed in, launch the Login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI components
        tvWelcome = findViewById(R.id.tvWelcome);
        tvDate = findViewById(R.id.tvDate);
        tvUserCount = findViewById(R.id.tvUserCount);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        tvRevenue = findViewById(R.id.tvRevenue);
        recyclerViewRecentOrders = findViewById(R.id.recyclerViewRecentOrders);
        tvNoRecentOrders = findViewById(R.id.tvNoRecentOrders);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date()));

        // Set up RecyclerView for recent orders
        recentOrdersList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, recentOrdersList, this);
        recyclerViewRecentOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecentOrders.setAdapter(orderAdapter);

        // Set click listeners
        btnAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddEditProductActivity.class));
        });

        btnViewOrders.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderManagementActivity.class));
        });

        // Set bottom navigation listener
        bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_dashboard) {
                return true;
            } else if (itemId == R.id.navigation_products) {
                startActivity(new Intent(this, ProductManagementActivity.class));
                return true;
            } else if (itemId == R.id.navigation_orders) {
                startActivity(new Intent(this, OrderManagementActivity.class));
                return true;
            } else if (itemId == R.id.navigation_users) {
                startActivity(new Intent(this, UserManagementActivity.class));
                return true;
            } else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }

            return false;
        });

        // Load admin profile to get name
        loadAdminProfile(currentUser.getUid());

        // Load dashboard data
        loadDashboardData();
    }

    private void loadAdminProfile(String userId) {
        firebaseHelper.getUserRef(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User adminUser = snapshot.getValue(User.class);
                    if (adminUser != null) {
                        adminName = adminUser.getName();
                        tvWelcome.setText("Welcome, " + adminName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load profile: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDashboardData() {
        progressBar.setVisibility(View.VISIBLE);

        // Load user count
        firebaseHelper.getUsersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                tvUserCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load user count: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load product count
        firebaseHelper.getProductsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                tvProductCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load product count: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load order count and total revenue
        firebaseHelper.getOrdersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                tvOrderCount.setText(String.valueOf(count));

                // Calculate total revenue
                double totalRevenue = 0;
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        totalRevenue += order.getTotal();
                    }
                }

                // Format and display revenue
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

                tvRevenue.setText(currencyFormat.format(totalRevenue));

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to load order data: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load recent orders (limited to 5)
        firebaseHelper.getOrdersRef().limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recentOrdersList.clear();

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        recentOrdersList.add(0, order); // Add to beginning to show most recent first
                    }
                }

                orderAdapter.notifyDataSetChanged();

                // Show or hide the "No recent orders" message
                if (recentOrdersList.isEmpty()) {
                    tvNoRecentOrders.setVisibility(View.VISIBLE);
                    recyclerViewRecentOrders.setVisibility(View.GONE);
                } else {
                    tvNoRecentOrders.setVisibility(View.GONE);
                    recyclerViewRecentOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load recent orders: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateStatusClicked(Order order, int position) {
        startActivity(new Intent(this, OrderManagementActivity.class));
    }

    @Override
    public void onViewDetailsClicked(Order order) {
        // Create a simple dialog to show order details
        StringBuilder detailsBuilder = new StringBuilder();
        detailsBuilder.append("Order ID: ").append(order.getOrderId()).append("\n\n");
        detailsBuilder.append("Customer: ").append(order.getCustomerName()).append("\n\n");
        detailsBuilder.append("Status: ").append(order.getStatus()).append("\n\n");
        detailsBuilder.append("Total: ").append(String.format("%.2f", order.getTotal())).append("\n\n");

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            detailsBuilder.append("Items:\n");
            for (String key : order.getItems().keySet()) {
                Order.OrderItem item = order.getItems().get(key);
                if (item != null) {
                    detailsBuilder.append("- ")
                            .append(item.getProductName())
                            .append(" (x").append(item.getQuantity()).append("): $")
                            .append(String.format("%.2f",item.getPrice() * item.getQuantity()))
                            .append("\n");
                }
            }
        }

        if (order.getShippingAddress() != null && !order.getShippingAddress().isEmpty()) {
            detailsBuilder.append("\nShipping Address:\n").append(order.getShippingAddress());
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Order Details")
                .setMessage(detailsBuilder.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset the selected navigation item when returning to this activity
        bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
    }
}