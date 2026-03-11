package com.example.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrdersFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private TextView tvEmptyOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_orders, container, false);

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        tvEmptyOrders = view.findViewById(R.id.tvEmptyOrders);

        // Set up RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load orders
        loadOrders();

        return view;
    }

    private void loadOrders() {
        // This would typically fetch orders from Firebase or another data source
        // For now, we'll just show an empty state

        List<CustomerOrder> orders = new ArrayList<>();

        if (orders.isEmpty()) {
            recyclerViewOrders.setVisibility(View.GONE);
            tvEmptyOrders.setVisibility(View.VISIBLE);
        } else {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setVisibility(View.GONE);

            // Set adapter with orders
            // recyclerViewOrders.setAdapter(new CustomerOrderAdapter(getContext(), orders));
        }
    }
}