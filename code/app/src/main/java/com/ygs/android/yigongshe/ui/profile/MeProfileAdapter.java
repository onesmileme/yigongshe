package com.ygs.android.yigongshe.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeItemBean;
import com.ygs.android.yigongshe.bean.UserInfoBean;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.activity.MeActivitiesActivity;
import com.ygs.android.yigongshe.ui.profile.app.MeMyAppActivity;
import com.ygs.android.yigongshe.ui.profile.apply.MeApplyActivity;
import com.ygs.android.yigongshe.ui.profile.charitytime.MeCharityTimeActivity;
import com.ygs.android.yigongshe.ui.profile.community.MeCommunityActivity;
import com.ygs.android.yigongshe.ui.profile.corporation.MeCorporationActivity;
import com.ygs.android.yigongshe.ui.profile.focus.MeFocusActivity;
import com.ygs.android.yigongshe.utils.DensityUtil;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;
import com.ygs.android.yigongshe.view.CircleImageView;
import java.util.LinkedList;
import java.util.List;

public class MeProfileAdapter extends BaseQuickAdapter<MeItemBean, BaseViewHolder> {

  List<MeItemBean> beans;

  Context mContext;

  private View headView;

  public MeProfileAdapter(Context context) {

    super(R.layout.item_me_profile, null);
    initBeans();
    this.setNewData(beans);
    mContext = context;

    headView = LayoutInflater.from(context).inflate(R.layout.view_me_info, null);
    int height = DensityUtil.dp2px(context, 86);
    ViewGroup.LayoutParams params =
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
    headView.setLayoutParams(params);
    this.addHeaderView(headView);
  }

  public void updateUserInfo(UserInfoBean userInfoBean) {

    TextView nameTv = headView.findViewById(R.id.me_name_tv);
    nameTv.setText(userInfoBean.username);

    TextView phoneTv = headView.findViewById(R.id.me_phone_tv);
    phoneTv.setText(userInfoBean.phone);

    CircleImageView avatarIv = headView.findViewById(R.id.me_avatar);
    ImageLoadUtil.loadImage(avatarIv, userInfoBean.avatar);
  }

  private void initBeans() {

    beans = new LinkedList<>();

    String[] names = {
        "我的消息", "我的关注", "我的社团", "我的活动", "我的益工圈", "我的公益时间", "我的应用", "邀请伙伴"
    };
    int[] icons = {
        R.drawable.me_message, R.drawable.me_focus, R.drawable.me_association,
        R.drawable.me_activity, R.drawable.me_charity_circle, R.drawable.me_chairty_duration,
        R.drawable.me_app, R.drawable.me_invite
    };

    for (int i = 0; i < names.length; i++) {
      MeItemBean bean = new MeItemBean();
      bean.title = names[i];
      bean.icon = icons[i];
      if (i == 1 || i == 4 || i == 7) {
        bean.showSplitLine = false;
      }
      beans.add(bean);
    }

    MeItemBean bean = beans.get(beans.size() - 1);
    bean.showIndicator = false;
  }

  @Override protected void convert(BaseViewHolder helper, MeItemBean item) {

    helper.setText(R.id.me_item_title, item.title);
    ImageView iconIv = helper.getView(R.id.me_item_icon);
    iconIv.setBackgroundResource(item.icon);
    helper.setVisible(R.id.me_item_indicator, item.showIndicator);
    helper.setVisible(R.id.me_item_splitline, item.showSplitLine);
  }

  public Class<? extends BaseActivity> detailClassAtPosition(int postion) {

    switch (postion) {
      case 0: {//message
        return MeApplyActivity.class;
        //                return MessageActivity.class;
      }
      case 1://我的关注
        return MeFocusActivity.class;
      case 2://我的社团
        return MeCorporationActivity.class;
      case 3://我的活动
        return MeActivitiesActivity.class;
      case 4://我的益工圈
        return MeCommunityActivity.class;
      case 5://我的公益时间
        return MeCharityTimeActivity.class;
      case 6://我的应用
        return MeMyAppActivity.class;

      default:
        break;
    }

    return null;
  }

  //    private void click(int position){
  //
  //        if (position == beans.size() - 1){
  //            //invite people
  //            return;
  //        }
  //
  //        Class<? extends  BaseActivity> clazz = detailClassAtPosition(position);
  //        if (clazz == null){
  //            Log.e(TAG, "click: failed position "+position );
  //            return;
  //        }
  //
  //        Intent intent = new Intent(mContext,clazz);
  //        mContext.startActivity(intent);
  //
  //    }
}
