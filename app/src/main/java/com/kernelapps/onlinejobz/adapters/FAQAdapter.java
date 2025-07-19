package com.kernelapps.onlinejobz.adapters;

import android.app.Activity;
import android.content.Context;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.kernelapps.onlinejobz.R;
import com.kernelapps.onlinejobz.models.FAQ;
import com.kernelapps.onlinejobz.utils.AdsManager;

import java.util.List;


public class FAQAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<FAQ> faqList;
    private final Context context;

    public FAQAdapter(Context context, List<FAQ> faqList) {
        this.context = context;
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final FAQ faq = faqList.get(position);
        FAQViewHolder viewHolder = (FAQViewHolder) holder;

        viewHolder.question.setText(faq.getQuestion());
        viewHolder.answer.setText(faq.getAnswer());

        boolean isExpanded = faq.isExpanded();
        viewHolder.answer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewHolder.adContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewHolder.indicator.setImageDrawable(
                AppCompatResources.getDrawable(
                        context,
                        isExpanded ? R.drawable.baseline_indeterminate_check_box_24 : R.drawable.baseline_add_box_24
                )
        );

        if (isExpanded) {
            viewHolder.adContainer.removeAllViews(); // Prevent duplicated banners
            AdsManager.loadAndShowBanner((Activity) context, viewHolder.adContainer);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            faq.setExpanded(!faq.isExpanded());
            TransitionManager.beginDelayedTransition((ViewGroup) viewHolder.itemView);
            notifyItemChanged(position);
        });
    }



    @Override
    public int getItemCount() {
        return faqList.size();
    }


    static class FAQViewHolder extends RecyclerView.ViewHolder {
        private final TextView question;
        private final TextView answer;
        private final ImageView indicator;
        private final FrameLayout adContainer;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            indicator = itemView.findViewById(R.id.indicator);
            adContainer = itemView.findViewById(R.id.faq_banner_ad_container);
        }
    }
}

