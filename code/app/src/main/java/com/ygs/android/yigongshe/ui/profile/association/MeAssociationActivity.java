package com.ygs.android.yigongshe.ui.profile.association;

import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;

import butterknife.BindInt;
import butterknife.BindView;

public class MeAssociationActivity extends BaseActivity {

    @BindView(R.id.me_association_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.titlebar_backward_btn)
    Button backButton;

    protected  void initIntent(){

    }

    protected  void initView(){

    }

    protected  int getLayoutResId(){

        return R.layout.activity_me_association;
    }
}
