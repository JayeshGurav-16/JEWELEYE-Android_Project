package com.example.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CustomerCategoryAdapter extends RecyclerView.Adapter<CustomerCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<CustomerCategory> categoryList;
    private CategoryClickListener listener;

    public interface CategoryClickListener {
        void onCategoryClick(CustomerCategory category, int position);
    }

    public CustomerCategoryAdapter(Context context, List<CustomerCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public CustomerCategoryAdapter(Context context, List<CustomerCategory> categoryList, CategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CustomerCategory category = categoryList.get(position);

        holder.tvCategoryName.setText(category.getName());

        // Load category image with Glide
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(category.getImageUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.imgCategory);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}