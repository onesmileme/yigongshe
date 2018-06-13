package com.weikan.app.wenyouquan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.face.FaceConversionUtil;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.Global;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.widget.roundedimageview.CircleImageView;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by liujian on 16/8/2.
 */
public class WenyouDetailAdapter extends WenyouListAdapter {
    public WenyouDetailAdapter(Context context, List<WenyouListData.WenyouListItem> data) {
        super(context, data);
    }

    public class DetailViewHolder extends ViewHolder {

        ViewGroup commentlistLayout;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.wenyou_detail_item;
    }

    @Override
    protected ViewHolder makeViewHolder() {
        return new DetailViewHolder();
    }

    @Override
    protected void updateHolder(View convertView, ViewHolder holder) {
        super.updateHolder(convertView, holder);
        ((DetailViewHolder) holder).commentlistLayout = (ViewGroup) convertView.findViewById(R.id.lv_discuss_comment);
    }


    /**
     * 设置评论item
     *
     * @param ci
     * @return
     */
    @Override
    protected View getCommentItemView(final WenyouListData.CommentItem ci, int pos) {
        View view = inflater.inflate(R.layout.wenyou_detail_comment_item, null);
        ImageView avatar = (ImageView) view.findViewById(R.id.iv_wenyou_detail_comment_avatar);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_wenyou_detail_comment_v);
        ImageView split = (ImageView) view.findViewById(R.id.iv_wenyou_detail_comment_split);
        TextView name = (TextView) view.findViewById(R.id.tv_wenyou_detail_comment_nick);
        TextView content = (TextView) view.findViewById(R.id.tv_wenyou_detail_comment_title);
        TextView time = (TextView) view.findViewById(R.id.tv_wenyou_detail_comment_create_at);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        ImageLoaderUtil.updateImageBetweenUrl(avatar, ci.avatar, R.drawable.user_default);

        if (ci.role == 1) {
            name.setTextColor(context.getResources().getColor(R.color.wenyou_list_name_red));
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.icon_redv);
        } else if (ci.role == 2) {

            name.setTextColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.icon_bluev);
        } else {
            name.setTextColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
            iv.setVisibility(View.GONE);
        }

        name.setText(ci.sname);

        FriendlyDate fd = new FriendlyDate(ci.ctime * 1000);
        time.setText(fd.toFriendlyDate(false));

        if (!TextUtils.isEmpty(ci.content)) {
            content.setVisibility(View.VISIBLE);
            content.setText(addClickablePart(ci), TextView.BufferType.SPANNABLE);
        } else {
            content.setVisibility(View.GONE);
        }
        if (pos == 0) {
            split.setVisibility(View.GONE);
        } else {
            split.setVisibility(View.VISIBLE);
        }
        RxView.clicks(avatar)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent(context, UserHomeActivity.class);
                        intent.putExtra(BundleParamKey.UID, ci.uid);
                        context.startActivity(intent);
                    }
                });
        RxView.clicks(name)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent(context, UserHomeActivity.class);
                        intent.putExtra(BundleParamKey.UID, ci.uid);
                        context.startActivity(intent);
                    }
                });
        return view;
    }

    @Override
    protected void hideCommentListView(ViewHolder holder) {
        super.hideCommentListView(holder);
        ((DetailViewHolder) holder).commentlistLayout.setVisibility(View.GONE);
    }

    protected void showCommentList(ViewHolder holder) {
        super.showCommentList(holder);
        ((DetailViewHolder) holder).commentlistLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateCommentListView(final WenyouListData.WenyouListItem object, final ViewHolder holder, int position) {
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
            for (int j = object.comment.comment_list.size() - 1; j >= 0; j--) {
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
        }
        if ((object.comment.top == null || object.comment.top.size() == 0)
                && (object.comment.comment_list == null || object.comment.comment_list.size() == 0)) {
            holder.commentlist.setVisibility(View.GONE);
            hideCommentListView(holder);
        }
    }

    @Override
    public void showzanLayout(ViewGroup praiseNamesLayout, WenyouListData.WenyouListItem object) {
        praiseNamesLayout.setVisibility(View.VISIBLE);
        praiseNamesLayout.removeAllViews();
        for (int i = 0; i < object.praise.user_list.size(); i++) {
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
    }

    protected SpannableString addClickablePart(final WenyouListData.CommentItem object) {
        final SpannableString ssbContent = FaceConversionUtil.getInstace().getExpressionString(context, object.content.trim());
        // 第一个赞图标
        if (!TextUtils.isEmpty(object.reply_sname)) {
            SpannableString ssbName = new SpannableString(object.reply_sname);
            if (object.reply_role == 1) {
                ssbName = new SpannableString(object.reply_sname + " ");
                Drawable d = context.getResources().getDrawable(R.drawable.icon_redv);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                ssbName.setSpan(span, ssbName.length() - 1, ssbName.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (object.reply_role == 2) {
                ssbName = new SpannableString(object.reply_sname + " ");
                Drawable d = context.getResources().getDrawable(R.drawable.icon_bluev);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                ssbName.setSpan(span, ssbName.length() - 1, ssbName.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            ssbName.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    if (alertDialog != null && alertDialog.isShowing()) {
                                        return;
                                    }
                                    if (object.reply_sname.equals(object.sname)) {
                                        Intent intent = new Intent(context, UserHomeActivity.class);
                                        intent.putExtra(BundleParamKey.UID, object.uid);
                                        context.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(context, UserHomeActivity.class);
                                        intent.putExtra(BundleParamKey.UID, object.reply_uid);
                                        context.startActivity(intent);
                                    }
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    // 设置被回复的人名颜色
                                    if (object.reply_role == 1) {
                                        ds.setColor(context.getResources().getColor(R.color.wenyou_list_name_red));
                                    } else if (object.reply_role == 2) {
                                        ds.setColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
                                    } else {
                                        ds.setColor(context.getResources().getColor(R.color.wenyou_list_name_normal));
                                    }

                                    // 去掉下划线
                                    ds.setUnderlineText(false);
                                }

                            }, 0, ssbName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return new SpannableString(TextUtils.concat(new SpannableString("回复 "), ssbName, new SpannableString("："), ssbContent));
        }
        return ssbContent;
    }
}
