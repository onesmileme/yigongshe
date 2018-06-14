//package com.ygs.android.yigongshe.view;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import com.ygs.android.yigongshe.R;
//import com.ygs.android.yigongshe.utils.DensityUtil;
//import java.util.LinkedList;
//
///**
// * Created by ruichao on 2018/6/14.
// */
//
//public class CommonTitleBar extends LinearLayout {
//  /** titlebar中间的标题 */
//  private String mTitle = "";
//
//  /** titlebar背景色 */
//  private int mBackground;
//
//  /** titlebar文字颜色 */
//  private int mTitleColor;
//
//  /** titlebar返回按钮图片 */
//  private int mBackIconRes = R.drawable.title_back_black;
//  //
//  /** 分割线 */
//  private boolean mDividerVisible = true;
//  private Context mContext;
//
//  private int mScreenWidth;
//  private int mActionPadding;
//  private int mOutPadding;
//  private int mHeight;
//
//  private ImageView mLeft;
//  private LinearLayout mRightLayout;
//  private TextView mCenter;
//  private View mDividerView;
//
//  public CommonTitleBar(Context context) {
//    super(context, null);
//  }
//
//  public CommonTitleBar(Context context, @Nullable AttributeSet attrs) {
//    super(context, attrs, 0);
//  }
//
//  public CommonTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//    super(context, attrs, defStyleAttr);
//    TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
//    mTitle = typedArray.getString(R.styleable.CommonTitleBar_common_tb_title);
//    mBackground = typedArray.getColor(R.styleable.CommonTitleBar_common_tb_background,
//        context.getResources().getColor(R.color.white));
//    mTitleColor = typedArray.getColor(R.styleable.CommonTitleBar_common_tb_color,
//        context.getResources().getColor(R.color.black));
//    mBackIconRes = typedArray.getResourceId(R.styleable.CommonTitleBar_common_tb_back_icon,
//        R.drawable.title_back_black);
//    mDividerVisible =
//        typedArray.getBoolean(R.styleable.CommonTitleBar_common_tb_divider_visible, true);
//    typedArray.recycle();
//    mContext = context;
//    init(context);
//  }
//
//  private void init(Context context) {
//    mScreenWidth = getResources().getDisplayMetrics().widthPixels;
//    mOutPadding = DensityUtil.dp2px(context, 15); //layout间margin
//    mActionPadding = DensityUtil.dp2px(context, 5); //layout中控件间的padding
//    mHeight = context.getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
//    initView(context);
//  }
//
//  private void initView(Context context) {
//    setOrientation(HORIZONTAL);
//    mLeft.setImageResource(mBackIconRes);
//    mLeft.setOnClickListener(new OnClickListener() {
//      @Override public void onClick(View view) {
//        if (null != mContext && mContext instanceof Activity) {
//          ((Activity) mContext).finish();
//        }
//      }
//    });
//    mCenter.setGravity(Gravity.CENTER);
//    setTitle(mTitle);
//    //右布局通过addAction动态添加
//    mRightLayout.setPadding(mOutPadding, 0, 0, 0);
//
//    //底部分割线
//    mDividerView.setBackgroundColor(context.getResources().getColor(R.color.black));
//  }
//
//  public void setTitle(CharSequence title) {
//    mCenter.setText(title);
//  }
//
//  /**
//   * A {@link LinkedList} that holds a list of {@link Action}s.
//   */
//  @SuppressWarnings("serial") public static class ActionList extends LinkedList<Action> {
//  }
//
//  /**
//   * Definition of an action that could be performed, along with a icon to
//   * show.
//   */
//  public interface Action {
//
//    String getText();
//
//    int getDrawable();
//
//    void performAction(View view);
//
//    int getBackground();
//  }
//
//  public static class BaseAction implements Action {
//    @Override public int getDrawable() {
//      return 0;
//    }
//
//    @Override public void performAction(View view) {
//
//    }
//
//    @Override public String getText() {
//      return null;
//    }
//
//    @Override public int getBackground() {
//      return 0;
//    }
//  }
//
//  public static class ImageAction extends BaseAction {
//
//    private int mDrawable;
//
//    public ImageAction(int drawable) {
//      mDrawable = drawable;
//    }
//
//    @Override public int getDrawable() {
//      return mDrawable;
//    }
//  }
//
//  public static class TextAction extends BaseAction {
//
//    final private String mText;
//    private Integer mColor;
//
//    public TextAction(String text) {
//      mText = text;
//    }
//
//    public TextAction(String text, int color) {
//      mText = text;
//      mColor = color;
//    }
//
//    @Override public String getText() {
//      return mText;
//    }
//
//    public Integer getColor() {
//      return mColor;
//    }
//  }
//}
