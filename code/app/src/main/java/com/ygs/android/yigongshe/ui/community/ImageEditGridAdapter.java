package com.ygs.android.yigongshe.ui.community;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;

public class ImageEditGridAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

  private Context mContext;

  public ImageEditGridAdapter(Context context) {
    super(R.layout.item_edit_image, null);
    mContext = context;
  }

  @Override protected void convert(BaseViewHolder helper, String item) {
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  public void onBindViewHolder(BaseViewHolder holder, int position) {
    if (position == getItemCount() - 1) {
      holder.setImageDrawable(R.id.item_image, mContext.getDrawable(R.drawable.add_pic));
      holder.setVisible(R.id.iv_delete, false);
    } else {
      holder.setVisible(R.id.iv_delete, true);
    }
  }
}
