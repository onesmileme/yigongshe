package com.weikan.app.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.weikan.app.account.AccountManager;
import com.weikan.app.common.adater.BaseListAdapter;
import com.weikan.app.live.widget.LiveForeShowItemView;
import com.weikan.app.live.widget.LiveListItemView;
import com.weikan.app.live.bean.LiveListObject;
import com.weikan.app.util.LToast;

import rx.functions.Action1;

/**
 * 直播列表
 * @author kailun on 16/8/16.
 */
public class LiveListAdapter extends BaseListAdapter<LiveListObject> {

    private final int ITEM_VIEW_TYPE_LIVE = 1;
    private final int ITEM_VIEW_TYPE_PENDING = 2;

    public Action1<LiveListItemView> actionUserClick;

    public LiveListAdapter(@NonNull Context context) {
        super(context, LiveListItemView.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 0) { // +1，为了兼容
            if (emptyView == null) {
                emptyView = new EmptyView(context);
            }
            return emptyView;
        }
        else if (itemViewType == ITEM_VIEW_TYPE_LIVE) {
            if(convertView==null){
                convertView = new LiveListItemView(context);
            }
            convertView.setOnClickListener(itemClickListener);
            ((LiveListItemView)convertView).actionUserClick = actionUserClick;
            final LiveListObject item = (LiveListObject) getItem(position);
            ((LiveListItemView)convertView).set(item);
            if(!TextUtils.isEmpty(item.uid) && item.uid.equals(AccountManager.getInstance().getUserId())){
                ((LiveListItemView)convertView).setOnDeleteLiveListener(new LiveListItemView.DeletLiveListener() {
                    @Override
                    public void onSuccess() {
                        LToast.showToast("删除成功");
                        items.remove(item);
                        notifyDataSetChanged();
                    }
                    @Override
                    public void onFail() {
                        LToast.showToast("删除失败");
                    }
                });
            }
            return convertView;
        } else if(itemViewType == ITEM_VIEW_TYPE_PENDING) {
            if(convertView==null){
                convertView = new LiveForeShowItemView(context);
            }
            convertView.setOnClickListener(itemClickListener);

            LiveListObject item = (LiveListObject) getItem(position);
            ((LiveForeShowItemView)convertView).set(item);
            return convertView;
        }

        throw new RuntimeException("unknown itemViewType: " + itemViewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()){
            return 0; // +1，为了兼容
        }
        else if (position < items.size()) {
            LiveListObject obj = (LiveListObject) getItem(position);
            if(obj.status==1 || obj.status==3){
                return ITEM_VIEW_TYPE_LIVE;
            } else {
                return ITEM_VIEW_TYPE_PENDING;
            }
        }

        throw new RuntimeException("position(" + position + ") is large than items.size("
                + items.size() + ")");
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }
}
