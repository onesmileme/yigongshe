package com.weikan.app.original.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.weikan.app.R;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/16
 */
public class TextColorButtonView extends FrameLayout {

    public static final String DEFAULT_COLOR_TEXT = "0xffffff";
    public static final int DEFAULT_COLOR = Color.WHITE;

    FrameLayout fl;
    ImageView ivColor;

    String colorText = "";
    boolean choose = false;

    public TextColorButtonView(Context context) {
        super(context);
        initViews(null);
    }

    public TextColorButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public TextColorButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_text_color_button_view, this);
        fl = (FrameLayout) findViewById(R.id.fl);
        ivColor = (ImageView) findViewById(R.id.iv_color);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextColorButtonView);
            String colorText = a.getString(R.styleable.TextColorButtonView_text_color_str);
            a.recycle();

            if (!TextUtils.isEmpty(colorText)) {
                int color;
                try {
                    color = Color.parseColor(colorText);
                } catch (RuntimeException e) {
                    // 忽略掉这个异常，保持缺省值
                    this.colorText = DEFAULT_COLOR_TEXT;
                    ivColor.setBackgroundColor(DEFAULT_COLOR);
                    return;
                }

                this.colorText = colorText;
                ivColor.setBackgroundColor(color);
            }
        }
    }

    public String getColorText() {
        return colorText;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
        fl.setBackgroundColor(choose ?
                getResources().getColor(R.color.image_edit_choose_bg) :
                Color.TRANSPARENT);
    }

    public boolean getChoose() {
        return this.choose;
    }
}
