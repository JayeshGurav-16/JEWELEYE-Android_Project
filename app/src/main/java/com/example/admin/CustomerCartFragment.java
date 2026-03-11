package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.CustomerCheckoutActivity;
import com.example.admin.R;
import com.example.admin.CustomerCartAdapter;
import com.example.admin.CustomerCartItem;
import com.example.admin.CustomerCartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerCartFragment extends Fragment implements CustomerCartAdapter.CartActionListener {

    private RecyclerView recyclerViewCart;
    private TextView tvEmptyCart, tvSubtotal, tvShipping, tvTotal;
    private Button btnCheckout;

    private CustomerCartAdapter cartAdapter;
    private CustomerCartManager cartManager;

    private static final int CHECKOUT_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_cart, container, false);

        // Initialize CartManager
        cartManager = CustomerCartManager.getInstance(getContext());

        // Initialize UI components
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvShipping = view.findViewById(R.id.tvShipping);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Set up cart adapter and recycler view
        List<CustomerCartItem> cartItems = cartManager.getCartItems();
        cartAdapter = new CustomerCartAdapter(getContext(), cartItems, this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);

        // Set checkout button click listener
        btnCheckout.setOnClickListener(v -> navigateToCheckout());

        // Display cart items or empty state
        updateCartView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart view when returning to this fragment
        updateCartView();
    }

    private void updateCartView() {
        List<CustomerCartItem> cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            recyclerViewCart.setVisibility(View.GONE);
            tvSubtotal.setText("Rs0.00");
            tvShipping.setText("Rs0.00");
            tvTotal.setText("Rs0.00");
            btnCheckout.setEnabled(false);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            recyclerViewCart.setVisibility(View.VISIBLE);

            // Update cart item list
            cartAdapter.updateCartItems(cartItems);

            // Calculate and display totals
            double subtotal = cartManager.getSubtotal();
            double shippingCost = calculateShippingCost(subtotal);
            double total = subtotal + shippingCost;

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

            tvSubtotal.setText(currencyFormat.format(subtotal));
            tvShipping.setText(currencyFormat.format(shippingCost));
            tvTotal.setText(currencyFormat.format(total));

            btnCheckout.setEnabled(true);
        }
    }

    private double calculateShippingCost(double subtotal) {
        // Free shipping on orders over 100 Rs (adjust as needed)
        return subtotal >= 100 ? 0 : 5.99;
    }

    private void navigateToCheckout() {
        Intent intent = new Intent(getActivity(), CustomerCheckoutActivity.class);
        startActivityForResult(intent, CHECKOUT_REQUEST_CODE);
    }

    @Override
    public void onQuantityChanged(String productId, int newQuantity) {
        cartManager.updateQuantity(productId, newQuantity);
        updateCartView();
    }

    @Override
    public void onRemoveItem(String productId) {
        cartManager.removeFromCart(productId);
        updateCartView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHECKOUT_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            boolean orderPlaced = data.getBooleanExtra("order_success", false);
            if (orderPlaced) {
                Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                cartManager.clearCart();  // Optional: clear the cart after order
                updateCartView();
            }
        }
    }
}
