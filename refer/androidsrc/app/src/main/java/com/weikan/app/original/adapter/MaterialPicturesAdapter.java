package com.weikan.app.original.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.weikan.app.R;
import com.weikan.app.original.bean.ImageNtsObject;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/8
 */
public class MaterialPicturesAdapter extends RecyclerView.Adapter<MaterialPicturesAdapter.MaterialPictureViewHolder> {

    @NonNull
    List<ImageNtsObject> items = new ArrayList<>();

    MaterialPictureViewHolder.OnClickListener onClickListener = null;

    public void setOnMaterialPictureClickListener(MaterialPictureViewHolder.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public MaterialPictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.widget_material_picture_view, parent, false);
        final MaterialPictureViewHolder viewHolder = new MaterialPictureViewHolder(item);
        viewHolder.setOnClickListener(new MaterialPictureViewHolder.OnClickListener() {
            @Override
            public void onClick(MaterialPictureViewHolder viewHolder) {
                if (onClickListener != null) {
                    onClickListener.onClick(viewHolder);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MaterialPictureViewHolder holder, int position) {
        ImageNtsObject item = this.items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void setItems(@NonNull List<ImageNtsObject> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public static class MaterialPictureViewHolder extends RecyclerView.ViewHolder implements Target {

        ImageView iv;
        ImageNtsObject item;
        Bitmap bm;

        public interface OnClickListener {
            void onClick(MaterialPictureViewHolder viewHolder);
        }

        private OnClickListener listener;

        public void setOnClickListener(OnClickListener listener) {
            this.listener = listener;
        }

        private View.OnClickListener onItemViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(MaterialPictureViewHolder.this);
                }
            }
        };

        public MaterialPictureViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            itemView.setOnClickListener(onItemViewClickListener);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bm = bitmap;
            iv.setImageBitmap(bm);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        public void setItem(@Nullable ImageNtsObject item) {
            this.item = item;

            bm = null;
            if (item != null) {
                Picasso.with(itemView.getContext())
                        .load(item.n.url)
                        .into(this);
            } else {
                iv.setImageResource(0);
            }
        }

        public ImageNtsObject getItem() {
            return this.item;
        }

        @Nullable
        public Bitmap getBitmap() {
            return bm;
        }
    }
}
