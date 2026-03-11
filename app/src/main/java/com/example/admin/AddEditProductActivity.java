package com.example.admin;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.admin.Product;
import com.example.admin.FirebaseHelper;
import com.example.admin.ImageUtils;
public class AddEditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText etProductName, etProductDescription, etProductPrice,
            etProductCategory, etProductStock;
    private ImageView ivProductImage;
    private Button btnUploadImage, btnSaveProduct;
    private ProgressBar progressBar;

    private FirebaseHelper firebaseHelper;
    private String productId;
    private Uri imageUri;
    private String currentImageUrl;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductCategory = findViewById(R.id.etProductCategory);
        etProductStock = findViewById(R.id.etProductStock);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        progressBar = findViewById(R.id.progressBar);

        // Determine if we're in edit mode
        productId = getIntent().getStringExtra("PRODUCT_ID");
        isEditMode = productId != null;

        // Update toolbar title based on mode
        getSupportActionBar().setTitle(isEditMode ? R.string.edit_product : R.string.add_product);

        // Set up click listeners
        btnUploadImage.setOnClickListener(v -> openImagePicker());
        btnSaveProduct.setOnClickListener(v -> saveProduct());

        // If edit mode, load the product data
        if (isEditMode) {
            loadProductData();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageUtils.loadImageFromUri(this, imageUri, ivProductImage);
        }
    }

    private void loadProductData() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.getProductRef(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        // Populate UI with product data
                        etProductName.setText(product.getName());
                        etProductDescription.setText(product.getDescription());
                        etProductPrice.setText(String.valueOf(product.getPrice()));
                        etProductCategory.setText(product.getCategory());
                        etProductStock.setText(String.valueOf(product.getStock()));

                        // Load product image
                        currentImageUrl = product.getImageUrl();
                        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                            ImageUtils.loadImageFromUrl(AddEditProductActivity.this, currentImageUrl, ivProductImage);
                        }
                    }
                } else {
                    Toast.makeText(AddEditProductActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddEditProductActivity.this, "Error loading product: " +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveProduct() {
        // Get input values
        String name = etProductName.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String category = etProductCategory.getText().toString().trim();
        String stockStr = etProductStock.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Name is required");
            etProductName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etProductDescription.setError("Description is required");
            etProductDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etProductPrice.setError("Price is required");
            etProductPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            etProductCategory.setError("Category is required");
            etProductCategory.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stockStr)) {
            etProductStock.setError("Stock quantity is required");
            etProductStock.requestFocus();
            return;
        }

        // Parse numeric values
        double price;
        int stock;

        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etProductPrice.setError("Invalid price format");
            etProductPrice.requestFocus();
            return;
        }

        try {
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            etProductStock.setError("Invalid stock format");
            etProductStock.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);

        // If there's a new image, upload it first
        if (imageUri != null) {
            firebaseHelper.uploadProductImage(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Create or update product with the new image URL
                            saveProductToDatabase(name, description, price, category, stock, imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddEditProductActivity.this, "Failed to upload image: " +
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // No new image, use existing image URL or null
            saveProductToDatabase(name, description, price, category, stock, currentImageUrl);
        }
    }

    private void saveProductToDatabase(String name, String description, double price,
                                       String category, int stock, String imageUrl) {
        // Create product object
        Product product;

        if (isEditMode) {
            // Update existing product
            product = new Product(productId, name, description, price, category, stock, imageUrl);
            product.setUpdatedAt(System.currentTimeMillis());

            firebaseHelper.updateProduct(product)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddEditProductActivity.this, "Product updated successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddEditProductActivity.this, "Failed to update product: " +
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Add new product
            product = new Product(null, name, description, price, category, stock, imageUrl);

            firebaseHelper.addProduct(product)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddEditProductActivity.this, "Product added successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddEditProductActivity.this, "Failed to add product: " +
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
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