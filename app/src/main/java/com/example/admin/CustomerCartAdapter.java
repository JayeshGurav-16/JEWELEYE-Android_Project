package com.example.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerCartAdapter extends RecyclerView.Adapter<CustomerCartAdapter.CartViewHolder> {

    private Context context;
    private List<CustomerCartItem> cartItems;
    private CartActionListener actionListener;
    private NumberFormat currencyFormat;

    public interface CartActionListener {
        void onQuantityChanged(String productId, int newQuantity);
        void onRemoveItem(String productId);
    }

    public CustomerCartAdapter(Context context, List<CustomerCartItem> cartItems, CartActionListener actionListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.actionListener = actionListener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CustomerCartItem item = cartItems.get(position);

        // Set product name
        holder.tvProductName.setText(item.getProductName());

        // Set price
        holder.tvPrice.setText(currencyFormat.format(item.getPrice()));

        // Set quantity
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Set subtotal
        holder.tvSubtotal.setText(currencyFormat.format(item.getSubtotal()));

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_product)
                .into(holder.imgProduct);

        // Set click listeners
        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity >= 1) {
                actionListener.onQuantityChanged(item.getProductId(), newQuantity);
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            actionListener.onQuantityChanged(item.getProductId(), newQuantity);
        });

        holder.btnRemove.setOnClickListener(v -> {
            actionListener.onRemoveItem(item.getProductId());
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CustomerCartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvPrice, tvQuantity, tvSubtotal;
        ImageButton btnDecrease, btnIncrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}