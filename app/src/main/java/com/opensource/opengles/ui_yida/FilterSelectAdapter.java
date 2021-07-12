package com.opensource.opengles.ui_yida;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.opensource.opengles.R;
import com.opensource.opengles.ui_yida.bean.FilterBean;

import java.util.List;


public class FilterSelectAdapter extends RecyclerView.Adapter<FilterSelectAdapter.ItemViewHolder> {

    private final List<FilterBean> dataList;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private int currentPosition;

    public FilterSelectAdapter(List<FilterBean> dataList) {
        this.dataList = dataList;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView colorImage;
        View ifa_select_line;
        TextView text;

        public ItemViewHolder(View view) {
            super(view);
            colorImage = view.findViewById(R.id.ifa_color_image);
            ifa_select_line = view.findViewById(R.id.ifa_select_line);
            text = view.findViewById(R.id.ifa_text);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder h, int position) {
        if (position == currentPosition) {
            h.ifa_select_line.setVisibility(View.VISIBLE);
            h.text.setBackgroundResource(R.drawable.bg_keycolor_conner_8_half_bottom);
            if (currentPosition == 0) {
                h.colorImage.setImageResource(R.mipmap.detail_filter_p);
            } else {
                h.colorImage.setImageResource(dataList.get(position).getImgResource());
            }
        } else {
            h.ifa_select_line.setVisibility(View.GONE);
            h.text.setBackgroundResource(R.color.text_back_color_p);
            h.colorImage.setImageResource(dataList.get(position).getImgResource());
        }

        h.text.setText(dataList.get(position).getName());
        h.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) onItemClickListener.onItemClick(position);
            currentPosition = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
