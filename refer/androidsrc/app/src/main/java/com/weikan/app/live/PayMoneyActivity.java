package com.weikan.app.live;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.live.adapter.MoneyAdapter;
import com.weikan.app.live.bean.MoneyBean;
import com.weikan.app.live.bean.MoneyDogBean;
import com.weikan.app.live.bean.PayEvent;
import com.weikan.app.util.LToast;
import com.weikan.app.util.PayUtil;
import com.weikan.app.wxapi.WechatPaymentEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;

public class PayMoneyActivity extends BaseActivity {
    private TextView tvMoney;
    private ListView lvList;
    private MoneyAdapter adapter;
    private List<MoneyDogBean> moneyBeanList;
    private RelativeLayout root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_money);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        EventBus.getDefault().register(PayMoneyActivity.this);
        initView();
        initData();
        initListener();
    }
    private void initView(){
        tvMoney = (TextView)findViewById(R.id.money_num);
        lvList = (ListView)findViewById(R.id.lv_money_list);
        root = (RelativeLayout)findViewById(R.id.activity_pay_money);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new MoneyAdapter(this);
    }
    private void initListener(){
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoneyDogBean moneyDogBean = moneyBeanList.get(position);
                showLoadingDialog();
                PayUtil.getInstance().pay(moneyDogBean.money,PayMoneyActivity.this);
            }
        });
    }
    private void initData(){
        moneyBeanList = new ArrayList<MoneyDogBean>();
        for(int i = 0 ; i < 6 ; i++){
            MoneyDogBean bean = new MoneyDogBean();
            switch (i){
                case 0:
                    bean.dog = 50;
                    bean.money = 5;
                    break;
                case 1:
                    bean.dog = 100;
                    bean.money = 10;
                    break;
                case 2:
                    bean.dog = 200;
                    bean.money = 20;
                    break;
                case 3:
                    bean.dog = 500;
                    bean.money = 50;
                    break;
                case 4:
                    bean.dog = 1000;
                    bean.money = 100;
                    break;
                case 5:
                    bean.dog = 2000;
                    bean.money = 200;
                    break;
            }
            moneyBeanList.add(bean);
            adapter.setMoneyList(moneyBeanList);
        }
        MoneyAgent.getUserMoney(new AmbJsonResponseHandler<MoneyBean>() {
            @Override
            public void success(@Nullable MoneyBean data) {
                tvMoney.setText(Integer.toString(data.money));
                lvList.setAdapter(adapter);
            }
        });
    }
    public void onEventMainThread(WechatPaymentEvent event) {
        hideLoadingDialog();
        if(event.errno == 0){
            LToast.showToast("充值成功");
            EventBus.getDefault().post(new PayEvent());
            finish();
        }else{
            LToast.showToast("充值失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
