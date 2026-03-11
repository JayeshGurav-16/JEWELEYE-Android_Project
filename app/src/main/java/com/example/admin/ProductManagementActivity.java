package com.example.admin;
import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.admin.ProductAdapter;
import com.example.admin.Product;
import com.example.admin.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity implements ProductAdapter.ProductActionListener {

    private RecyclerView recyclerViewProducts;
    private ProgressBar progressBar;
    private TextView tvNoProducts;
    private SearchView searchView;
    private FloatingActionButton fabAddProduct;

    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> allProductsList;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        progressBar = findViewById(R.id.progressBar);
        tvNoProducts = findViewById(R.id.tvNoProducts);
        searchView = findViewById(R.id.searchView);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        // Set up RecyclerView
        productList = new ArrayList<>();
        allProductsList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, this);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(productAdapter);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        // Set FAB click listener
        fabAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(ProductManagementActivity.this, AddEditProductActivity.class));
        });

        // Load products
        loadProducts();
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.getProductsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProductsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        allProductsList.add(product);
                    }
                }

                // Filter based on current search query
                String query = searchView.getQuery().toString().trim();
                if (!query.isEmpty()) {
                    filterProducts(query);
                } else {
                    productList.clear();
                    productList.addAll(allProductsList);
                    productAdapter.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);
                updateEmptyStateVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductManagementActivity.this, "Failed to load products: " +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        productList.clear();

        if (query.isEmpty()) {
            productList.addAll(allProductsList);
        } else {
            query = query.toLowerCase();
            for (Product product : allProductsList) {
                if (product.getName().toLowerCase().contains(query) ||
                        product.getCategory().toLowerCase().contains(query) ||
                        product.getDescription().toLowerCase().contains(query)) {
                    productList.add(product);
                }
            }
        }

        productAdapter.notifyDataSetChanged();
        updateEmptyStateVisibility();
    }

    private void updateEmptyStateVisibility() {
        if (productList.isEmpty()) {
            tvNoProducts.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
        } else {
            tvNoProducts.setVisibility(View.GONE);
            recyclerViewProducts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditClicked(Product product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("PRODUCT_ID", product.getProductId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(Product product, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteProduct(product);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProduct(Product product) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.deleteProduct(product.getProductId())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductManagementActivity.this, "Product deleted successfully",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductManagementActivity.this, "Failed to delete product: " +
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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