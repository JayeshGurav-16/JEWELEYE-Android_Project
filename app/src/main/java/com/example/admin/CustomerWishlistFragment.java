package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerWishlistFragment extends Fragment implements CustomerProductAdapter.ProductClickListener {

    private RecyclerView recyclerViewWishlist;
    private TextView tvEmptyWishlist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_wishlist, container, false);

        recyclerViewWishlist = view.findViewById(R.id.recyclerViewWishlist);
        tvEmptyWishlist = view.findViewById(R.id.tvEmptyWishlist);

        // Set up RecyclerView with grid layout (2 columns)
        recyclerViewWishlist.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Load wishlist items
        loadWishlistItems();

        return view;
    }

    private void loadWishlistItems() {
        // This would typically fetch wishlist items from your WishlistManager
        // For now, we'll just show an empty state

        List<CustomerProduct> wishlistItems = new ArrayList<>();

        if (wishlistItems.isEmpty()) {
            recyclerViewWishlist.setVisibility(View.GONE);
            tvEmptyWishlist.setVisibility(View.VISIBLE);
        } else {
            recyclerViewWishlist.setVisibility(View.VISIBLE);
            tvEmptyWishlist.setVisibility(View.GONE);

            // Set adapter with wishlist items
            recyclerViewWishlist.setAdapter(new CustomerProductAdapter(getContext(), wishlistItems, this));
        }
    }

    @Override
    public void onProductClick(CustomerProduct product, int position) {
        // Navigate to product details
        Intent intent = new Intent(getActivity(), CustomerProductDetailsActivity.class);
        intent.putExtra("PRODUCT_ID", product.getProductId());
        startActivity(intent);
    }
}