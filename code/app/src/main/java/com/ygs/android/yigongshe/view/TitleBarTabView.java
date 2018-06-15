package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.utils.DensityUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ruichao on 2018/6/15.
 */

public class TitleBarTabView extends LinearLayout {
  public static final int FIRST_INDEX = 0;
  public static final int SECOND_INDEX = 1;
  public static final int THIRD_INDEX = 2;
  private LinearLayout mTabMenuView;
  private int mCurrentTabPosition;
  private int mTextSelectedColor;
  private int mTextUnselectedColor;
  private int mMenuTextSize;
  private int mMenuSelectedColor;
  private int mMenuUnselectedColor;
  private List<TabCheckListener> mListenerList;
  private float mHeight;
  private Context mContext;

  public TitleBarTabView(Context context) {
    this(context, (AttributeSet) null, 0);
  }

  public TitleBarTabView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TitleBarTabView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    this.mCurrentTabPosition = -1;
    this.mListenerList = new ArrayList();
    this.mHeight = 45.0F;
    this.setOrientation(LinearLayout.HORIZONTAL);
    this.mTextSelectedColor = mContext.getResources().getColor(R.color.tab_checked);
    this.mTextUnselectedColor = mContext.getResources().getColor(R.color.tab_unchecked);
    int menuBackgroundColor = mContext.getResources().getColor(R.color.white);
    this.mMenuTextSize = (int) mContext.getResources().getDimension(R.dimen.dimen_14dp);
    this.mMenuSelectedColor = mContext.getResources().getColor(R.color.tab_checked);
    this.mMenuUnselectedColor = mContext.getResources().getColor(R.color.transparent);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBarTabView);
    this.mTextSelectedColor =
        a.getColor(R.styleable.TitleBarTabView_tabTextSelectedColor, this.mTextSelectedColor);
    this.mTextUnselectedColor =
        a.getColor(R.styleable.TitleBarTabView_tabTextUnSelectedColor, this.mTextUnselectedColor);
    menuBackgroundColor =
        a.getColor(R.styleable.TitleBarTabView_titleTabBackground, menuBackgroundColor);
    this.mMenuTextSize =
        a.getDimensionPixelSize(R.styleable.TitleBarTabView_tabTextSize, this.mMenuTextSize);
    this.mMenuSelectedColor =
        a.getColor(R.styleable.TitleBarTabView_menuSelectedColor, this.mMenuSelectedColor);
    this.mMenuUnselectedColor =
        a.getColor(R.styleable.TitleBarTabView_menuUnselectedColor, this.mMenuUnselectedColor);
    a.recycle();
    this.mTabMenuView = new LinearLayout(context);
    LayoutParams params = new LayoutParams(-1, -1);
    params.gravity = 17;
    this.mTabMenuView.setOrientation(LinearLayout.HORIZONTAL);
    this.mTabMenuView.setBackgroundColor(menuBackgroundColor);
    this.mTabMenuView.setGravity(Gravity.CENTER);
    this.mTabMenuView.setLayoutParams(params);
    this.addView(this.mTabMenuView, 0);
  }

  public void setMenuTextSizeInPx(int menuTextSize) {
    this.mMenuTextSize = menuTextSize;
  }

  public void setHeight(float height) {
    this.mHeight = height;
  }

  public int getCount() {
    return this.mTabMenuView.getChildCount();
  }

  public void setTabView(@NonNull List<String> tabTexts) {
    for (int i = 0; i < tabTexts.size(); ++i) {
      String str = (String) tabTexts.get(i);
      this.addTab(str, i);
    }
  }

  public void addTab(String tabText, int index) {
    final LinearLayout tabButton = new LinearLayout(this.getContext());
    LayoutParams params = new LayoutParams(-2, -1);
    tabButton.setOrientation(LinearLayout.VERTICAL);
    params.setMargins(DensityUtil.dp2px(mContext, 10.0F), 0, DensityUtil.dp2px(mContext, 10.0F), 0);
    tabButton.setLayoutParams(params);
    TextView tab = new TextView(this.getContext());
    tab.setSingleLine();
    tab.setEllipsize(TextUtils.TruncateAt.END);
    tab.setGravity(Gravity.CENTER);
    tab.setTextSize(0, (float) this.mMenuTextSize);
    tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        DensityUtil.dp2px(mContext, this.mHeight)));
    tab.setTextColor(this.mTextUnselectedColor);
    tab.setText(tabText);
    View bottomView = new View(this.getContext());
    LayoutParams bottomParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(mContext, 2.0F));
    bottomParams.gravity = 80;
    bottomView.setLayoutParams(bottomParams);
    bottomView.setBackgroundColor(this.mMenuUnselectedColor);
    tabButton.addView(tab);
    tabButton.addView(bottomView);
    tabButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        TitleBarTabView.this.switchMenu(tabButton);
      }
    });
    //this.mTabMenuView.setOnClickListener(new OnClickListener() {
    //  public void onClick(View v) {
    //    TitleBarTabView.this.switchMenu(tabButton);
    //  }
    //});
    this.mTabMenuView.addView(tabButton, index);
  }

  public boolean isShowing() {
    return this.mCurrentTabPosition != -1;
  }

  public void setCurrentTab(int mCurrentTabPosition) {
    this.switchMenu(this.mTabMenuView.getChildAt(mCurrentTabPosition));
  }

  public void setCurrentTabWithoutListener(int mCurrentTabPosition) {
    this.switchMenuView(this.mTabMenuView.getChildAt(mCurrentTabPosition));
  }

  public int getCurrentTabPos() {
    return this.mCurrentTabPosition;
  }

  private void switchMenu(View target) {
    this.switchMenuView(target);
    Iterator var2 = this.mListenerList.iterator();

    while (var2.hasNext()) {
      TitleBarTabView.TabCheckListener listener = (TitleBarTabView.TabCheckListener) var2.next();
      if (null != listener) {
        listener.onTabChecked(this.mCurrentTabPosition);
      }
    }
  }

  private void switchMenuView(View target) {
    for (int i = 0; i < this.mTabMenuView.getChildCount(); ++i) {
      TextView tvTab = (TextView) ((LinearLayout) this.mTabMenuView.getChildAt(i)).getChildAt(0);
      View bottomTab = ((LinearLayout) this.mTabMenuView.getChildAt(i)).getChildAt(1);
      if (target == this.mTabMenuView.getChildAt(i)) {
        this.mCurrentTabPosition = i;
        tvTab.setTextColor(this.mTextSelectedColor);
        bottomTab.setBackgroundColor(this.mMenuSelectedColor);
      } else {
        tvTab.setTextColor(this.mTextUnselectedColor);
        bottomTab.setBackgroundColor(this.mMenuUnselectedColor);
      }
    }
  }

  public void addTabCheckListener(TitleBarTabView.TabCheckListener listener) {
    this.mListenerList.add(listener);
  }

  public interface TabCheckListener {
    void onTabChecked(int var1);
  }
}
