package com.weikan.app.live.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.weikan.app.R;
import com.weikan.app.face.FaceRelativeLayout;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.util.Global;
import rx.functions.Func1;

/**
 * @author kailun on 16/9/3.
 */
public class InputDialog extends Dialog {

    private EditText et;
    private Button bt;
    private FaceRelativeLayout faceLayout;

    private boolean showEmoj = true;

    private Func1<String, Boolean> funcConfirm;

    public InputDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    public InputDialog(Context context, boolean showEmoj) {
        super(context, R.style.MyDialog);
        this.showEmoj = showEmoj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wenyou_input_dialog);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = Global.getInstance().SCREEN_WIDTH;
        dialogWindow.setAttributes(lp);

        et = (EditText) findViewById(R.id.ed_dis_detail);
        faceLayout = (FaceRelativeLayout) findViewById(R.id.rl_face);
        faceLayout.setEditView(et);

        ImageView btnFace = (ImageView) faceLayout.findViewById(R.id.btn_face);
        btnFace.setVisibility(showEmoj? View.VISIBLE: View.GONE);

        bt = (Button) findViewById(R.id.bt_dis_detail_pub);
        bt.setOnClickListener(new OnNoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                boolean r = true;
                if (funcConfirm != null) {
                    r = funcConfirm.call(et.getText().toString());
                    et.setText("");
                }
                if (r) {
//                    dismiss();
                }
            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        et.setHint("");
        et.requestFocus();
        et.postDelayed(new Runnable() {
            @Override
            public void run() {
                showInputMethod(et);
            }
        }, 200);
    }

    @Override
    public void dismiss() {
        closeInputMethod(et);
        super.dismiss();
    }

    private void showInputMethod(final View view) {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    private void closeInputMethod(final View view) {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive(view);
        if (isOpen) {
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void setOnConfirmFunc(Func1<String, Boolean> funcConfirm) {
        this.funcConfirm = funcConfirm;
    }

    public void clearText() {
        if (et != null) {
            et.setText("");
        }
    }
}
