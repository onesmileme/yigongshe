package com.weikan.app.original;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.flexbox.FlexboxLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.original.bean.TagObject;
import com.weikan.app.util.URLDefine;
import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujian on 16/5/21.
 */
public class TagSelectActivity extends BaseActivity implements View.OnClickListener {

    ArrayList<TagObject.TagContent> dataList;

    ListView tagList;
    TagAdapter adapter;
    FlexboxLayout flexboxLayout;

    ArrayList<String> selectTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_tag);

        updateTitle();

        tagList = (ListView) findViewById(R.id.lv_tag_select);
        adapter = new TagAdapter(this);
        dataList = new ArrayList<>();
        adapter.setCommentList(dataList);
        tagList.setAdapter(adapter);
        tagList.setDividerHeight(0);
        tagList.setDivider(null);
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setSubItems(i);
                selectTags.clear();
            }
        });

        flexboxLayout = (FlexboxLayout) findViewById(R.id.fl_tag_select);

        sendRequest();
    }

    private void setSubItems(int pos) {
        flexboxLayout.removeAllViews();
        if (pos >= 0 && pos < dataList.size()) {
            ArrayList<String> items = dataList.get(pos).items;
            for (int i = 0; i < items.size(); i++) {
                LinearLayout viewgroup = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tag_select_sub_item, null);
                final TextView textView = (TextView) viewgroup.findViewById(R.id.tv_tag_select_item);
                textView.setText(items.get(i));
                RxView.clicks(textView)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                textView.setSelected(!textView.isSelected());
                                if (textView.isSelected()) {
                                    selectTags.add(textView.getText().toString());
                                } else {
                                    selectTags.remove(textView.getText().toString());
                                }
                            }
                        });
                flexboxLayout.addView(viewgroup);
            }
        }
    }


    private void updateTitle() {
        TextView titleView = (TextView) findViewById(R.id.tv_titlebar_title);
        titleView.setText("选择标签");
        ImageView backImageView = (ImageView) findViewById(R.id.iv_titlebar_back);
        backImageView.setOnClickListener(this);
        TextView right = (TextView) findViewById(R.id.tv_titlebar_right);
        right.setText("确定");
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("tags", selectTags);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void sendRequest() {
        showLoadingDialog();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TAG_LIST);

        Map<String, String> params = new HashMap<String, String>();
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<TagObject>() {
            @Override
            public void success(@NonNull TagObject data) {
                dataList.clear();
                dataList.addAll(data.content);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                hideLoadingDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    class TagAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<TagObject.TagContent> mDataList;

        public TagAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public void setCommentList(ArrayList<TagObject.TagContent> commentList) {
            mDataList = commentList;
            notifyDataSetChanged();
        }

        public void setSelected(int pos) {

        }

        @Override
        public int getCount() {
            if (mDataList == null) {
                return 0;
            }
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            if (mDataList == null || mDataList.size() == 0) {
                return null;
            }
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tag_select_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tagName = (TextView) convertView.findViewById(R.id.tv_tag_select_item);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (mDataList == null || mDataList.size() == 0) {
                return convertView;
            }
            if (mDataList.get(position) == null) {
                return convertView;
            }
            final TagObject.TagContent originalCommentObject = mDataList.get(position);
            viewHolder.tagName.setText(originalCommentObject.title);
            return convertView;
        }

        public class ViewHolder {
            public TextView tagName;
        }
    }
}
