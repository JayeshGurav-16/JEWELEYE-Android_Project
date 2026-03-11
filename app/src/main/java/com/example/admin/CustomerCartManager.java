package com.example.admin;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomerCartManager {
    private static final String PREF_NAME = "customer_cart";
    private static final String KEY_CART = "cart_items";

    private static CustomerCartManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private List<CustomerCartItem> cartItems;

    private CustomerCartManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCartItems();
    }

    public static synchronized CustomerCartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomerCartManager(context);
        }
        return instance;
    }

    private void loadCartItems() {
        String cartJson = sharedPreferences.getString(KEY_CART, null);
        if (cartJson != null) {
            Type type = new TypeToken<ArrayList<CustomerCartItem>>() {}.getType();
            cartItems = gson.fromJson(cartJson, type);
        } else {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCartItems() {
        String cartJson = gson.toJson(cartItems);
        sharedPreferences.edit().putString(KEY_CART, cartJson).apply();
    }

    public List<CustomerCartItem> getCartItems() {
        return cartItems;
    }

    public void addToCart(String productId, String productName, String imageUrl, double price) {
        // Check if item already exists in cart
        for (CustomerCartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                // Increase quantity of existing item
                item.setQuantity(item.getQuantity() + 1);
                saveCartItems();
                return;
            }
        }

        // Add new item to cart
        CustomerCartItem newItem = new CustomerCartItem(productId, productName, imageUrl, price, 1);
        cartItems.add(newItem);
        saveCartItems();
    }

    public void updateQuantity(String productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeFromCart(productId);
            return;
        }

        for (CustomerCartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(newQuantity);
                saveCartItems();
                return;
            }
        }
    }

    public void removeFromCart(String productId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProductId().equals(productId)) {
                cartItems.remove(i);
                saveCartItems();
                return;
            }
        }
    }

    public void clearCart() {
        cartItems.clear();
        saveCartItems();
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (CustomerCartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }
}