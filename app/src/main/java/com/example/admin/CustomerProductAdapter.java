package com.example.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ProductViewHolder> {

    private Context context;
    private List<CustomerProduct> productList;
    private ProductClickListener listener;
    private boolean isHorizontal;
    private NumberFormat currencyFormat;

    public interface ProductClickListener {
        void onProductClick(CustomerProduct product, int position);
    }

    public CustomerProductAdapter(Context context, List<CustomerProduct> productList,
                                  ProductClickListener listener) {
        this(context, productList, listener, false);
    }

    public CustomerProductAdapter(Context context, List<CustomerProduct> productList,
                                  ProductClickListener listener, boolean isHorizontal) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.isHorizontal = isHorizontal;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = isHorizontal ? R.layout.item_product_horizontal : R.layout.item_product_grid;
        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CustomerProduct product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(currencyFormat.format(product.getPrice()));

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.imgProduct);
        }

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product, position);
            }
        });

        // Set click listener for Add to Cart button if it exists
        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(v -> {
                // Add to cart functionality
                // This would typically call a cart manager method
                // For example: cartManager.addToCart(product.getProductId(), product.getName(), product.getImageUrl(), product.getPrice());
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice;
        Button btnAddToCart; // May be null in some layouts

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart); // May be null in some layouts
        }
    }
}