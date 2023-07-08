package com.example.imagepro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageSelectionAdapter extends RecyclerView.Adapter<ImageSelectionAdapter.OptionViewHolder> {

    private final List<ImageSelectionOption> options;

    private final OnOptionClickListener onOptionClickListener;

    public ImageSelectionAdapter(List<ImageSelectionOption> options, OnOptionClickListener onOptionClickListener) {
        this.options = options;
        this.onOptionClickListener = onOptionClickListener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_selection, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        ImageSelectionOption option = options.get(position);

        holder.iconImageView.setImageResource(option.getIconResId());
        holder.optionTextView.setText(option.getOptionText());

        holder.itemView.setOnClickListener(v -> {
            if (onOptionClickListener != null) {
                onOptionClickListener.onOptionClick(option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class OptionViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView optionTextView;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            optionTextView = itemView.findViewById(R.id.optionTextView);
        }
    }

    public interface OnOptionClickListener {
        void onOptionClick(ImageSelectionOption option);
    }
}

