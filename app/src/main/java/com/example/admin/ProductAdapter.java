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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private ProductActionListener listener;

    public ProductAdapter(Context context, List<Product> productList, ProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product name
        holder.tvProductName.setText(product.getName());

        // Set product price
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvProductPrice.setText(currencyFormat.format(product.getPrice()));

        // Set product category
        holder.tvProductCategory.setText(product.getCategory());

        // Set product stock
        holder.tvProductStock.setText("Stock: " + product.getStock());

        // Load product image if available
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // If you have the ImageUtils class, use it here
            // ImageUtils.loadImageFromUrl(context, product.getImageUrl(), holder.ivProductImage);

            // Otherwise, you can use a basic image loading library or show a placeholder
            // For now, we'll just use the placeholder
        }

        // Set click listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClicked(product);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(product, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvProductCategory, tvProductStock;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface ProductActionListener {
        void onEditClicked(Product product);
        void onDeleteClicked(Product product, int position);
    }
}