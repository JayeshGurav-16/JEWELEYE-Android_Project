package com.example.admin;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.admin.CustomerProduct;
import com.example.admin.CustomerUser;

import java.util.HashMap;
import java.util.Map;

public class CustomerFirebaseHelper {
    private static CustomerFirebaseHelper instance;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private CustomerFirebaseHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public static synchronized CustomerFirebaseHelper getInstance() {
        if (instance == null) {
            instance = new CustomerFirebaseHelper();
        }
        return instance;
    }

    // Authentication methods
    public Task<AuthResult> signIn(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signUp(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public Task<Void> resetPassword(String email) {
        return firebaseAuth.sendPasswordResetEmail(email);
    }

    // Database references
    public DatabaseReference getUsersRef() {
        return firebaseDatabase.getReference("users");
    }

    public DatabaseReference getUserRef(String userId) {
        return firebaseDatabase.getReference("users").child(userId);
    }

    public DatabaseReference getProductsRef() {
        return firebaseDatabase.getReference("products");
    }

    public DatabaseReference getProductRef(String productId) {
        return firebaseDatabase.getReference("products").child(productId);
    }

    public DatabaseReference getOrdersRef() {
        return firebaseDatabase.getReference("orders");
    }

    public DatabaseReference getOrderRef(String orderId) {
        return firebaseDatabase.getReference("orders").child(orderId);
    }

    public DatabaseReference getCategoriesRef() {
        return firebaseDatabase.getReference("categories");
    }

    public DatabaseReference getBannersRef() {
        return firebaseDatabase.getReference("banners");
    }

    public DatabaseReference getReviewsRef() {
        return firebaseDatabase.getReference("reviews");
    }

    // Storage references
    public StorageReference getProductImagesRef() {
        return firebaseStorage.getReference("product_images");
    }

    public StorageReference getUserImagesRef() {
        return firebaseStorage.getReference("user_images");
    }

    // User profile operations
    public Task<Void> createUserProfile(CustomerUser user) {
        return getUserRef(user.getUserId()).setValue(user);
    }

    public Task<Void> updateUserProfile(String userId, Map<String, Object> updates) {
        return getUserRef(userId).updateChildren(updates);
    }

    public Task<Void> updateUserLastLogin(String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastLoginAt", System.currentTimeMillis());
        return getUserRef(userId).updateChildren(updates);
    }

    // Order operations
    public Task<Void> placeOrder(String orderId, Object orderData) {
        return getOrderRef(orderId).setValue(orderData);
    }

    // Upload image
    public UploadTask uploadImage(Uri imageUri, StorageReference storageRef) {
        return storageRef.putFile(imageUri);
    }

    // Add product review
    public Task<Void> addProductReview(String productId, String reviewId, Object reviewData) {
        return getReviewsRef().child(productId).child(reviewId).setValue(reviewData);
    }

}