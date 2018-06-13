package com.weikan.app.original.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.ShareActivity;
import com.weikan.app.account.AccountManager;
import com.weikan.app.bean.ShareResultEvent;
import com.weikan.app.bean.Voice;
import com.weikan.app.original.TagsListActivity;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.PicObject;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.URLDefine;
import com.weikan.app.widget.DynamicHeightSketchImageView;
import com.weikan.app.widget.VoiceRecordView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.xiaopan.sketch.request.DisplayOptions;
import platform.http.HttpUtils;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import rx.functions.Action1;
import rx.functions.Func1;

public class OriginalMainAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<OriginalItem> mOriginalList;
    private boolean isAvatarClickable = true;
    private boolean isShowTime = true;
    private boolean isShowTags = true;


    private OnShareClickListener onShareClickListener;

    private OnPraiseClickListener onPraiseClickListener;

    public void setOnShareClickListener(OnShareClickListener listener){
        onShareClickListener = listener;
    }

    public void setOnPraiseClickListener(OnPraiseClickListener listener){
        onPraiseClickListener = listener;
    }

    public OriginalMainAdapter (Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }


    public void setContent(List<OriginalItem> originalObjectList) {
        mOriginalList = originalObjectList;
        notifyDataSetChanged();
    }

    public void setAvatarClickable(boolean able){
        isAvatarClickable = able;
    }

    public void setShowTime(boolean showTime){
        isShowTime = showTime;
    }

    public void setShowTags(boolean showTags) {
        isShowTags = showTags;
    }

    @Override
    public int getCount() {
        if (mOriginalList != null) {
            return mOriginalList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mOriginalList != null) {
            return mOriginalList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        OriginalMainHolder originalMainHolder = null;
        // 临时去掉view复用，解决图片复用导致宽高错误的问题
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.original_main_list_item, null);
            originalMainHolder = new OriginalMainHolder(convertView);
            convertView.setTag(originalMainHolder);
        } else {
            originalMainHolder = (OriginalMainHolder) convertView.getTag();
        }

        if (mOriginalList == null || mOriginalList.get(position) == null) {
            return convertView;
        }


        final OriginalItem originalObject = mOriginalList.get(position);
        if (originalObject.praise!=null && originalObject.praise.flag) {
            originalMainHolder.praiseIconImageView.setImageResource(R.drawable.original_detail_bottom_praised);
        } else {
            originalMainHolder.praiseIconImageView.setImageResource(R.drawable.original_detail_bottom_praise);
        }

        if (isShowTime) {
            setText(originalMainHolder.timeTextView, String.valueOf(getShowTime(originalObject.pubtime)));
        } else {
            setText(originalMainHolder.timeTextView, "");
        }
        final DynamicHeightSketchImageView contentIconImageView = originalMainHolder.contentIconImageView;
        if (originalObject.pic != null ){
            PicObject pic = originalObject.pic.s != null ? originalObject.pic.s : originalObject.pic.t;
            if(pic != null) {

                float dv = (float) pic.h / (float) pic.w;
//                if (dv > 3) {
//                    dv = 3;
//                }
//                contentIconImageView.setAspectRatio(dv);
//                contentIconImageView.getHierarchy().setPlaceholderImage(R.drawable.image_bg);
//                Uri uri = Uri.parse(pic.url);
//                DraweeController controller = Fresco.newDraweeControllerBuilder()
//                        .setUri(uri)
//                        .setAutoPlayAnimations(true)
//                        .build();
//                contentIconImageView.setController(controller);
                contentIconImageView.setHeightRatio(dv);
                contentIconImageView.displayImage(pic.url);
//                ImageLoaderUtil.updateImage(contentIconImageView, pic.url, R.drawable.image_bg);
            }
        }
        setText(originalMainHolder.contentTitleTextView, originalObject.title);
        setText(originalMainHolder.contentBriefIntroductionTextView, originalObject.contentAbstract);
        ImageLoaderUtil.updateImageBetweenUrl(originalMainHolder.authorIconImageView, originalObject.headimgurl, R.drawable.user_default);

        originalMainHolder.readnumTextView.setText(originalObject.read_num+"");

        setText(originalMainHolder.authorNameTextView, originalObject.oa_nick_name);
        setText(originalMainHolder.shareNumTextView, String.valueOf(originalObject.forward != null ? originalObject.forward.num : 0));
        setText(originalMainHolder.commentNumTextView, String.valueOf(originalObject.comment != null ? originalObject.comment.num : 0));
        setText(originalMainHolder.praiseNumTextView, String.valueOf(originalObject.praise != null ? originalObject.praise.num : 0));

        ArrayList<Voice> voices = originalObject.voice;
        int voiceSize = voices != null ? voices.size(): 0;

        if (voiceSize != 0) {
            originalMainHolder.voiceLayout.setVisibility(View.VISIBLE);
            VoiceRecordView[] voiceViews = originalMainHolder.voiceViews;
            for (int i = 0; i < voiceViews.length; i++) {
                if (i < voiceSize) {
                    // 这里肯定不会为null
                    voiceViews[i].setVoice(voices.get(i));
                } else {
                    voiceViews[i].setVoice(null);
                }
            }
        } else {
            originalMainHolder.voiceLayout.setVisibility(View.GONE);
        }

        ArrayList<OriginalItem.Tag> tags = originalObject.tag;
        int size = tags != null ? tags.size(): 0;

        LinearLayout tagLayout = originalMainHolder.tagLayout;
        tagLayout.removeAllViews();

        if (isShowTags) {
            Resources resources = mContext.getResources();
            float density = resources.getDisplayMetrics().density;

            for (int i = 0; i < size; i++) {
                final TextView v = new TextView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(0, 0, (int)(5 * density), 0); // 右边留5dp
                v.setLayoutParams(lp);
                v.setBackgroundResource(R.drawable.shape_bg_tag);
                v.setText(tags.get(i).name);
                v.setTextSize(10);
                v.setTextColor(resources.getColor(R.color.tag_text));

                RxView.clicks(v)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            String tag = v.getText().toString().replace("#", "");
                            onTagClick(mContext, tag);
                        }
                    });
                tagLayout.addView(v);
            }
        }

        originalMainHolder.topImageView.setImageDrawable(null);
        if(originalObject.top!=null){
            switch (originalObject.top.rank){
                case 1:
                    originalMainHolder.topImageView.setImageResource(R.drawable.icon_top1);
                    break;
                case 2:
                    originalMainHolder.topImageView.setImageResource(R.drawable.icon_top2);
                    break;
                case 3:
                    originalMainHolder.topImageView.setImageResource(R.drawable.icon_top3);
                    break;
            }
        }
        switch (originalObject.category){
            case 1://文章
                originalMainHolder.coverImageView.setImageDrawable(null);
                break;
            case 2://图片
                originalMainHolder.coverImageView.setImageDrawable(null);
                break;
            case 3://视频
                originalMainHolder.coverImageView.setImageResource(R.drawable.icon_cover_video);
                break;
            case 4://gif
                originalMainHolder.coverImageView.setImageResource(R.drawable.icon_cover_gif);
                break;
            default:
                originalMainHolder.coverImageView.setImageDrawable(null);
                break;
        }
        // 设置十大相关的title
        if(!TextUtils.isEmpty(originalObject.cycle)){
//            originalMainHolder.divider.setVisibility(View.GONE);
            originalMainHolder.dividerTop.setVisibility(View.VISIBLE);
            originalMainHolder.topText.setText(originalObject.cycle);
        } else {
//            originalMainHolder.divider.setVisibility(View.VISIBLE);
            originalMainHolder.dividerTop.setVisibility(View.GONE);
        }

        // 设置转发头部
        if (originalObject.channel_act != null && originalObject.channel_act.forward != null) {
            originalMainHolder.forwardLayout.setVisibility(View.VISIBLE);
            originalMainHolder.forwardName.setText(originalObject.channel_act.forward.nickname);
        } else {
            originalMainHolder.forwardLayout.setVisibility(View.GONE);
        }

        // 点赞按钮点击
        RxView.clicks(originalMainHolder.praiseIconLayoutView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (onPraiseClickListener != null) {
                            onPraiseClickListener.onPraiseClick(position, originalObject);
                        }
                        onPraiseClick(mContext, originalObject);
                    }
                });
        // 分享按钮点击
        RxView.clicks(originalMainHolder.shareIconLayoutView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (onShareClickListener != null) {
                            onShareClickListener.onShareClick(position,
                                    originalObject.forward != null ? originalObject.forward.url : "", originalObject);
                        }
                        onShareClick(mContext, originalObject);
                    }
                });

        // 用户头像和名称点击
        Action1<Void> userHomeAction = new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent();
                intent.setClass(mContext, UserHomeActivity.class);
                intent.putExtra("uid", originalObject.authorid);
                mContext.startActivity(intent);
            }
        };
        RxView.clicks(originalMainHolder.authorNameTextView)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return isAvatarClickable && !TextUtils.isEmpty(originalObject.authorid);
                    }
                })
                .subscribe(userHomeAction);
        RxView.clicks(originalMainHolder.authorIconImageView)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        return isAvatarClickable && !TextUtils.isEmpty(originalObject.authorid);
                    }
                })
                .subscribe(userHomeAction);

        return convertView;
    }

    private void setText(TextView textView, String text) {
        if (textView == null) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public class OriginalMainHolder {
        public TextView timeTextView;
        public TextView readnumTextView;
        public DynamicHeightSketchImageView contentIconImageView;
        public TextView contentTitleTextView;
        public TextView contentBriefIntroductionTextView;
        public ImageView authorIconImageView;
        public TextView authorNameTextView;
        public View shareIconLayoutView;
        public TextView shareNumTextView;
        public View commentIconLayoutView;
        public TextView commentNumTextView;
        public ImageView praiseIconImageView;
        public View praiseIconLayoutView;
        public TextView praiseNumTextView;
        public ImageView topImageView;
        public ImageView coverImageView;
        public LinearLayout voiceLayout;
        public VoiceRecordView[] voiceViews;
        public LinearLayout tagLayout;
//        public View divider;
        public View dividerTop;
        public TextView topText;

        /** 显示转发的信息 */
        public View forwardLayout;
        /** 显示转发的人名 */
        public TextView forwardName;

        public OriginalMainHolder(View convertView) {
            timeTextView = (TextView) convertView.findViewById(R.id.original_list_time);
            readnumTextView = (TextView) convertView.findViewById(R.id.original_list_read_num);
            contentIconImageView = (DynamicHeightSketchImageView) convertView.findViewById(R.id.original_list_content_icon);
            DisplayOptions displayOptions = new DisplayOptions();
            displayOptions.setLoadingImage(R.drawable.image_bg);
            displayOptions.setErrorImage(R.drawable.image_bg);
            contentIconImageView.setOptions(displayOptions);
            contentTitleTextView = (TextView) convertView.findViewById(R.id.original_list_title);
            contentBriefIntroductionTextView = (TextView) convertView.findViewById(R.id.original_list_brief_introduction);
            authorIconImageView = (ImageView) convertView.findViewById(R.id.original_list_author_icon);
            authorNameTextView = (TextView) convertView.findViewById(R.id.original_list_author_name);
            shareIconLayoutView = convertView.findViewById(R.id.ll_original_list_share_icon);
            shareNumTextView = (TextView) convertView.findViewById(R.id.original_list_share_num);
            commentIconLayoutView = convertView.findViewById(R.id.ll_original_list_comment_icon);
            commentNumTextView = (TextView) convertView.findViewById(R.id.original_list_comment_num);
            praiseIconImageView = (ImageView) convertView.findViewById(R.id.original_list_praise_icon);
            praiseNumTextView = (TextView) convertView.findViewById(R.id.original_list_praise_num);
            praiseIconLayoutView = convertView.findViewById(R.id.ll_original_list_praise_icon);
            topImageView = (ImageView) convertView.findViewById(R.id.iv_original_list_top);
            coverImageView = (ImageView) convertView.findViewById(R.id.iv_original_list_cover);

            voiceLayout = (LinearLayout) convertView.findViewById(R.id.ll_voices);
            voiceViews = new VoiceRecordView[3];

            voiceViews[0] = (VoiceRecordView) convertView.findViewById(R.id.voice_0);
            voiceViews[1] = (VoiceRecordView) convertView.findViewById(R.id.voice_1);
            voiceViews[2] = (VoiceRecordView) convertView.findViewById(R.id.voice_2);

            tagLayout = (LinearLayout) convertView.findViewById(R.id.ll_tags);

            topText = (TextView) convertView.findViewById(R.id.tv_original_list_top_divider);
            dividerTop = convertView.findViewById(R.id.rl_original_list_top_divider);

            forwardLayout = convertView.findViewById(R.id.original_list_forward_layout);
            forwardName = (TextView) convertView.findViewById(R.id.original_list_forward_name);
        }
    }

    public String getShowTime (long time) {
//        FriendlyDate friendlyDate = new FriendlyDate(time * 1000);
//        return friendlyDate.toFriendlyDate(false);
        Date date = new Date(time * 1000);
        return new SimpleDateFormat("M月d日", Locale.getDefault()).format(date);
    }

    public interface OnShareClickListener{
        void onShareClick(int position, String url, OriginalItem item);
    }

    public interface OnPraiseClickListener{
        void onPraiseClick(int position, OriginalItem item);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(ShareResultEvent event){
        if(event!=null && event.tid!=null){
            for(OriginalItem item:mOriginalList){
                if(item.tid.equals(event.tid)){
                    item.forward.num++;
//                    sendShareRequest(item.tid);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }


    private void sendShareRequest(String tid) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.SHARE_LOG);
        Map<String, String> params = new HashMap<>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {

            }
        });
    }

    private void onTagClick(Context context, String tag){
        Intent intent = new Intent(context, TagsListActivity.class);
        intent.putExtra(TagsListActivity.BUNDLE_TAG, tag);
        context.startActivity(intent);
    }

    private void onShareClick(Context context, OriginalItem item){
        new ShareActivity.Builder()
                .tid(item.tid)
                .title("")
                .content(item.share_content)
                .url(item.forward != null ? item.forward.url : "")
                .imgurl(item.share_pic != null && item.share_pic.t != null ? item.share_pic.t.url : "")
//                .shareWenyouType(ShareActivity.SHARE_WENYOU_FOR_NEWS)
                .buildAndShow();

    }

    private void onPraiseClick(Context context, OriginalItem item){
        if(!AccountManager.getInstance().isLogin()){
            AccountManager.getInstance().gotoLogin((Activity) context);
            return;
        }
            if(item.praise!=null){
                if(item.praise.flag){
                    item.praise.flag = false;
                    item.praise.num--;
                    notifyDataSetChanged();
                    toCancelPraise(item);
                } else {
                    item.praise.flag = true;
                    item.praise.num++;
                    notifyDataSetChanged();
                    toAddPraise(item);
                }
            }
    }


    private void toAddPraise(final OriginalItem item) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.ZAN_ADD);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", item.tid);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {

            @Override
            public void success() {

            }
        });
    }

    private void toCancelPraise(final OriginalItem item) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.ZAN_CANCEL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", item.tid);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {

            @Override
            public void success() {

            }
        });
    }
}
