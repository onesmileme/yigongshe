package com.weikan.app.original.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.original.bean.OriginalDetailItem;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.URLDefine;
import com.weikan.app.widget.DynamicHeightImageView;
import com.weikan.app.widget.VoiceRecordView;
import com.weikan.app.widget.photoviewpager.BitmapPersistence;
import com.weikan.app.widget.photoviewpager.PhotoViewPagerActivity;

import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import rx.functions.Action1;

/**
 * NA实现详情页头部
 * Created by liujian on 16/3/22.
 */
public class PartitionDetailHeaderProvider extends AbsDetailHeaderProvider<OriginalDetailItem> {

    @Override
    public View getDetailHeaderView(final Context context, View originalView, ViewGroup parant, final OriginalDetailItem object) {
        if (originalView == null) {
            originalView = LayoutInflater.from(context).inflate(R.layout.detail_header_partition, parant, false);
        }

        TextView titleText = (TextView) originalView.findViewById(R.id.tv_partition_detail_header_title);
        TextView contentText = (TextView) originalView.findViewById(R.id.tv_partition_detail_header_content);
        final ImageView favIv = (ImageView) originalView.findViewById(R.id.iv_fav);
        final TextView favText = (TextView) originalView.findViewById(R.id.tv_fav);
        View favView = originalView.findViewById(R.id.ll_fav);
        final ImageView dislikeIv = (ImageView) originalView.findViewById(R.id.iv_dislike);
        final TextView dislikeText = (TextView) originalView.findViewById(R.id.tv_dislike);
        View dislikeView = originalView.findViewById(R.id.ll_dislike);

//        SimpleDraweeView imageView = (SimpleDraweeView) originalView.findViewById(R.id.iv_partition_detail_header_pic1);
        DynamicHeightImageView imageView = (DynamicHeightImageView) originalView.findViewById(R.id.iv_partition_detail_header_pic1);
        View voiceLayout = originalView.findViewById(R.id.ll_partition_detail_header_voices);
        voiceLayout.setVisibility(View.GONE);

        VoiceRecordView[] voiceRecordViews = new VoiceRecordView[3];
        voiceRecordViews[0] = (VoiceRecordView) originalView.findViewById(R.id.partition_detail_header_voice_0);
        voiceRecordViews[1] = (VoiceRecordView) originalView.findViewById(R.id.partition_detail_header_voice_1);
        voiceRecordViews[2] = (VoiceRecordView) originalView.findViewById(R.id.partition_detail_header_voice_2);
        voiceRecordViews[0].setVoice(null);
        voiceRecordViews[1].setVoice(null);
        voiceRecordViews[2].setVoice(null);

        if (object != null) {
            if (!TextUtils.isEmpty(object.title)) {
                titleText.setVisibility(View.VISIBLE);
                titleText.setText(object.title);
            } else {
                titleText.setVisibility(View.GONE);
            }

            updateFavText(object,favIv,favText);
            updateDislike(object,dislikeIv,dislikeText);

            RxView.clicks(favView)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if (object.collection.flag) {
                                object.collection.flag = false;
                                sendUnFavRequest(object.tid);
                            } else {
                                object.collection.flag = true;
                                sendFavRequest(object.tid);
                            }
                            updateFavText(object, favIv, favText);
                        }
                    });

            RxView.clicks(dislikeView)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if(object.dislike.flag){
                                object.dislike.flag=false;
                                sendUnDislikeRequest(object.tid);
                            } else {
                                object.dislike.flag=true;
                                sendDislikeRequest(object.tid);
                            }
                            updateDislike(object, dislikeIv, dislikeText);
                        }
                    });

            if (object.item_list != null ) {
                for (int i = 0; i < object.item_list.size(); i++) {
                    final OriginalDetailItem.MultiItem item = object.item_list.get(i);
                    // 如果图片不为空，这个item就只显示图片
                    if (item.img != null) {
                        float dv = (float) item.img.w / (float) item.img.h;
                        final Uri uri = Uri.parse(item.img.url);
//                        imageView.setAspectRatio(dv);
//                        imageView.getHierarchy().setPlaceholderImage(R.drawable.image_bg);
//                        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                                .setUri(uri)
//                                .setAutoPlayAnimations(true)
//                                .build();
//                        imageView.setController(controller);
                        imageView.setHeightRatio(dv);
                        ImageLoaderUtil.updateImage(imageView,item.img.url,R.drawable.image_bg);

                        RxView.clicks(imageView)
                                .subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void aVoid) {
                                        // 点击跳转大图
                                        BitmapPersistence.getInstance().clean();
                                        BitmapPersistence.getInstance().mDrawable.add(null);
                                        BitmapPersistence.getInstance().mDrawableUrl.add(item.img.url);

                                        Intent intent = new Intent(context, PhotoViewPagerActivity.class);
                                        intent.putExtra("bitmaps_index", 0);
                                        context.startActivity(intent);
                                    }
                                });

                        // 图片附带的语音
                        if (item.voice != null && item.voice.size() > 0) {
                            voiceLayout.setVisibility(View.VISIBLE);
                            for (int j = 0; j < item.voice.size(); j++) {
                                voiceRecordViews[j].setVoice(item.voice.get(j));
                            }
                        }
                    }
                    // 否则显示文字
                    else if(item.desc!=null){
                        contentText.setText(item.desc);
                    }


                }
            }
        }

        return originalView;
    }

    public void setOnModifyPictureClickListener(View view, View.OnClickListener listener) {
        View viewById = view.findViewById(R.id.tv_modify_picture);
        viewById.setOnClickListener(listener);
    }

    private void updateFavText(OriginalDetailItem object, ImageView iv, TextView tv){
        if(object.collection.flag){
            iv.setImageResource(R.drawable.icon_fav_yellow);
            tv.setText("取消收藏");
            tv.setTextColor(0xfffecb0a);
        } else {
            iv.setImageResource(R.drawable.icon_fav_grey);
            tv.setText("收藏");
            tv.setTextColor(0xff666666);
        }
    }

    private void updateDislike(OriginalDetailItem object, ImageView iv, TextView tv){
        if(object.dislike.flag){
            iv.setImageResource(R.drawable.icon_dislike_focus);
            tv.setTextColor(0xfffecb0a);
        } else {
            iv.setImageResource(R.drawable.icon_dislike);
            tv.setTextColor(0xff666666);
        }
    }

    private void sendFavRequest(String tid){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.FAV_ADD);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {

            }
        });
    }

    private void sendUnFavRequest(String tid){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.FAV_DEL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {

            }
        });
    }

    private void sendDislikeRequest(String tid){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.DISLIKE_ADD);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {

            }
        });
    }

    private void sendUnDislikeRequest(String tid){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.DISLIKE_DEL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
            @Override
            public void success() {

            }
        });
    }

}
