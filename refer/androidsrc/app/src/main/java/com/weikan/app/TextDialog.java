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
public class TextDialog extends DialogFragment implements View.OnClickListener {
    private String text;

    public static TextDialog newInstance(String text) {
        TextDialog instance = new TextDialog();
        instance.text = text;
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.text_dialog, container, false);
        contentView.findViewById(R.id.text_dialog_cancel_button).setOnClickListener(this);
        ((TextView) contentView.findViewById(R.id.text_dialog_text_view)).setText(text);
        return contentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_dialog_cancel_button: {
                getDialog().cancel();
                break;
            }
            default: {
                break;
            }
        }
    }
}
