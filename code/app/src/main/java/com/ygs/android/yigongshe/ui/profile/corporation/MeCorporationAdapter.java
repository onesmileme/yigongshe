package com.ygs.android.yigongshe.ui.profile.corporation;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeCorporationBean;
import com.ygs.android.yigongshe.view.GlideRoundTransform;
import java.util.List;

/**
 * Created by ruichao on 2018/7/26.
 */

public class MeCorporationAdapter
    extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
  /**
   * Same as QuickAdapter#QuickAdapter(Context,int) but with
   * some initialization data.
   *
   * @param data A new list is created out of this one to avoid mutable list
   */
  public MeCorporationAdapter(List data) {
    super(data);
    addItemType(MeCorporationBean.TYPE_ITEM_0, R.layout.item_corporation_0);
    addItemType(MeCorporationBean.TYPE_ITEM_1, R.layout.item_corporation_1);
    addItemType(MeCorporationBean.TYPE_ITEM_2, R.layout.item_corporation_2);
    addItemType(MeCorporationBean.TYPE_ITEM_3, R.layout.item_corporation_3);
    addItemType(MeCorporationBean.TYPE_ITEM_4, R.layout.item_dynamic);
  }

  @Override protected void convert(BaseViewHolder helper, MultiItemEntity item) {
    switch (helper.getItemViewType()) {
      case MeCorporationBean.TYPE_ITEM_1:
        helper.setText(R.id.tv1, ((MeCorporationBean.MeCorporationTransItemBean1) item).name1);
        break;
      case MeCorporationBean.TYPE_ITEM_2:
        helper.setText(R.id.tv1, ((MeCorporationBean.MeCorporationTransItemBean2) item).name1);
        helper.setText(R.id.tv2, ((MeCorporationBean.MeCorporationTransItemBean2) item).name2);
        break;
      case MeCorporationBean.TYPE_ITEM_3:
        helper.setText(R.id.tv1, ((MeCorporationBean.MeCorporationTransItemBean3) item).name1);
        helper.setText(R.id.tv2, ((MeCorporationBean.MeCorporationTransItemBean3) item).name2);
        helper.setText(R.id.tv3, ((MeCorporationBean.MeCorporationTransItemBean3) item).name3);
        break;
      case MeCorporationBean.TYPE_ITEM_4:
        MeCorporationBean.MeCorporationTransItemBean4 itemBean4 =
            (MeCorporationBean.MeCorporationTransItemBean4) item;
        Glide.with(mContext)
            .load(itemBean4.pic)
            .placeholder(R.drawable.loading2)
            .error(R.drawable.loading2)
            .fallback(R.drawable.loading2)
            .thumbnail(0.1f)
            .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
            .into((ImageView) helper.getView(R.id.img));

        helper.setText(R.id.title, itemBean4.title);
        helper.setText(R.id.time, itemBean4.create_at);
        helper.setText(R.id.content, itemBean4.desc);
        helper.addOnClickListener(R.id.container);
        break;
    }
  }
}
