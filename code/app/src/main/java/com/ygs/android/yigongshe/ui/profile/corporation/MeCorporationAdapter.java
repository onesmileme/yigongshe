package com.ygs.android.yigongshe.ui.profile.corporation;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeCorporationBean;
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
    }
  }
}
