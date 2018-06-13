package com.weikan.app.original.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.weikan.app.R;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/6
 */
public class InputTextPanelView extends FrameLayout {

    private EditText et;
    private Button btnConfirm;
    private ImageView ivClose;
    private LinearLayout llColors;

    String colorText = "";

    public InputTextPanelView(Context context) {
        super(context);
        initViews();
    }

    public InputTextPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public InputTextPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_input_text_panel, this);
        et = (EditText) findViewById(R.id.et);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean empty = s.length() == 0;
                ivClose.setVisibility(empty? View.GONE: View.VISIBLE);
            }
        });
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });

        llColors = (LinearLayout) findViewById(R.id.ll_colors);
        int childCount = llColors.getChildCount();
        for (int i = 0; i<childCount; i++) {
            View childAt = llColors.getChildAt(i);
            childAt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onColorButtonClick(v);
                }
            });
        }
    }

    private void onColorButtonClick(View v) {
        if (!(v instanceof TextColorButtonView)) {
            return;
        }

        TextColorButtonView view = (TextColorButtonView) v;
        this.colorText = view.getColorText();

        int childCount = llColors.getChildCount();
        for (int i = 0; i<childCount; i++) {
            View childAt = llColors.getChildAt(i);
            if (!(childAt instanceof TextColorButtonView)) {
                continue;
            }

            TextColorButtonView childAtView = (TextColorButtonView) childAt;
            childAtView.setChoose(childAtView == view);
        }
    }

    public EditText getEditText() {
        return et;
    }

    public Button getButtonConfirm() {
        return btnConfirm;
    }

    public String getColorText() {
        return this.colorText;
    }

    public void setColorText(String colorText) {
        this.colorText = colorText;

        int childCount = llColors.getChildCount();
        for (int i = 0; i<childCount; i++) {
            View childAt = llColors.getChildAt(i);
            if (!(childAt instanceof TextColorButtonView)) {
                continue;
            }

            TextColorButtonView childAtView = (TextColorButtonView) childAt;
            childAtView.setChoose(childAtView.getColorText().equals(colorText));
        }
    }
}
