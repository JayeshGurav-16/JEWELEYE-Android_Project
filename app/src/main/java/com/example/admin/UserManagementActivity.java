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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import com.example.admin.UserAdapter;
import com.example.admin.User;
import com.example.admin.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.UserActionListener {

    private RecyclerView recyclerViewUsers;
    private ProgressBar progressBar;
    private TextView tvNoUsers;
    private SearchView searchView;

    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> allUsersList;
    private FirebaseHelper firebaseHelper;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Initialize Firebase helper
        firebaseHelper = FirebaseHelper.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        progressBar = findViewById(R.id.progressBar);
        tvNoUsers = findViewById(R.id.tvNoUsers);
        searchView = findViewById(R.id.searchView);

        // Set up RecyclerView
        userList = new ArrayList<>();
        allUsersList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, this);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.getUsersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && !user.getUserId().equals(currentUserId)) {
                        // Don't show current admin in the list
                        allUsersList.add(user);
                    }
                }

                // Filter based on current search query
                String query = searchView.getQuery().toString().trim();
                if (!query.isEmpty()) {
                    filterUsers(query);
                } else {
                    userList.clear();
                    userList.addAll(allUsersList);
                    userAdapter.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);
                updateEmptyStateVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UserManagementActivity.this, "Failed to load users: " +
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        userList.clear();

        if (query.isEmpty()) {
            userList.addAll(allUsersList);
        } else {
            query = query.toLowerCase();
            for (User user : allUsersList) {
                if (user.getName().toLowerCase().contains(query) ||
                        user.getEmail().toLowerCase().contains(query)) {
                    userList.add(user);
                }
            }
        }

        userAdapter.notifyDataSetChanged();
        updateEmptyStateVisibility();
    }

    private void updateEmptyStateVisibility() {
        if (userList.isEmpty()) {
            tvNoUsers.setVisibility(View.VISIBLE);
            recyclerViewUsers.setVisibility(View.GONE);
        } else {
            tvNoUsers.setVisibility(View.GONE);
            recyclerViewUsers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBlockUnblockClicked(User user, int position) {
        boolean newActiveStatus = !user.isActive();
        String message = newActiveStatus ?
                "Are you sure you want to unblock this user?" :
                "Are you sure you want to block this user?";

        new AlertDialog.Builder(this)
                .setTitle(newActiveStatus ? "Unblock User" : "Block User")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateUserStatus(user, newActiveStatus, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void updateUserStatus(User user, boolean active, int position) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.updateUserStatus(user.getUserId(), active)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserManagementActivity.this,
                            "User " + (active ? "unblocked" : "blocked") + " successfully",
                            Toast.LENGTH_SHORT).show();

                    // Update local data
                    user.setActive(active);
                    userAdapter.notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserManagementActivity.this,
                            "Failed to update user status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteClicked(User user, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteUser(user);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUser(User user) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.deleteUser(user.getUserId())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserManagementActivity.this, "User deleted successfully",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserManagementActivity.this, "Failed to delete user: " +
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