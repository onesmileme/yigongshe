package com.weikan.app.personalcenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.personalcenter.bean.SelectOfficialAccount;
import com.weikan.app.util.ImageLoaderUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaorenhui on 2015/12/6.
 */
public class ChooseOfficialAccountAdapter extends BaseAdapter {

    private Context context;
    private List<SelectOfficialAccount> data;
    private LayoutInflater inflater;
    private HashMap<Integer,Boolean> isSelected = new HashMap<Integer,Boolean>();


    public ChooseOfficialAccountAdapter(Context context, List<SelectOfficialAccount> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        initDate();
    }

    private void initDate(){
        for(int i=0; i<data.size();i++) {
            isSelected.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.choose_oa_row, null);
            holder = new ViewHolder();

            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tvOfcNickname = (TextView) convertView.findViewById(R.id.tv_oa_nickname);
            holder.tvOfcAccount = (TextView) convertView.findViewById(R.id.tv_ofc_account);
            holder.cbSync = (CheckBox) convertView.findViewById(R.id.cb_sync);
            holder.tvSynced = (TextView)convertView.findViewById(R.id.tv_synced);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SelectOfficialAccount account = data.get(position);
        if (!TextUtils.isEmpty(account.avatar)) {
            ImageLoaderUtil.updateImage(holder.ivAvatar, account.avatar, R.drawable.icon_share_qq);
        }

        if (!TextUtils.isEmpty(account.ofcNickname)) {
            holder.tvOfcNickname.setText(account.ofcNickname);
        }

        if (!TextUtils.isEmpty(account.ofcAccount)) {
            holder.tvOfcAccount.setText("微信号:"+account.ofcAccount);
        }


        if (account.isSync == 1) { // 不同步
            holder.cbSync.setVisibility(View.VISIBLE);
            holder.tvSynced.setVisibility(View.GONE);
            holder.cbSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isSelected.put(position, isChecked);
                    if(listItemOnCheckedChangedListener!=null){
                        listItemOnCheckedChangedListener.onCheckedChanged(account,isChecked);
                    }
                }
            });
            holder.cbSync.setChecked(isSelected.get(position));
        } else {  // 同步
            holder.cbSync.setVisibility(View.GONE);
            holder.tvSynced.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private ListItemOnCheckedChangeListener listItemOnCheckedChangedListener = null;

    public void setOnListItemCheckedChangedListener(ListItemOnCheckedChangeListener listener) {
        listItemOnCheckedChangedListener = listener;
    }

    public interface ListItemOnCheckedChangeListener {
        void onCheckedChanged(SelectOfficialAccount item, boolean isChecked);
    }

    public class ViewHolder {
        public ImageView ivAvatar;

        public TextView tvOfcNickname;

        public TextView tvOfcAccount;

        public TextView tvSynced;

        public CheckBox cbSync;

    }
}
