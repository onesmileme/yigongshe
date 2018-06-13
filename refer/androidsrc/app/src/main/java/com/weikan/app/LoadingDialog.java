package com.weikan.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wutong on 1/9/16.
 */
public class LoadingDialog extends DialogFragment implements View.OnClickListener {
    public static LoadingDialog newInstance() {
        LoadingDialog instance = new LoadingDialog();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.loading_dialog, container, false);
        ((TextView) contentView.findViewById(R.id.loading_dialog_text_view)).setText("正在加载…");
        contentView.findViewById(R.id.loading_dialog_image_view).setVisibility(View.INVISIBLE);
        contentView.findViewById(R.id.text_dialog_cancel_button).setOnClickListener(this);
        return contentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_dialog_cancel_button: {
                // getDialog().cancel();
                break;
            }
            default: {
                break;
            }
        }
    }
}
