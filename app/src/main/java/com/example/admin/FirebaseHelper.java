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
import com.example.admin.Product;
import com.example.admin.User;

import java.util.UUID;

public class FirebaseHelper {
    private static FirebaseHelper instance;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private FirebaseHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
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

    // Storage references
    public StorageReference getProductImagesRef() {
        return firebaseStorage.getReference("product_images");
    }

    // Upload product image
    public UploadTask uploadProductImage(Uri imageUri) {
        String filename = UUID.randomUUID().toString();
        return getProductImagesRef().child(filename).putFile(imageUri);
    }

    // CRUD operations for users
    public Task<Void> createUserProfile(User user) {
        return getUserRef(user.getUserId()).setValue(user);
    }

    public Task<Void> updateUserStatus(String userId, boolean active) {
        return getUserRef(userId).child("active").setValue(active);
    }

    public Task<Void> deleteUser(String userId) {
        return getUserRef(userId).removeValue();
    }

    // CRUD operations for products
    public Task<Void> addProduct(Product product) {
        // Generate a product ID
        String productId = getProductsRef().push().getKey();
        product.setProductId(productId);

        return getProductRef(productId).setValue(product);
    }

    public Task<Void> updateProduct(Product product) {
        return getProductRef(product.getProductId()).setValue(product);
    }

    public Task<Void> deleteProduct(String productId) {
        return getProductRef(productId).removeValue();
    }

    // Order status update
    public Task<Void> updateOrderStatus(String orderId, String newStatus) {
        return getOrderRef(orderId).child("status").setValue(newStatus);
    }
}
