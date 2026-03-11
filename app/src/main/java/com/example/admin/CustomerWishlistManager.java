package com.example.admin;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomerWishlistManager {
    private static final String PREF_NAME = "customer_wishlist";
    private static final String KEY_WISHLIST = "wishlist_items";

    private static CustomerWishlistManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private List<String> wishlistItems; // Store product IDs

    private CustomerWishlistManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadWishlistItems();
    }

    public static synchronized CustomerWishlistManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomerWishlistManager(context);
        }
        return instance;
    }

    private void loadWishlistItems() {
        String wishlistJson = sharedPreferences.getString(KEY_WISHLIST, null);
        if (wishlistJson != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            wishlistItems = gson.fromJson(wishlistJson, type);
        } else {
            wishlistItems = new ArrayList<>();
        }
    }

    private void saveWishlistItems() {
        String wishlistJson = gson.toJson(wishlistItems);
        sharedPreferences.edit().putString(KEY_WISHLIST, wishlistJson).apply();
    }

    public List<String> getWishlistItems() {
        return wishlistItems;
    }

    public void addToWishlist(String productId) {
        if (!isInWishlist(productId)) {
            wishlistItems.add(productId);
            saveWishlistItems();
        }
    }

    public void removeFromWishlist(String productId) {
        wishlistItems.remove(productId);
        saveWishlistItems();
    }

    public boolean isInWishlist(String productId) {
        return wishlistItems.contains(productId);
    }

    public void toggleWishlistItem(String productId) {
        if (isInWishlist(productId)) {
            removeFromWishlist(productId);
        } else {
            addToWishlist(productId);
        }
    }

    public int getWishlistCount() {
        return wishlistItems.size();
    }

    public void clearWishlist() {
        wishlistItems.clear();
        saveWishlistItems();
    }
}