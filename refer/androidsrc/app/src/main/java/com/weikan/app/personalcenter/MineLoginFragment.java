package com.weikan.app.personalcenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseFragment;

/**
 * Created by liujian on 16/7/26.
 */
public class MineLoginFragment extends BaseFragment {

    private boolean isTitleShow = true;

    public void setTitleShow(boolean titleShow) {
        isTitleShow = titleShow;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine_login, null);
        if (isTitleShow) {
            view.findViewById(R.id.iv_titlebar_back).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.tv_titlebar_title)).setText("我的");
        } else {
            view.findViewById(R.id.base_pull_title).setVisibility(View.GONE);
        }

        ImageView rightBtn = (ImageView) view.findViewById(R.id.iv_titlebar_right);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
            }
        });


        view.findViewById(R.id.iv_mine_login_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountManager.getInstance().gotoDirectWeiXinLogin(getActivity(), null);
            }
        });
        view.findViewById(R.id.tv_mine_login_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountManager.getInstance().gotoDirectWeiXinLogin(getActivity(), null);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}