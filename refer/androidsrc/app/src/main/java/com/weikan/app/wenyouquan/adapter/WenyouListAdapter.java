package com.weikan.app.wenyouquan.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.base.BaseFragmentActivity;
import com.weikan.app.face.FaceConversionUtil;
import com.weikan.app.face.FaceRelativeLayout;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.live.LivePlayNativeActivity;
import com.weikan.app.original.bean.Pic;
import com.weikan.app.original.bean.PicObject;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.push.PushDefine;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.DrawableUtils;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.Global;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.JumpUtil;
import com.weikan.app.util.LToast;
import com.weikan.app.util.SchemaUtil;
import com.weikan.app.util.ShareTools;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.bean.WenyouCommentObject;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.widget.VerticalImageSpan;
import com.weikan.app.widget.photoviewpager.BitmapPersistence;
import com.weikan.app.widget.photoviewpager.PhotoViewPagerActivity;
import com.weikan.app.widget.roundedimageview.CircleImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import rx.functions.Action1;

/**
 * Created by liujian on 16/6/25.
 */
public class WenyouListAdapter extends BaseAdapter {

    protected List<WenyouListData.WenyouListItem> data;
    protected Context context;
    protected LayoutInflater inflater;

    ImageSpan[] spanBlueV;
    ImageSpan[] spanRedV;
    ImageSpan spanTop;

    public int MAX_PIC_WIDTH = 250;
    public int MAX_PIC_HEIGHT = 500;

    public WenyouListAdapter(Context context, List<WenyouListData.WenyouListItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        Drawable drawable = context.getResources().getDrawable(R.drawable.icon_bluev);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        spanBlueV = new VerticalImageSpan[2];
        spanBlueV[0] = new VerticalImageSpan(drawable);
        spanBlueV[1] = new VerticalImageSpan(drawable);

        Drawable drawableRed = context.getResources().getDrawable(R.drawable.icon_redv);
        drawableRed.setBounds(0, 0, drawableRed.getIntrinsicWidth(), drawableRed.getIntrinsicHeight());
        spanRedV = new VerticalImageSpan[2];
        spanRedV[0] = new VerticalImageSpan(drawableRed);
        spanRedV[1] = new VerticalImageSpan(drawableRed);


        Drawable drawableTop = context.getResources().getDrawable(R.drawable.icon_comment_top);
        drawableTop.setBounds(0, 0, drawableTop.getIntrinsicWidth(), drawableTop.getIntrinsicHeight());
        spanTop = new VerticalImageSpan(drawableTop);
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return data != null ? data.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    protected int getLayoutID() {
        return R.layout.tweet_row;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = makeViewHolder();
            convertView = inflater.inflate(getLayoutID(), null);

            updateHolder(convertView, holder);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WenyouListData.WenyouListItem object = (WenyouListData.WenyouListItem) getItem(i);

        updateUserInfoView(object, holder);

        updateZanListView(object, holder);

        updateCommentListView(object, holder, i);

        updateItemListView(object, holder);

        updateLikeCommentIconView(object, holder);

        updateTagView(object, holder);

        updateLongClickReply(convertView, object);
        return convertView;
    }

    /**
     * 更新用户信息相关，名字，头像，v，title，时间
     *
     * @param object
     * @param holder
     */
    protected void updateUserInfoView(final WenyouListData.WenyouListItem object, ViewHolder holder) {

        if (!TextUtils.isEmpty(object.headimgurl)) {
            ImageLoaderUtil.updateImageBetweenUrl(holder.photo, object.headimgurl);
        } else {
            holder.photo.setImageResource(R.drawable.user_default);
        }
        if (object.oa_nick_name != null) {
            holder.nick.setText(object.oa_nick_name);
        }
        if (object.role == 1) {
            holder.nick.setTextColor(context.getResources().getColor(R.color.wenyou_list_name_red));
            holder.ivV.setVisibility(View.VISIBLE);
            holder.ivV.setImageResource(R.drawable.icon_redv);
        } else if (object.role == 2) {

            holder.nick.setTextColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
            holder.ivV.setVisibility(View.VISIBLE);
            holder.ivV.setImageResource(R.drawable.icon_bluev);
        } else {
            holder.nick.setTextColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
            holder.ivV.setVisibility(View.GONE);
        }
        String subtitle = "";
        if (!TextUtils.isEmpty(object.company)) {
            subtitle = object.company;
        }
        if (!TextUtils.isEmpty(object.post)) {
            subtitle = (subtitle + "  " + object.post).trim();
        }
        if (!TextUtils.isEmpty(subtitle)) {
            holder.subtitle.setText(subtitle);
            holder.subtitle.setVisibility(View.VISIBLE);
        } else {
            holder.subtitle.setVisibility(View.GONE);
        }
        FriendlyDate fd = new FriendlyDate(object.ctime * 1000);
        holder.createTime.setText(fd.toFriendlyDate(false));
        if (!TextUtils.isEmpty(object.contentAbstract)) {
            holder.title.setVisibility(View.VISIBLE);
            String title = object.contentAbstract;
            if (title.contains(PushDefine.SCHEMA)) {
                if (PushDefine.PATH_LIVE_PLAY.equals(SchemaUtil.parsePath(title))) {

//                  String html = "<font>"+title.substring(0,title.lastIndexOf("appfac://"))+"</font>"+"<u><font color='#3376e0'>快来围观>></font></u>";
//                  holder.title.setText( Html.fromHtml(html));
                    String scheme = title.substring(title.lastIndexOf(PushDefine.SCHEMA), title.lastIndexOf("'"));
                    final Map<String, String> kvs = SchemaUtil.urlRequest(scheme);
                    String click = title.substring(0, title.indexOf("<")) + "快来围观>>";
                    SpannableString ssb = FaceConversionUtil.getInstace().getExpressionString(context, click);
                    ssb.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        if (!AccountManager.getInstance().isLogin()) {
                                            AccountManager.getInstance().gotoLogin((Activity) context);
                                            return;
                                        }
                                        Intent intent = new Intent(context, LivePlayNativeActivity.class);
                                        intent.putExtra("liveid", kvs.get("liveid"));
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setColor(context.getResources().getColor(R.color.wenguan));
                                        // 去掉下划线
                                    }
                                }, click.lastIndexOf("快来围观>>"), click.lastIndexOf("快来围观>>") + 6,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.title.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.title.setText(ssb);
                }
            } else {
                holder.title.setText(FaceConversionUtil.getInstace().getExpressionString(context, object.contentAbstract));
            }

        } else {
            holder.title.setVisibility(View.GONE);
        }

