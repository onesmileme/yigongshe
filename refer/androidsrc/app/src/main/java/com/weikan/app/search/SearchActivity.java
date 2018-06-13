package com.weikan.app.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.Constants;
import com.weikan.app.R;
import com.weikan.app.base.BaseFragmentActivity;
import com.weikan.app.common.widget.SimpleNavigationView;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/3/27
 */
public class SearchActivity extends BaseFragmentActivity implements View.OnClickListener {

    private SimpleNavigationView navigation;
    private EditText etSearch;
    private ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
    }

    private void initViews() {

        navigation = (SimpleNavigationView) findViewById(R.id.navigation);
        navigation.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etSearch = (EditText) findViewById(R.id.et_search);
        ivClose = (ImageView) findViewById(R.id.iv_close);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    gotoSearchResult(etSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        findViewById(R.id.tv_search_1).setOnClickListener(this);
        findViewById(R.id.tv_search_2).setOnClickListener(this);
        findViewById(R.id.tv_search_3).setOnClickListener(this);
        findViewById(R.id.tv_search_4).setOnClickListener(this);
        findViewById(R.id.tv_search_5).setOnClickListener(this);
        findViewById(R.id.tv_search_6).setOnClickListener(this);

    }

    void gotoSearchResult(String searchkey){
        Intent intent = new Intent(this,SearchResultActivity.class);
        intent.putExtra(Constants.SEARCH_KEY,searchkey);
        intent.putExtra(Constants.TITLE,searchkey);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_search_1:
                gobackFilter(0,((TextView)findViewById(R.id.tv_search_1)).getText().toString());
                break;
            case R.id.tv_search_2:
                gobackFilter(1,((TextView)findViewById(R.id.tv_search_2)).getText().toString());
                break;
            case R.id.tv_search_3:
                gobackFilter(2,((TextView)findViewById(R.id.tv_search_3)).getText().toString());
                break;
            case R.id.tv_search_4:
                gobackFilter(3,((TextView)findViewById(R.id.tv_search_4)).getText().toString());
                break;
            case R.id.tv_search_5:
                gobackFilter(4,((TextView)findViewById(R.id.tv_search_5)).getText().toString());
                break;
            case R.id.tv_search_6:
                gobackFilter(5,((TextView)findViewById(R.id.tv_search_6)).getText().toString());
                break;
        }
    }

    void gobackFilter(int filter, String title){
//        Intent intent = new Intent();
//        intent.putExtra(Constants.SEARCH_FILTER, filter);
//        setResult(RESULT_OK,intent);
//        finish();

        Intent intent = new Intent(this,SearchResultActivity.class);
        intent.putExtra(Constants.SEARCH_FILTER,filter+"");
        intent.putExtra(Constants.TITLE,title);
        startActivity(intent);
    }
}
