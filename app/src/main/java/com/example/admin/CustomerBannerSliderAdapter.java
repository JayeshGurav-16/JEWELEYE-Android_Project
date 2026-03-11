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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CustomerBannerSliderAdapter extends RecyclerView.Adapter<CustomerBannerSliderAdapter.BannerViewHolder> {

    private Context context;
    private List<CustomerBanner> bannerList;

    public CustomerBannerSliderAdapter(Context context, List<CustomerBanner> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_slider, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        CustomerBanner banner = bannerList.get(position);

        // Load image with Glide
        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(banner.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                    .into(holder.imgBanner);
        }

        // Set banner title and description if they exist
        if (banner.getTitle() != null && !banner.getTitle().isEmpty()) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(banner.getTitle());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        if (banner.getDescription() != null && !banner.getDescription().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(banner.getDescription());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Set click listener if the banner has a link
        if (banner.getLinkUrl() != null && !banner.getLinkUrl().isEmpty()) {
            holder.itemView.setOnClickListener(v -> {
                // Handle banner click - could open a URL, a product page, etc.
                // You could implement this based on your app's requirements
            });
        }
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView tvTitle, tvDescription;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            tvTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvDescription = itemView.findViewById(R.id.tvBannerDescription);
        }
    }
}