        if (object.source.equals("usershare")) {
            PicObject picObject;
            picObject = object.pic.n == null ? (object.pic.s == null ? object.pic.t : object.pic.s) : object.pic.n;
            if (picObject != null && !TextUtils.isEmpty(picObject.url)) {
                holder.ivSharePart.setVisibility(View.VISIBLE);
                ImageLoaderUtil.updateImage(holder.ivSharePart, picObject.url);
            } else {
                holder.ivSharePart.setVisibility(View.GONE);
            }
            holder.tvSharePart.setText(TextUtils.isEmpty(object.title) ? "我分享了图片，你看不看" : object.title);
            holder.llSharePart.setVisibility(View.VISIBLE);
            holder.llSharePart.setOnClickListener(new OnNoRepeatClickListener() {
                @Override
                public void onNoRepeatClick(View v) {
                    if (!TextUtils.isEmpty(object.schema)) {
                        JumpUtil.executeSchema(object.schema, context);
                    }
                }
            });
        } else {
            holder.llSharePart.setVisibility(View.GONE);
        }

        Action1<Void> userClickAction = new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(context, UserHomeActivity.class);
                intent.putExtra(BundleParamKey.UID, object.authorid);
                context.startActivity(intent);
            }
        };

        RxView.clicks(holder.photo)
                .subscribe(userClickAction);
        RxView.clicks(holder.nick)
                .subscribe(userClickAction);
    }

    /**
     * 更新图片列表
     *
     * @param object
     * @param holder
     */
    protected void updateItemListView(final WenyouListData.WenyouListItem object, final ViewHolder holder) {
        if (object.item_list != null) {
            holder.picsLayout.removeAllViews();
            holder.picsLayout.setVisibility(View.VISIBLE);
            for (int j = 0; j < object.item_list.size(); j++) {
                LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.wenyou_image_item, null);
                ImageView iv = (ImageView) ll.findViewById(R.id.thumbs);//new ImageView(context);//ImageView) inflater.inflate(R.layout.wenyou_image_item,null);
//                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setImage(iv, object.item_list.get(j).s.url);
//                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
//                        Global.dpToPx(context, 80), Global.dpToPx(context, 80));
//                iv.setLayoutParams(lp);
                holder.picsLayout.addView(ll);

                if (object.item_list.size() == 1) {
                    // 单图的情况，图片等比缩放，在MAx限定的范围内
                    ViewGroup.LayoutParams lp = iv.getLayoutParams();
                    float oriW = object.item_list.get(0).s.w;
                    float oriH = object.item_list.get(0).s.h;
                    float destW = oriW;
                    float destH = oriH;
                    if (oriW > oriH) {
                        destW = oriW > Global.dpToPx(context, MAX_PIC_WIDTH)
                                ? Global.dpToPx(context, MAX_PIC_WIDTH) : oriW;
                        destH = destW / oriW * oriH;
                    } else {
                        destH = oriH > Global.dpToPx(context, MAX_PIC_HEIGHT)
                                ? Global.dpToPx(context, MAX_PIC_HEIGHT) : oriH;
                        destW = destH / oriH * oriW;
                    }

                    lp.width = (int) destW;
                    lp.height = (int) destH;
                    iv.setLayoutParams(lp);
                }

                final int finalJ = j;
                RxView.clicks(iv)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                gotoImageActivity(holder, finalJ, object.item_list);
                            }
                        });
            }
        } else {
            holder.picsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 更新评论列表
     *
     * @param object
     * @param holder
     */
    protected void updateCommentListView(final WenyouListData.WenyouListItem object, final ViewHolder holder, final int position) {
        if (object.comment == null) {
            return;
        }
        holder.commentlist.removeAllViews();
        if (object.comment.top != null && object.comment.top.size() > 0) {
            showCommentList(holder);
            for (int j = object.comment.top.size() - 1; j >= 0; j--) {
                final WenyouListData.CommentItem ci = object.comment.top.get(j);
                View tv = getTopCommentItemView(ci);
                holder.commentlist.addView(tv);
                RxView.clicks(tv)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                onCommentClick(context, object, ci.cid, ci.uid, ci.sname, ci.role, holder);
                            }
                        });
                RxView.longClicks(tv)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                if (TextUtils.equals(ci.uid, AccountManager.getInstance().getUserId())) {
                                    showDeleteCommentDialog(object, ci);
                                }
                            }
                        });
            }

            // top和普通的分割线
            if (object.comment.comment_list != null && object.comment.comment_list.size() > 0) {
                ImageView split = new ImageView(context);
                split.setImageResource(R.drawable.split_common);
                split.setScaleType(ImageView.ScaleType.FIT_XY);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Global.dpToPx(context, 1));
                lp.leftMargin = Global.dpToPx(context, 2);
                lp.rightMargin = Global.dpToPx(context, 2);
                lp.topMargin = Global.dpToPx(context, 3);
                lp.bottomMargin = Global.dpToPx(context, 3);
                holder.commentlist.addView(split, lp);
            }
        }
        if (object.comment.comment_list != null && object.comment.comment_list.size() > 0) {
            showCommentList(holder);
            // 评论列表按照朋友圈正序
            if (object.comment.comment_list.size() > 5) {
                Drawable nav_up = null;
                if (object.commentState) {
                    nav_up = context.getResources().getDrawable(R.drawable.button_up_wenyou);
                    holder.commentUpOrDpwn.setText("收起评论列表");
                } else {
                    nav_up = context.getResources().getDrawable(R.drawable.button_down_wenyou);
                    holder.commentUpOrDpwn.setText("更多" + (object.comment.comment_list.size() - 5) + "条评论");
                }
                nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                holder.commentUpOrDpwn.setCompoundDrawables(null, null, nav_up, null);
                holder.commentUpOrDpwn.setVisibility(View.VISIBLE);
                holder.commentUpOrDpwn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        object.commentState = !object.commentState;
                        updateCommentListView(object, holder, position);
                        if (onItemCommentMoreClickListener != null && !object.commentState) {
                            onItemCommentMoreClickListener.onItemCommentMoreClick(position + 2);
                        }
                    }
                });
            } else {
                holder.commentUpOrDpwn.setVisibility(View.GONE);
            }
            int k;
            if (object.commentState) {
                k = 0;
            } else {
                k = object.comment.comment_list.size() > 5 ? object.comment.comment_list.size() - 5 : 0;
            }
            for (int j = object.comment.comment_list.size() - 1; j >= k; j--) {
                final WenyouListData.CommentItem ci = object.comment.comment_list.get(j);
                View tv = getCommentItemView(ci, j);
                holder.commentlist.addView(tv);
                RxView.clicks(tv)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                onCommentClick(context, object, ci.cid, ci.uid, ci.sname, ci.role, holder);
                            }
                        });
                RxView.longClicks(tv)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                if (TextUtils.equals(ci.uid, AccountManager.getInstance().getUserId())) {
                                    showDeleteCommentDialog(object, ci);
                                }
                            }
                        });
            }
            holder.commentlist.invalidate();
        } else {
            holder.commentUpOrDpwn.setVisibility(View.GONE);
        }
        if ((object.comment.top == null || object.comment.top.size() == 0)
                && (object.comment.comment_list == null || object.comment.comment_list.size() == 0)) {
            holder.commentlist.setVisibility(View.GONE);
            hideCommentListView(holder);
        }
    }

    public interface onItemCommentMoreClickListener {
        void onItemCommentMoreClick(int position);
    }

    private onItemCommentMoreClickListener onItemCommentMoreClickListener;

    public void setOnItemCommentMoreClickListener(onItemCommentMoreClickListener onItemCommentMoreClickListener) {
        this.onItemCommentMoreClickListener = onItemCommentMoreClickListener;
    }

    /**
     * 获取评论itemview
     *
     * @param object
     * @return
     */
    protected View getCommentItemView(final WenyouListData.CommentItem object, int pos) {
        TextView tv = new TextView(context);
        tv.setVisibility(View.VISIBLE);
        tv.setText(addClickablePart(object), TextView.BufferType.SPANNABLE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        return tv;
    }

    /**
     * 获取评论itemview
     *
     * @param object
     * @return
     */
    protected View getTopCommentItemView(final WenyouListData.CommentItem object) {
        TextView tv = new TextView(context);
        tv.setVisibility(View.VISIBLE);
        SpannableString ssb = addClickablePart(object);
        SpannableString topiconSpan = new SpannableString("  ");
        topiconSpan.setSpan(spanTop, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb = new SpannableString(TextUtils.concat(topiconSpan, ssb));
        tv.setText(ssb, TextView.BufferType.SPANNABLE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        return tv;
    }

    /**
     * 隐藏评论列表
     *
     * @param holder
     */
    protected void hideCommentListView(ViewHolder holder) {
        holder.commentlist.setVisibility(View.GONE);
    }

    /**
     * 更新点赞和评论icon
     *
     * @param object
     * @param holder
     */
    protected void updateLikeCommentIconView(final WenyouListData.WenyouListItem object, final ViewHolder holder) {
        if (object.praise.flag) {
            holder.likeIcon.setSelected(true);
        } else {
            holder.likeIcon.setSelected(false);
        }
        RxView.clicks(holder.like)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        onPraiseClick(context, object);
                    }
                });
        RxView.clicks(holder.comment)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        onCommentClick(context, object, "", "", "", 0, holder);
                    }
                });
        RxView.clicks(holder.share)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        onShareClick(context, object);
                    }
                });
        if (object.comment != null) {
            holder.commentCount.setText((object.comment.num + object.comment.top_num) + "");
        } else {
            holder.commentCount.setText("0");
        }
        if (object.praise != null) {
            holder.likeCount.setText(object.praise.num + "");
        } else {
            holder.likeCount.setText("0");
        }
    }

    /**
     * 更新点赞列表
     *
     * @param object
     * @param holder
     */
    protected void updateZanListView(final WenyouListData.WenyouListItem object, ViewHolder holder) {
        if (object.praise.user_list.size() == 0) {
            holder.zanLayout.setVisibility(View.GONE);
        }
        holder.discuss_split.setVisibility(View.GONE);
        if ((object.praise.user_list.size() != 0)
                && (object.comment.comment_list.size() != 0)) {
            holder.discuss_split.setVisibility(View.VISIBLE);
        }

        if (object.praise.user_list.size() > 0) {
            holder.zanLayout.setVisibility(View.VISIBLE);
            showzanLayout(holder.zanNamesLayout, object);
        } else {
            holder.zanLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 更新tag标签
     *
     * @param object
     * @param holder
     */
    protected void updateTagView(final WenyouListData.WenyouListItem object, ViewHolder holder) {
        if (object.label_info != null && object.label_info.size() > 0) {
            holder.tagView.setVisibility(View.VISIBLE);
            final WenyouListData.LabelInfoObject li = object.label_info.get(0);
            int bgColor = 0xff4990e3;
            try {
                bgColor = Color.parseColor(li.bgColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tagView.setBackgroundDrawable(DrawableUtils.makeSolidCornerShape(context, bgColor));
            int textColor = Color.WHITE;
            try {
                textColor = Color.parseColor(li.textColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tagView.setText(li.name);
            holder.tagView.setTextColor(textColor);
            RxView.clicks(holder.tagView)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if (!TextUtils.isEmpty(li.schema)) {
                                JumpUtil.executeSchema(li.schema, context);
                            }
                        }
                    });
        } else {
            holder.tagView.setVisibility(View.GONE);
        }
//
//
//        if (object.groupInfo != null && object.groupInfo.size() > 0) {
//            holder.tagView.setVisibility(View.VISIBLE);
//            WenyouListData.GroupInfo gi = object.groupInfo.get(0);
//            holder.tagView.setText(gi.name);
//            holder.tagView.setTextColor(Color.WHITE);
//            int bgColor = 0xff4990e3;
//            try {
//                bgColor = Color.parseColor(gi.label_color);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            holder.tagView.setBackgroundDrawable(DrawableUtils.makeSolidCornerShape(context, bgColor));
//
//            RxView.clicks(holder.tagView)
//                    .subscribe(new Action1<Void>() {
//                        @Override
//                        public void call(Void aVoid) {
//                            Intent intent = new Intent(context, GroupDetailActivity.class);
//                            intent.putExtra(BundleParamKey.GROUPID, object.groupInfo.get(0).group_id);
//                            intent.putExtra(BundleParamKey.GROUPNAME, object.groupInfo.get(0).name);
//                            context.startActivity(intent);
//                        }
//                    });
//        } else if (object.topicInfo != null && object.topicInfo.size() > 0) {
//            holder.tagView.setVisibility(View.VISIBLE);
//            holder.tagView.setText("#" + object.topicInfo.get(0).name);
//            holder.tagView.setTextColor(0xfffc461e);
//            holder.tagView.setBackgroundResource(R.drawable.wenyou_list_tag_bg);
//
//            RxView.clicks(holder.tagView)
//                    .subscribe(new Action1<Void>() {
//                        @Override
//                        public void call(Void aVoid) {
//                            Intent intent = new Intent(context, WenyouTopicListActivity.class);
//                            intent.putExtra(URLDefine.TOPIC_ID, object.topicInfo.get(0).id);
//                            intent.putExtra(URLDefine.TOPIC_NAME, object.topicInfo.get(0).name);
//                            context.startActivity(intent);
//                        }
//                    });
//        } else {
//            holder.tagView.setVisibility(View.GONE);
//        }
    }

    protected void updateLongClickReply(final View convertView, final WenyouListData.WenyouListItem object) {
        // 长按提示删除
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (TextUtils.equals(object.authorid, AccountManager.getInstance().getUserId())) {
                    showDeletePostDialog(object, (ViewHolder) convertView.getTag());
                }
                return false;
            }
        });
    }

    protected void setImage(ImageView iv, String photoName) {
        if (TextUtils.isEmpty(photoName)) {
            iv.setImageResource(R.drawable.image_bg);
            iv.setVisibility(View.GONE);
            iv.setTag("");
        } else {
            iv.setVisibility(View.VISIBLE);
            ImageLoaderUtil.updateImageBetweenUrl(iv, photoName);
        }
    }

    protected void gotoImageActivity(ViewHolder holder, int index, ArrayList<Pic> pics) {
        BitmapPersistence.getInstance().mDrawable.clear();
        BitmapPersistence.getInstance().clean();
        for (int i = 0; i < holder.picsLayout.getChildCount(); i++) {
            ImageView iv = (ImageView) ((LinearLayout) holder.picsLayout.getChildAt(i)).getChildAt(0);
            Drawable bitmap = iv.getDrawable();
            if (bitmap != null) {
                BitmapPersistence.getInstance().mDrawable.add(bitmap);
                BitmapPersistence.getInstance().mDrawableUrl
                        .add(pics.get(i).n.url);
            }
        }

        Intent intent = new Intent(context, PhotoViewPagerActivity.class);
        intent.putExtra("bitmaps_index", index);
        context.startActivity(intent);
    }

    protected void updateHolder(View convertView, ViewHolder holder) {
        holder.photo = (CircleImageView) convertView
                .findViewById(R.id.avatar);
        holder.nick = (TextView) convertView.findViewById(R.id.nick);
        holder.subtitle = (TextView) convertView.findViewById(R.id.tv_tweet_subtitle);
        holder.createTime = (TextView) convertView
                .findViewById(R.id.create_at);
        holder.title = (TextView) convertView.findViewById(R.id.title);
        holder.ivV = (ImageView) convertView.findViewById(R.id.iv_v);

        holder.llSharePart = (LinearLayout) convertView.findViewById(R.id.ll_share_content);
        holder.ivSharePart = (ImageView) convertView.findViewById(R.id.iv_share_circle);
        holder.tvSharePart = (TextView) convertView.findViewById(R.id.tv_share_circle);

        holder.picsLayout = (ViewGroup) convertView.findViewById(R.id.pics_multi);

        holder.commentCount = (TextView) convertView
                .findViewById(R.id.comment_count);
        holder.likeCount = (TextView) convertView
                .findViewById(R.id.like_count);
        holder.likeIcon = (ImageView) convertView
                .findViewById(R.id.like_icon);
        holder.comment = convertView.findViewById(R.id.comment);
        holder.like = convertView.findViewById(R.id.like);
        holder.share = convertView.findViewById(R.id.share);
        holder.tagView = (TextView) convertView.findViewById(R.id.tv_wenyou_item_tag);

			/* 赞 转 评论 */
        holder.zanLayout = convertView.findViewById(R.id.lv_discuss_zan);

        holder.zanNamesLayout = (ViewGroup) convertView
                .findViewById(R.id.iv_discuss_zan_names_layout);
        holder.commentlist = (LinearLayout) convertView.findViewById(R.id.discuss_comment_list);

        holder.discuss_botton = (LinearLayout) convertView.findViewById(R.id.discuss_botton);
        holder.discuss_split = convertView.findViewById(R.id.discuss_split);
        //评论的收起或者展开按钮
        holder.commentUpOrDpwn = (TextView) convertView.findViewById(R.id.tv_comment_upordown);
    }

    protected ViewHolder makeViewHolder() {
        return new ViewHolder();
    }

    public class ViewHolder {
        // 原文部分
        CircleImageView photo;
        TextView nick;
        TextView subtitle;
        TextView createTime;
        TextView title;
        ViewGroup picsLayout;
        ImageView ivV;

        LinearLayout llSharePart;
        ImageView ivSharePart;
        TextView tvSharePart;

        // 底部按钮
        View comment;
        View like;
        View share;
        TextView commentCount;
        TextView likeCount;
        ImageView likeIcon;
        ViewGroup zanNamesLayout;
        TextView tagView;

        LinearLayout discuss_botton;
        View discuss_split;
        View zanLayout;
        LinearLayout commentlist;
        TextView commentUpOrDpwn;
    }

    public void showzanLayout(final ViewGroup praiseNamesLayout, final WenyouListData.WenyouListItem object) {
        praiseNamesLayout.setVisibility(View.VISIBLE);
        praiseNamesLayout.removeAllViews();
        int zanLayoutWidth = praiseNamesLayout.getMeasuredWidth();
        int lineNum = (zanLayoutWidth - Global.dpToPx(context, 3)) / Global.dpToPx(context, 17);
        if (lineNum == 0) {
            lineNum = 14;
        }
        TextView zanDownOrUp = new TextView(context);
        zanDownOrUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.zanState = !object.zanState;
                showzanLayout(praiseNamesLayout, object);
            }
        });
        FlexboxLayout.LayoutParams tvPa = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPa.leftMargin = Global.dpToPx(context, 7);
        tvPa.topMargin = Global.dpToPx(context, 4);
        zanDownOrUp.setTextColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
        zanDownOrUp.setTextSize(12);
        Drawable nav_up = null;
        int showNum = object.praise.user_list.size();
        if (object.zanState) {
            nav_up = context.getResources().getDrawable(R.drawable.button_up_wenyou);
            zanDownOrUp.setText("收起");
        } else {
            nav_up = context.getResources().getDrawable(R.drawable.button_down_wenyou);
            if (object.praise.user_list.size() > lineNum * 2) {
                showNum = lineNum * 2 - 3;
            }
            zanDownOrUp.setText("更多");
        }
        zanDownOrUp.setCompoundDrawablePadding(Global.dpToPx(context, 9));
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        zanDownOrUp.setCompoundDrawables(null, null, nav_up, null);
        for (int i = 0; i < showNum; i++) {
            CircleImageView iv = new CircleImageView(context, null, 0);
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(Global.dpToPx(context, 20), Global.dpToPx(context, 20));
            lp.rightMargin = 0 - Global.dpToPx(context, 3);
            praiseNamesLayout.addView(iv, lp);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (TextUtils.isEmpty(object.praise.user_list.get(i).headimgurl)) {
                iv.setImageResource(R.drawable.user_default);
            } else {
                ImageLoaderUtil.updateImageBetweenUrl(iv, object.praise.user_list.get(i).headimgurl, R.drawable.user_default);
            }
        }
        if (object.zanState || object.praise.user_list.size() > lineNum * 2) {
            praiseNamesLayout.addView(zanDownOrUp, tvPa);
        }
    }


    public void showCommentList(TextView mTextView,
                                WenyouListData.CommentItem object) {
        mTextView.setVisibility(View.VISIBLE);
//        mTextView.setMovementMethod(MyLinkMovementMethod.getInstance());
        mTextView.setText(addClickablePart(object), TextView.BufferType.SPANNABLE);
    }

    protected SpannableString addClickablePart(final WenyouListData.CommentItem object) {
        // 第一个赞图标
        String str = "";
        if (object.role == 0) {
            str = object.sname;
        } else {
            // 留一个空格位置来放v
            str = object.sname + " ";
        }
        String[] likeUsers = new String[2];
        int secondstartPos = -1;
        likeUsers[0] = str;
        if (!TextUtils.isEmpty(object.reply_sname)) {
            if (object.reply_role == 0) {
                likeUsers[1] = object.reply_sname;
            } else {
                // 留个位置放v
                likeUsers[1] = object.reply_sname + " ";
            }

            str = likeUsers[0] + "回复" + likeUsers[1];
            // 存在两个名字相同的情况，所以单独记录
            secondstartPos = str.length() - likeUsers[1].length();
        }
        SpannableString ssb = FaceConversionUtil.getInstace().getExpressionString(context, str + " : " + object.content.trim());
        // 评论人加v
        if (object.role == 1) {
            ssb.setSpan(spanRedV[0], likeUsers[0].length() - 1, likeUsers[0].length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (object.role == 2) {
            ssb.setSpan(spanBlueV[0], likeUsers[0].length() - 1, likeUsers[0].length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        // 评论被回复人加v
        if (!TextUtils.isEmpty(object.reply_sname)) {
            if (object.reply_role == 1) {
                ssb.setSpan(spanRedV[1], str.length() - 1, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (object.reply_role == 2) {
                ssb.setSpan(spanBlueV[1], str.length() - 1, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        if (likeUsers.length > 0) {
            // 最后一个
            for (int i = 0; i < likeUsers.length; i++) {
//				if(i != 1){
                final String name = likeUsers[i];
                if (name != null && !name.equals("")) {
                    final int start = i == 0 ? 0 : secondstartPos;
                    final int finalI = i;
                    ssb.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            return;
                                        }
                                        if (finalI == 0) {
                                            Intent intent = new Intent(context, UserHomeActivity.class);
                                            intent.putExtra(BundleParamKey.UID, object.uid);
                                            context.startActivity(intent);
                                        } else if (finalI == 1) {
                                            Intent intent = new Intent(context, UserHomeActivity.class);
                                            intent.putExtra(BundleParamKey.UID, object.reply_uid);
                                            context.startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        if (finalI == 0) {
                                            // 设置评论的人名颜色
                                            if (object.role == 1) {
                                                ds.setColor(context.getResources().getColor(R.color.wenyou_list_name_red)); // 设置文本颜色
                                            } else {
                                                ds.setColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
                                            }
                                        } else {
                                            // 设置被回复的人名颜色
                                            if (object.reply_role == 1) {
                                                ds.setColor(context.getResources().getColor(R.color.wenyou_list_name_red)); // 设置文本颜色
                                            } else {
                                                ds.setColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
                                            }
                                        }
                                        // 去掉下划线
                                        ds.setUnderlineText(false);
                                    }

                                }, start, start + name.trim().length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return ssb;
    }

    InputDialog inputDialog;

    protected void onCommentClick(Context context, WenyouListData.WenyouListItem item, String reply_cid, String reply_uid,
                                  String reply_sname, int reply_role, ViewHolder holder) {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().gotoLogin((Activity) context);
            return;
        }

        if (TextUtils.equals(reply_uid, AccountManager.getInstance().getUserId())) {
            // 自己回复自己无效
            return;
        }

        if (inputDialog == null) {
            inputDialog = new InputDialog(context);
        }

        inputDialog.setData(item, reply_cid, reply_uid, reply_sname, reply_role, holder);
        inputDialog.show();

    }

    protected void onShareClick(Context context, WenyouListData.WenyouListItem item) {
        if (item.share != null) {
            ShareTools.getInstance().share(context,
                    item.share.title,
                    "",
                    item.share.desc,
                    item.share.url,
                    item.share.icon != null ? item.share.icon : "", 0);
        }
    }

    protected void toComment(final String content, final String tid, final String reply_cid, final String reply_uid, final String reply_sname, final int reply_role, final WenyouListData.WenyouListItem item, final ViewHolder holder) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.COMMENT_PUBLISH);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", AccountManager.getInstance().getUserId());
        params.put("tid", tid);
        params.put("content", content);
        params.put("reply_cid", reply_cid);
        params.put("reply_uid", reply_uid);
        HttpUtils.get(builder.build().toString(), params, new JsonResponseHandler<WenyouCommentObject>() {
            @Override
            public void success(@NonNull WenyouCommentObject data) {
                WenyouListData.CommentItem ci = new WenyouListData.CommentItem();
                ci.tid = tid;
                ci.cid = data.cid + "";
                ci.uid = AccountManager.getInstance().getUserId();
                ci.avatar = AccountManager.getInstance().getUserData().headimgurl;
                ci.sname = AccountManager.getInstance().getUserData().nick_name;
                ci.role = AccountManager.getInstance().getUserData().role;
                ci.content = content;
                ci.reply_sname = reply_sname;
                ci.reply_cid = reply_cid;
                ci.reply_uid = reply_uid;
                ci.reply_role = reply_role;
                ci.ctime = new Date().getTime() / 1000;
                holder.commentlist.setVisibility(View.VISIBLE);
                if (item.comment == null || item.comment.comment_list == null || item.comment.comment_list.size() == 0) {
                    item.comment = new WenyouListData.Comment();
                    item.comment.comment_list = new ArrayList<WenyouListData.CommentItem>();
                    item.comment.comment_list.add(0, ci);
                    item.comment.num = 1;
                } else {
                    item.comment.comment_list.add(0, ci);
                    item.comment.num++;
                }
                notifyDataSetChanged();
            }
        });
    }

    protected void onPraiseClick(Context context, WenyouListData.WenyouListItem item) {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().gotoLogin((Activity) context);
            return;
        }
        if (item.praise != null) {
            if (item.praise.flag) {
                item.praise.flag = false;
                item.praise.num--;
                Iterator<WenyouListData.PraiseItem> it = item.praise.user_list.iterator();
                while (it.hasNext()) {
                    WenyouListData.PraiseItem pi = it.next();
                    if (pi.uid.equals(AccountManager.getInstance().getUserId())) {
                        it.remove();
                        break;
                    }
                }
                notifyDataSetChanged();
                toCancelPraise(item);
            } else {
                item.praise.flag = true;
                item.praise.num++;
                if (item.praise.user_list == null) {
                    item.praise.user_list = new ArrayList<>();
                }
                WenyouListData.PraiseItem pi = new WenyouListData.PraiseItem();
                pi.headimgurl = AccountManager.getInstance().getUserData().headimgurl;
                pi.nickname = AccountManager.getInstance().getUserData().nick_name;
                pi.uid = AccountManager.getInstance().getUserId();
                item.praise.user_list.add(pi);
                notifyDataSetChanged();
                toAddPraise(item);
            }
        }
    }


    protected void toAddPraise(final WenyouListData.WenyouListItem item) {
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

    protected void toCancelPraise(final WenyouListData.WenyouListItem item) {
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

    /**
     * 长按删除的菜单
     */
    protected static String[] sDeleteMenuList = new String[]{"删除", "取消"};

    /**
     * 删除帖子
     *
     * @param obj
     */
    public void showDeletePostDialog(final WenyouListData.WenyouListItem obj, final ViewHolder holder) {

        new AlertDialog.Builder(context)
                .setItems(sDeleteMenuList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showLoadingDialog();
                                sendDeleteRequest(obj, holder);
                                break;
                            default:
                                break;
                        }
                    }
                }).create().show();

    }

    /**
     * 删除评论
     *
     * @param obj
     */
    protected AlertDialog alertDialog;

    public void showDeleteCommentDialog(final WenyouListData.WenyouListItem obj, final WenyouListData.CommentItem cmt) {

        alertDialog = new AlertDialog.Builder(context)
                .setItems(sDeleteMenuList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showLoadingDialog();
                                sendDeleteCommentRequest(obj, cmt);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create();
        alertDialog.show();
    }


    protected void sendDeleteRequest(final WenyouListData.WenyouListItem item, final ViewHolder holder) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.DEL_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TID, item.tid);

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.SimpleJsonResponseHandler() {
            @Override
            public void success() {
                data.remove(item);
                notifyDataSetChanged();
                LToast.showToast("删除成功。");
            }

            @Override
            public void end() {
                hideLoadingDialog();
            }
        });
    }

    protected void sendDeleteCommentRequest(final WenyouListData.WenyouListItem item, final WenyouListData.CommentItem cmt) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.DEL_COMMENT);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TID, item.tid);
        params.put(URLDefine.CID, cmt.cid);

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.SimpleJsonResponseHandler() {
            @Override
            public void success() {
                item.comment.comment_list.remove(cmt);
                item.comment.num--;
                notifyDataSetChanged();
                LToast.showToast("删除成功。");
            }

            @Override
            public void end() {
                hideLoadingDialog();
            }
        });
    }

    protected void showLoadingDialog() {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showLoadingDialog();
        } else if (context instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) context).showLoadingDialog();
        }
    }

    protected void hideLoadingDialog() {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).hideLoadingDialog();
        } else if (context instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) context).hideLoadingDialog();
        }
    }

    protected void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
        if (view != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }

    }


    protected void closeInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive(view);
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

    public class InputDialog extends Dialog implements
            android.view.View.OnClickListener {
        private WenyouListData.WenyouListItem item;
        String r_cid;
        String r_uid;
        String r_sname;
        int r_role;
        EditText et;
        Button bt;
        FaceRelativeLayout faceLayout;
        ViewHolder holder;

        public InputDialog(Context context) {
            super(context, R.style.MyDialog);
        }

        public void setData(WenyouListData.WenyouListItem item, String reply_cid, String reply_uid,
                            String reply_sname, int reply_role, ViewHolder holder) {

            this.item = item;
            this.r_cid = reply_cid;
            this.r_uid = reply_uid;
            this.r_sname = reply_sname;
            this.r_role = reply_role;
            if (et != null) {
                et.setText("");
            }
            this.holder = holder;
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

            bt = (Button) findViewById(R.id.bt_dis_detail_pub);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String content = et.getText().toString();
                    if (TextUtils.isEmpty(content)) {
                        LToast.showToast("评论不能为空");
                        return;
                    }
                    if (content.length() > Constants.commentMaxLength) {
                        LToast.showToast(MainApplication.getInstance().getResources().getString(R.string.comment_pub_beyond, Constants.commentMaxLength));
                        return;
                    }
                    toComment(et.getText().toString(), item.tid, r_cid, r_uid, r_sname, r_role, item, holder);
                    dismiss();
                }
            });

        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (!TextUtils.isEmpty(r_sname)) {
                et.setHint("回复：" + r_sname);
            } else {
                et.setHint("");
            }
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

        @Override
        public void onClick(View v) {

        }


    }

    protected void showCommentList(ViewHolder holder) {
        holder.commentlist.setVisibility(View.VISIBLE);
    }
}
