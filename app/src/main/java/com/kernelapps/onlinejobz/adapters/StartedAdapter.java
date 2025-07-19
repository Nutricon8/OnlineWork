package com.kernelapps.onlinejobz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.models.StartedItem;

import java.util.List;

public class StartedAdapter extends RecyclerView.Adapter<StartedAdapter.ViewHolder> {

    private List<StartedItem> items;
    private Context context;

    public StartedAdapter(Context context, List<StartedItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.icon);
            titleView = itemView.findViewById(R.id.title);
        }
    }

    @Override
    public StartedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_started, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StartedAdapter.ViewHolder holder, int position) {
        StartedItem item = items.get(position);
        holder.imageView.setImageResource(item.getImageResId());
        holder.titleView.setText(item.getTitle());
        // Assign click listener that uses the passed-in function
        holder.itemView.setOnClickListener(v -> {
            if (item.getAction() != null) {
                item.getAction().run(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}