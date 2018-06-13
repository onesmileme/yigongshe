package com.weikan.app.original.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.weikan.app.R;
import com.weikan.app.original.bean.TweetRelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/8
 */
public class TweetInnoListAdapter extends RecyclerView.Adapter<TweetInnoListAdapter.InnerViewHolder> {

    @NonNull
    List<TweetRelObject> items = new ArrayList<>();

    InnerViewHolder.OnClickListener onClickListener = null;

    public void setOnItemClickListener(InnerViewHolder.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.widget_tweet_inno_list_item_view, parent, false);
        final InnerViewHolder viewHolder = new InnerViewHolder(item);
        viewHolder.setOnClickListener(new InnerViewHolder.OnClickListener() {
            @Override
            public void onClick(InnerViewHolder viewHolder) {
                if (onClickListener != null) {
                    onClickListener.onClick(viewHolder);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InnerViewHolder holder, int position) {
        TweetRelObject item = this.items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void setItems(@NonNull List<TweetRelObject> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public static class InnerViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TweetRelObject item;
        Bitmap bm;

        public interface OnClickListener {
            void onClick(InnerViewHolder viewHolder);
        }

        private OnClickListener listener;

        public void setOnClickListener(OnClickListener listener) {
            this.listener = listener;
        }

        private View.OnClickListener onItemViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(InnerViewHolder.this);
                }
            }
        };

        public InnerViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            itemView.setOnClickListener(onItemViewClickListener);
        }

        public void setItem(@Nullable TweetRelObject item) {
            this.item = item;

            bm = null;
            if (item != null && item.pic != null && item.pic.t != null) {
                Picasso.with(itemView.getContext())
                        .load(item.pic.t.url)
                        .into(iv);
            } else {
                iv.setImageResource(0);
            }
        }

        public TweetRelObject getItem() {
            return this.item;
        }

        @Nullable
        public Bitmap getBitmap() {
            return bm;
        }
    }
}
