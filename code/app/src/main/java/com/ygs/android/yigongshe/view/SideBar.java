package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.utils.DensityUtil;

public class SideBar extends View {

  /**
   * 触摸事件
   */
  private OnLetterChangedListener onLetterChangedListener;
  /**
   * 26个字母
   */
  private String[] letter = {
      "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
      "T", "U", "V", "W", "X", "Y", "Z", "#"
  };
  // 选中
  private Paint paint;
  private int choose = -1;

  private TextView mTvTips;

  public SideBar(Context context) {
    this(context, null);
  }

  public SideBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    paint = new Paint();
  }

  public void setTextView(TextView mTvTips) {
    this.mTvTips = mTvTips;
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int height = getHeight();
    int width = getWidth();
    // 获取每一个字母的高度
    int singleHeight = height / letter.length;
    int textSize = DensityUtil.dp2px(getContext(), 12);
    int normalColor = ContextCompat.getColor(getContext(), R.color.gray4);
    int selectColor = ContextCompat.getColor(getContext(), R.color.white);
    for (int i = 0; i < letter.length; i++) {
      paint.setColor(normalColor);
      paint.setTypeface(Typeface.DEFAULT_BOLD);
      paint.setAntiAlias(true);
      paint.setTextSize(textSize);
      // 选中的状态
      if (i == choose) {
        paint.setColor(selectColor);
        paint.setFakeBoldText(true);
      }
      // x坐标等于中间-字符串宽度的一半.
      float xPos = width / 2 - paint.measureText(letter[i]) / 2;
      float yPos = singleHeight * i + singleHeight;
      canvas.drawText(letter[i], xPos, yPos, paint);
      // 重置画笔
      paint.reset();
    }
  }

  @Override public boolean dispatchTouchEvent(MotionEvent event) {
    int action = event.getAction();
    float y = event.getY();
    int oldChoose = choose;
    int c = (int) (y / getHeight() * letter.length);
    switch (action) {
      case MotionEvent.ACTION_UP:
        //setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_00));
        choose = -1;
        invalidate();
        if (mTvTips != null) {
          mTvTips.setVisibility(View.INVISIBLE);
        }
        break;
      default:
        //setBackgroundResource(R.drawable.bg_sidebar);
        if (oldChoose != c) {
          if (c >= 0 && c < letter.length) {
            if (onLetterChangedListener != null) {
              onLetterChangedListener.onLetterChanged(letter[c]);
            }
            if (mTvTips != null) {
              mTvTips.setText(letter[c]);
              mTvTips.setVisibility(View.VISIBLE);
            }
            choose = c;
            invalidate();
          }
        }
        break;
    }
    return true;
  }

  /**
   * 设置监听
   *
   * @param onLetterChangedListener 滑动监听
   */
  public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
    this.onLetterChangedListener = onLetterChangedListener;
  }

  public interface OnLetterChangedListener {

    void onLetterChanged(String s);
  }
}