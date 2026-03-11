package com.example.admin;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.example.admin.OrderAdapter;

import com.example.admin.Order;

import com.example.admin.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity implements OrderAdapter.OrderActionListener {

    private RecyclerView recyclerViewOrders;
    private ProgressBar progressBar;
    private TextView tvNoOrders;
    private SearchView searchView;
    private TabLayout tabLayout;

    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private List<Order> allOrdersList;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        progressBar = findViewById(R.id.progressBar);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);

        // Set up RecyclerView
        orderList = new ArrayList<>();
        allOrdersList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList, this);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(orderAdapter);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOrders(newText);
                return true;
            }
        });

        // Set up tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterOrdersByStatus(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });

        // Load orders
        loadOrders();
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.getOrdersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allOrdersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        allOrdersList.add(order);
                    }
                }

                // Initially show all orders (tab position 0)
                filterOrdersByStatus(tabLayout.getSelectedTabPosition());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderManagementActivity.this, "Failed to load orders: " +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrdersByStatus(int tabPosition) {
        orderList.clear();

        // Tab positions: 0 = All, 1 = Pending, 2 = Shipped, 3 = Delivered
        String statusFilter = null;
        switch (tabPosition) {
            case 1:
                statusFilter = "pending";
                break;
            case 2:
                statusFilter = "shipped";
                break;
            case 3:
                statusFilter = "delivered";
                break;
        }

        // Filter orders by status if a specific tab is selected
        if (statusFilter != null) {
            for (Order order : allOrdersList) {
                if (order.getStatus().equalsIgnoreCase(statusFilter)) {
                    orderList.add(order);
                }
            }
        } else {
            // Show all orders
            orderList.addAll(allOrdersList);
        }

        // Update adapter and empty state visibility
        orderAdapter.notifyDataSetChanged();

        if (orderList.isEmpty()) {
            tvNoOrders.setVisibility(View.VISIBLE);
            if (statusFilter != null) {
                tvNoOrders.setText("No " + statusFilter + " orders");
            } else {
                tvNoOrders.setText(R.string.no_data_available);
            }
        } else {
            tvNoOrders.setVisibility(View.GONE);
        }
    }

    private void filterOrders(String query) {
        List<Order> filteredList = new ArrayList<>();

        for (Order order : orderList) {
            if (order.getOrderId().toLowerCase().contains(query.toLowerCase()) ||
                    order.getCustomerName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(order);
            }
        }

        orderAdapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            tvNoOrders.setVisibility(View.VISIBLE);
            tvNoOrders.setText("No orders found matching '" + query + "'");
        } else {
            tvNoOrders.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUpdateStatusClicked(Order order, int position) {
        // Create options array for status selection
        final String[] statusOptions = {
                getString(R.string.status_pending),
                getString(R.string.status_processing),
                getString(R.string.status_shipped),
                getString(R.string.status_delivered),
                getString(R.string.status_cancelled)
        };

        new AlertDialog.Builder(this)
                .setTitle("Update Order Status")
                .setSingleChoiceItems(statusOptions, getCurrentStatusIndex(order.getStatus()), null)
                .setPositiveButton("Update", (dialog, which) -> {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    if (selectedPosition != -1) {
                        String newStatus = statusOptions[selectedPosition].toLowerCase();
                        updateOrderStatus(order, newStatus, position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getCurrentStatusIndex(String currentStatus) {
        switch (currentStatus.toLowerCase()) {
            case "pending": return 0;
            case "processing": return 1;
            case "shipped": return 2;
            case "delivered": return 3;
            case "cancelled": return 4;
            default: return 0;
        }
    }

    private void updateOrderStatus(Order order, String newStatus, int position) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.updateOrderStatus(order.getOrderId(), newStatus)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrderManagementActivity.this,
                            "Order status updated successfully", Toast.LENGTH_SHORT).show();

                    // Update local data
                    order.setStatus(newStatus);
                    orderAdapter.notifyItemChanged(position);

                    // Refresh tab filters if needed
                    filterOrdersByStatus(tabLayout.getSelectedTabPosition());
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrderManagementActivity.this,
                            "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onViewDetailsClicked(Order order) {
        // In a real app, you might launch a detailed view of the order
        // For now, just show basic info in an alert dialog
        StringBuilder detailsBuilder = new StringBuilder();
        detailsBuilder.append("Order ID: ").append(order.getOrderId()).append("\n\n");
        detailsBuilder.append("Customer: ").append(order.getCustomerName()).append("\n\n");
        detailsBuilder.append("Status: ").append(order.getStatus()).append("\n\n");
        detailsBuilder.append("Total: $").append(String.format("%.2f", order.getTotal())).append("\n\n");

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            detailsBuilder.append("Items:\n");
            for (String key : order.getItems().keySet()) {
                Order.OrderItem item = order.getItems().get(key);
                if (item != null) {
                    detailsBuilder.append("- ")
                            .append(item.getProductName())
                            .append(" (x").append(item.getQuantity()).append("): $")
                            .append(String.format("%.2f", item.getPrice() * item.getQuantity()))
                            .append("\n");
                }
            }
        }

        if (order.getShippingAddress() != null && !order.getShippingAddress().isEmpty()) {
            detailsBuilder.append("\nShipping Address:\n").append(order.getShippingAddress());
        }

        new AlertDialog.Builder(this)
                .setTitle("Order Details")
                .setMessage(detailsBuilder.toString())
                .setPositiveButton("Close", null)
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
