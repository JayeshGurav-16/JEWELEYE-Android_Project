package com.example.admin;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.admin.R;
public class ImageUtils {

    public static void loadImageFromUrl(Context context, String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.image_placeholder_background)
                            .error(R.drawable.image_placeholder_background))
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.image_placeholder_background);
        }
    }

    public static void loadImageFromUri(Context context, Uri uri, ImageView imageView) {
        if (uri != null) {
            Glide.with(context)
                    .load(uri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.image_placeholder_background)
                            .error(R.drawable.image_placeholder_background))
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.image_placeholder_background);
        }
    }
}