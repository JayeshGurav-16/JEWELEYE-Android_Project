package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomerCheckoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etName, etEmail, etPhone, etAddress, etCity, etPostalCode;
    private RadioGroup rgPaymentMethod;
    private TextView tvSubtotal, tvShipping, tvTotal;
    private Button btnPlaceOrder;

    private CustomerCartManager cartManager;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_checkout);

        cartManager = CustomerCartManager.getInstance(this);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etPostalCode = findViewById(R.id.etPostalCode);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        updateOrderSummary();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void updateOrderSummary() {
        double subtotal = cartManager.getSubtotal();
        double shippingCost = calculateShippingCost(subtotal);
        double total = subtotal + shippingCost;

        tvSubtotal.setText(currencyFormat.format(subtotal));
        tvShipping.setText(currencyFormat.format(shippingCost));
        tvTotal.setText(currencyFormat.format(total));
    }

    private double calculateShippingCost(double subtotal) {
        return subtotal >= 100 ? 0 : 5.99;
    }

    private void placeOrder() {
        if (!validateForm()) {
            return;
        }

        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        RadioButton selectedPayment = findViewById(selectedPaymentId);
        String paymentMethod = selectedPayment.getText().toString();

        // Clear the cart
        cartManager.clearCart();

        // Prepare result intent for fragment
        Intent resultIntent = new Intent();
        resultIntent.putExtra("order_success", true);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Order placed successfully! Payment: " + paymentMethod, Toast.LENGTH_LONG).show();

        // Finish and return to cart fragment
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            valid = false;
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email is required");
            valid = false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone is required");
            valid = false;
        }

        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Address is required");
            valid = false;
        }

        if (etCity.getText().toString().trim().isEmpty()) {
            etCity.setError("City is required");
            valid = false;
        }

        if (etPostalCode.getText().toString().trim().isEmpty()) {
            etPostalCode.setError("Postal code is required");
            valid = false;
        }

        if (rgPaymentMethod.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
