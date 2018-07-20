package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.utils.DensityUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruichao on 2018/6/15.
 */

public class TitleBarTabView extends LinearLayout {
  /** tab index 0 */
  public static final int FIRST_INDEX = 0;
  /** tab index 1 */
  public static final int SECOND_INDEX = 1;
  /** tab index 2 */
  public static final int THIRD_INDEX = 2;

  //顶部菜单布局
  private LinearLayout mTabMenuView;
  //tabMenuView里面选中的tab位置，-1表示未选中
  private int mCurrentTabPosition = -1;

  //tab选中颜色:green_66
  private int mTextSelectedColor;
  //tab未选中颜色
  private int mTextUnselectedColor = 0xff111111;

  //tab字体大小
  private int mMenuTextSize = 14;

  //tab选中图标
  private int mMenuSelectedColor;
  //tab未选中图标
  private int mMenuUnselectedColor;
  private List<TabCheckListener> mListenerList = new ArrayList<>();
  private Context mContext;

  /** 控件整体高度，默认45dp */
  private float mHeight = 45.0f;

  public TitleBarTabView(Context context) {
    this(context, (AttributeSet) null, 0);
  }

  public TitleBarTabView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TitleBarTabView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    setOrientation(VERTICAL);
    mTextSelectedColor = mContext.getResources().getColor(R.color.tab_checked);
    mTextUnselectedColor = mContext.getResources().getColor(R.color.tab_unchecked);
    int menuBackgroundColor = mContext.getResources().getColor(R.color.white);
    mMenuTextSize = (int) mContext.getResources().getDimension(R.dimen.dimen_14dp);
    mMenuSelectedColor = mContext.getResources().getColor(R.color.tab_checked);
    mMenuUnselectedColor = mContext.getResources().getColor(R.color.transparent);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBarTabView);
    mTextSelectedColor =
        a.getColor(R.styleable.TitleBarTabView_tabTextSelectedColor, this.mTextSelectedColor);
    mTextUnselectedColor =
        a.getColor(R.styleable.TitleBarTabView_tabTextUnSelectedColor, this.mTextUnselectedColor);
    menuBackgroundColor =
        a.getColor(R.styleable.TitleBarTabView_titleTabBackground, menuBackgroundColor);
    mMenuTextSize =
        a.getDimensionPixelSize(R.styleable.TitleBarTabView_tabTextSize, this.mMenuTextSize);
    mMenuSelectedColor =
        a.getColor(R.styleable.TitleBarTabView_menuSelectedColor, this.mMenuSelectedColor);
    mMenuUnselectedColor =
        a.getColor(R.styleable.TitleBarTabView_menuUnselectedColor, this.mMenuUnselectedColor);
    a.recycle();
    mTabMenuView = new LinearLayout(context);
    LayoutParams params =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    params.gravity = Gravity.CENTER;
    mTabMenuView.setOrientation(HORIZONTAL);
    this.mTabMenuView.setBackgroundColor(menuBackgroundColor);
    this.mTabMenuView.setGravity(Gravity.CENTER);
    this.mTabMenuView.setLayoutParams(params);
    this.addView(this.mTabMenuView, 0);
  }

  public void setMenuTextSizeInPx(int menuTextSize) {
    mMenuTextSize = menuTextSize;
  }

  public void setHeight(float height) {
    mHeight = height;
  }

  public int getCount() {
    return mTabMenuView.getChildCount();
  }

  public void setTabView(@NonNull List<String> tabTexts) {
    for (int i = 0; i < tabTexts.size(); ++i) {
      String str = (String) tabTexts.get(i);
      addTab(str, i);
    }
  }

  public void addTab(String tabText, int index) {
    final LinearLayout tabButton = new LinearLayout(getContext());
    LayoutParams params =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    tabButton.setOrientation(VERTICAL);
    params.setMargins(DensityUtil.dp2px(mContext, 17.0f), 0, DensityUtil.dp2px(mContext, 17.0f), 0);
    tabButton.setLayoutParams(params);
    TextView tab = new TextView(getContext());
    tab.setSingleLine();
    tab.setEllipsize(TextUtils.TruncateAt.END);
    tab.setGravity(Gravity.CENTER);
    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMenuTextSize);
    tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        DensityUtil.dp2px(mContext, mHeight)));
    tab.setTextColor(mTextUnselectedColor);
    tab.setText(tabText);
    View bottomView = new View(getContext());
    LayoutParams bottomParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(mContext, 3.0f));
    bottomParams.gravity = Gravity.BOTTOM;
    bottomView.setLayoutParams(bottomParams);
    bottomView.setBackgroundColor(mMenuSelectedColor);
    tabButton.addView(tab);
    tabButton.addView(bottomView);
    //添加点击事件
    tabButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        switchMenu(tabButton);
      }
    });
    //扩大点击事件范围
    mTabMenuView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        switchMenu(tabButton);
      }
    });
    mTabMenuView.addView(tabButton, index);
  }

  public boolean isShowing() {
    return mCurrentTabPosition != -1;
  }

  public void setCurrentTab(int mCurrentTabPosition) {
    switchMenu(mTabMenuView.getChildAt(mCurrentTabPosition));
  }

  public void setCurrentTabWithoutListener(int mCurrentTabPosition) {
    switchMenuView(mTabMenuView.getChildAt(mCurrentTabPosition));
  }

  public int getCurrentTabPos() {
    return mCurrentTabPosition;
  }

  private void switchMenu(View target) {
    switchMenuView(target);
    for (TabCheckListener listener : mListenerList) {
      if (null != listener) {
        listener.onTabChecked(mCurrentTabPosition);
      }
    }
  }

  private void switchMenuView(View target) {
    for (int i = 0; i < mTabMenuView.getChildCount(); i++) {
      TextView tvTab = (TextView) ((LinearLayout) mTabMenuView.getChildAt(i)).getChildAt(0);
      View bottomTab = ((LinearLayout) mTabMenuView.getChildAt(i)).getChildAt(1);
      if (target == mTabMenuView.getChildAt(i)) {
        mCurrentTabPosition = i;
        tvTab.setTextColor(mTextSelectedColor);
        bottomTab.setBackgroundColor(mMenuSelectedColor);
      } else {
        tvTab.setTextColor(mTextUnselectedColor);
        bottomTab.setBackgroundColor(mMenuUnselectedColor);
      }
    }
  }

  public void addTabCheckListener(TitleBarTabView.TabCheckListener listener) {
    mListenerList.add(listener);
  }

  public interface TabCheckListener {
    void onTabChecked(int position);
  }
}
