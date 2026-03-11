    package com.example.admin;

    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Handler;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ProgressBar;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.GridLayoutManager;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import androidx.viewpager2.widget.ViewPager2;

    import com.google.android.material.tabs.TabLayout;
    import com.google.android.material.tabs.TabLayoutMediator;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.Query;
    import com.google.firebase.database.ValueEventListener;
    import com.example.admin.CustomerProductDetailsActivity;
    import com.example.admin.R;
    import com.example.admin.CustomerBannerSliderAdapter;
    import com.example.admin.CustomerCategoryAdapter;
    import com.example.admin.CustomerProductAdapter;
    import com.example.admin.CustomerBanner;
    import com.example.admin.CustomerCategory;
    import com.example.admin.CustomerProduct;
    import com.example.admin.CustomerFirebaseHelper;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;

    public class CustomerHomeFragment extends Fragment implements CustomerProductAdapter.ProductClickListener {

        private ViewPager2 viewPagerBanners;
        private TabLayout tabLayoutBannerIndicator;
        private RecyclerView recyclerViewCategories, recyclerViewFeatured, recyclerViewNewArrivals;
        private ProgressBar progressBar;
        private TextView tvViewAllFeatured, tvViewAllNewArrivals;

        private CustomerBannerSliderAdapter bannerAdapter;
        private CustomerCategoryAdapter categoryAdapter;
        private CustomerProductAdapter featuredProductsAdapter;
        private CustomerProductAdapter newArrivalsAdapter;

        private CustomerFirebaseHelper firebaseHelper;
        private Handler sliderHandler = new Handler();
        private List<CustomerBanner> bannerList;
        private List<CustomerCategory> categoryList;
        private List<CustomerProduct> featuredProducts;
        private List<CustomerProduct> newArrivalProducts;

        private Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPagerBanners != null) {
                    int currentItem = viewPagerBanners.getCurrentItem();
                    int bannerCount = bannerList.size();

                    if (bannerCount > 1) {
                        viewPagerBanners.setCurrentItem((currentItem + 1) % bannerCount);
                    }
                }
                sliderHandler.postDelayed(this, 3000); // Auto-slide every 3 seconds
            }
        };

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_customer_home, container, false);

            // Initialize Firebase helper
            firebaseHelper = CustomerFirebaseHelper.getInstance();

            // Initialize UI components
            viewPagerBanners = view.findViewById(R.id.viewPagerBanners);
            tabLayoutBannerIndicator = view.findViewById(R.id.tabLayoutBannerIndicator);
            recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
            recyclerViewFeatured = view.findViewById(R.id.recyclerViewFeatured);
            recyclerViewNewArrivals = view.findViewById(R.id.recyclerViewNewArrivals);
            progressBar = view.findViewById(R.id.progressBar);
            tvViewAllFeatured = view.findViewById(R.id.tvViewAllFeatured);
            tvViewAllNewArrivals = view.findViewById(R.id.tvViewAllNewArrivals);

            // Initialize lists
            bannerList = new ArrayList<>();
            categoryList = new ArrayList<>();
            featuredProducts = new ArrayList<>();
            newArrivalProducts = new ArrayList<>();

            // Set up banner slider
            bannerAdapter = new CustomerBannerSliderAdapter(getContext(), bannerList);
            viewPagerBanners.setAdapter(bannerAdapter);

            // Setup category recycler view with horizontal layout
            categoryAdapter = new CustomerCategoryAdapter(getContext(), categoryList);
            recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewCategories.setAdapter(categoryAdapter);

            // Setup featured products with grid layout (2 columns)
            featuredProductsAdapter = new CustomerProductAdapter(getContext(), featuredProducts, this);
            recyclerViewFeatured.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerViewFeatured.setAdapter(featuredProductsAdapter);

            // Setup new arrivals with horizontal layout
            newArrivalsAdapter = new CustomerProductAdapter(getContext(), newArrivalProducts, this, true);
            recyclerViewNewArrivals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewNewArrivals.setAdapter(newArrivalsAdapter);

            // Set click listeners for "View All" buttons
            tvViewAllFeatured.setOnClickListener(v -> {
                // Navigate to products fragment with featured filter
                // Implementation depends on your navigation setup
            });
            tvViewAllNewArrivals.setOnClickListener(v -> {
                // Navigate to products fragment with new arrivals filter
                // Implementation depends on your navigation setup
            });

            // Load content
            loadBanners();
            loadCategories();
            loadFeaturedProducts();
            loadNewArrivals();

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            startBannerSlideshow();
        }

        @Override
        public void onPause() {
            super.onPause();
            stopBannerSlideshow();
        }

        private void startBannerSlideshow() {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }

        private void stopBannerSlideshow() {
            sliderHandler.removeCallbacks(sliderRunnable);
        }

        private void loadBanners() {
            progressBar.setVisibility(View.VISIBLE);

            // Example of loading banners from Firebase
            firebaseHelper.getBannersRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bannerList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomerBanner banner = snapshot.getValue(CustomerBanner.class);
                        if (banner != null && banner.isActive()) {
                            bannerList.add(banner);
                        }
                    }

                    bannerAdapter.notifyDataSetChanged();

                    // Set up tab indicator if there are banners
                    if (bannerList.size() > 0) {
                        new TabLayoutMediator(tabLayoutBannerIndicator, viewPagerBanners,
                                (tab, position) -> {
                                    // No text needed for indicators
                                }).attach();
                    }

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    // Handle error
                }
            });
        }

        private void loadCategories() {
            firebaseHelper.getCategoriesRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    categoryList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomerCategory category = snapshot.getValue(CustomerCategory.class);
                        if (category != null) {
                            categoryList.add(category);
                        }
                    }

                    categoryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

        private void loadFeaturedProducts() {
            Query query = firebaseHelper.getProductsRef().orderByChild("featured").equalTo(true).limitToFirst(6);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    featuredProducts.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomerProduct product = snapshot.getValue(CustomerProduct.class);
                        if (product != null && product.getStock() > 0) {
                            featuredProducts.add(product);
                        }
                    }

                    featuredProductsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

        private void loadNewArrivals() {
            Query query = firebaseHelper.getProductsRef().orderByChild("createdAt").limitToLast(8);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    newArrivalProducts.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomerProduct product = snapshot.getValue(CustomerProduct.class);
                        if (product != null && product.getStock() > 0) {
                            newArrivalProducts.add(product);
                        }
                    }

                    // Sort by creation date (newest first)
                    Collections.sort(newArrivalProducts,
                            (p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));

                    newArrivalsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

        @Override
        public void onProductClick(CustomerProduct product, int position) {
            // Navigate to product details
            Intent intent = new Intent(getActivity(), CustomerProductDetailsActivity.class);
            intent.putExtra("PRODUCT_ID", product.getProductId());
            startActivity(intent);
        }
    }