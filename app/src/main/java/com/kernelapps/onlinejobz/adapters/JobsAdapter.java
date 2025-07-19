package com.kernelapps.onlinejobz.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.models.JobItem;
import com.kernelapps.onlinejobz.utils.AdsManager;
import com.kernelapps.onlinejobz.utils.FavoritesManager;

import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnJobClickListener {
        void onJobClick(JobItem jobItem);
        void onFavoriteClick(JobItem jobItem, int position);
    }

    private static final int VIEW_TYPE_JOB = 0;
    private static final int VIEW_TYPE_BANNER = 1;

    private final Context context;
    private final List<JobItem> jobItems;
    private final OnJobClickListener listener;

    public JobsAdapter(Context context, List<JobItem> jobItems, OnJobClickListener listener) {
        this.context = context;
        this.jobItems = jobItems;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        int originalCount = jobItems.size();
        int numberOfBanners = originalCount / 5;
        return originalCount + numberOfBanners;
    }

    @Override
    public int getItemViewType(int position) {
        return ((position + 1) % 6 == 0) ? VIEW_TYPE_BANNER : VIEW_TYPE_JOB;
    }

    private int getRealItemPosition(int position) {
        return position - (position / 6);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BANNER) {
            View bannerView = LayoutInflater.from(context).inflate(R.layout.item_banner_ad, parent, false);
            return new BannerViewHolder(bannerView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
            return new JobViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_BANNER) {
            ((BannerViewHolder) holder).bind(context);
        } else {
            int actualPosition = getRealItemPosition(position);
            JobItem item = jobItems.get(actualPosition);
            ((JobViewHolder) holder).bind(item, listener);
        }
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView image;
        private final ImageView favoriteIcon;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }

        public void bind(JobItem jobItem, OnJobClickListener listener) {
            title.setText(jobItem.getTitle());
            Glide.with(itemView.getContext()).load(jobItem.getImage()).into(image);

            boolean isFavorite = FavoritesManager.isFavorite(itemView.getContext(), jobItem.getId());
            favoriteIcon.setImageResource(
                    isFavorite ? R.drawable.round_favorite_24 : R.drawable.round_favorite_border_24
            );

            itemView.setOnClickListener(v -> {
                // Show interstitial ad first
                if (itemView.getContext() instanceof Activity) {
                    AdsManager.showInterstitialAd((Activity) itemView.getContext());
                }

                if (listener != null) {
                    listener.onJobClick(jobItem);
                }
            });

            favoriteIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(jobItem, position);
                }
            });
        }
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        FrameLayout adContainer;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            adContainer = itemView.findViewById(R.id.banner_ad_container);
        }

        public void bind(Context context) {
            adContainer.removeAllViews();
            AdsManager.loadAndShowBanner((Activity) context, adContainer);
        }
    }

    public void updateJobs(List<JobItem> newJobItems) {
        jobItems.clear();
        jobItems.addAll(newJobItems);
        notifyDataSetChanged();
    }
}
