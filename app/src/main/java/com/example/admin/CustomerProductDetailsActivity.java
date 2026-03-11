package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomerProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProductImage, ivWishlist;
    private TextView tvProductName, tvProductPrice, tvProductDescription, tvProductCategory, tvStockStatus;
    private Button btnAddToCart, btnBuyNow , tryon;
    private ProgressBar progressBar;

    private String productId;
    private CustomerProduct currentProduct;
    private CustomerFirebaseHelper firebaseHelper;
    private CustomerCartManager cartManager;
    public  static String productName;
    private boolean isInWishlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_details);

        // Get product ID from intent
        productId = getIntent().getStringExtra("PRODUCT_ID");
        if (productId == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize helpers
        firebaseHelper = CustomerFirebaseHelper.getInstance();
        cartManager = CustomerCartManager.getInstance(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Initialize UI components
        ivProductImage = findViewById(R.id.ivProductImage);
        ivWishlist = findViewById(R.id.ivWishlist);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductCategory = findViewById(R.id.tvProductCategory);
        tvStockStatus = findViewById(R.id.tvStockStatus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        progressBar = findViewById(R.id.progressBar);
        tryon = findViewById(R.id.tryon);

        // Set click listeners
        btnAddToCart.setOnClickListener(v -> addToCart());
        btnBuyNow.setOnClickListener(v -> buyNow());
        ivWishlist.setOnClickListener(v -> toggleWishlist());
        //tryon.setOnClickListener(v -> tryonf());

        // Load product details
        loadProductDetails();
        tryon.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerProductDetailsActivity.this, TryOnActivity.class);


            intent.putExtra("message_key", productName);
            startActivity(intent);
        });

    }

    private void loadProductDetails() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.getProductRef(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentProduct = snapshot.getValue(CustomerProduct.class);

                if (currentProduct != null) {
                    displayProductDetails(currentProduct);
                } else {
                    Toast.makeText(CustomerProductDetailsActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerProductDetailsActivity.this, "Error loading product: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Check if product is in wishlist
        updateWishlistIcon();
    }

    private void displayProductDetails(CustomerProduct product) {
        // Set product image
        // Set product image manually based on product name
        switch (product.getName().toLowerCase()) {
            case "ear ring":
                ivProductImage.setImageResource(R.drawable.earring);
                break;
            case "nose ring":
                ivProductImage.setImageResource(R.drawable.nose1);
                break;
            case "nose ring2":
                ivProductImage.setImageResource(R.drawable.nosering);
                break;
            case "necklace_cell":
                ivProductImage.setImageResource(R.drawable.nec1);
                break;
            case "rsjnecklace":
                ivProductImage.setImageResource(R.drawable.nec2);
                break;
            default:
                //ivProductImage.setImageResource(R.drawable.placeholder_image); // fallback image
                break;
        }


        // Set product details
        tvProductName.setText(product.getName());
        productName=product.getName();


        // Format and set price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        tvProductPrice.setText(currencyFormat.format(product.getPrice()));

        tvProductDescription.setText(product.getDescription());
        tvProductCategory.setText("Category: " + product.getCategory());

        // Set stock status
        if (product.getStock() > 10) {
            tvStockStatus.setText("In Stock");
            tvStockStatus.setTextColor(getResources().getColor(R.color.success_green));
        } else if (product.getStock() > 0) {
            tvStockStatus.setText("Only " + product.getStock() + " left");
            tvStockStatus.setTextColor(getResources().getColor(R.color.warning_orange));
        } else {
            tvStockStatus.setText("Out of Stock");
            tvStockStatus.setTextColor(getResources().getColor(R.color.error_red));
            btnAddToCart.setEnabled(false);
            btnBuyNow.setEnabled(false);
        }
    }

    private void addToCart() {
        if (currentProduct != null) {
            cartManager.addToCart(
                    currentProduct.getProductId(),
                    currentProduct.getName(),
                    currentProduct.getImageUrl(),
                    currentProduct.getPrice()
            );
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        }
    }
    private void buyNow() {
        if (currentProduct != null) {
            cartManager.addToCart(
                    currentProduct.getProductId(),
                    currentProduct.getName(),
                    currentProduct.getImageUrl(),
                    currentProduct.getPrice()
            );
            // Navigate to checkout
            startActivity(new Intent(this, CustomerCheckoutActivity.class)); // Note: Changed to CustomerCheckoutActivity
        }
    }


    private void toggleWishlist() {

    }

    private void updateWishlistIcon() {

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