package com.weikan.app.original.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.face.FaceConversionUtil;
import com.weikan.app.face.FaceRelativeLayout;
import com.weikan.app.personalcenter.UserHomeActivity;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.FriendlyDate;
import com.weikan.app.util.Global;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.bean.WenyouCommentObject;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.widget.VerticalImageSpan;
import com.weikan.app.widget.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import platform.http.HttpUtils;
import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by chunhui on 15/11/28.
 */
public class OriginalCommentAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<WenyouListData.CommentItem> mOriginalCommentList;
    private InputDialog inputDialog;
    private String tid;


    ImageSpan[] spanBlueV;
    ImageSpan[] spanRedV;

    public OriginalCommentAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);


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
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setCommentList(ArrayList<WenyouListData.CommentItem> commentList) {
        mOriginalCommentList = commentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mOriginalCommentList == null || mOriginalCommentList.size() == 0) {
            return 1;
        }
        return mOriginalCommentList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (mOriginalCommentList == null || mOriginalCommentList.size() == 0) {
            return null;
        }
        return mOriginalCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = mInflater.inflate(R.layout.item_original_liuyan, null);
            TextView liuyan = (TextView) convertView.findViewById(R.id.liuyan);
            liuyan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentClick(mContext, "", "", "", 0);
                }
            });
            return convertView;
        } else {
            ViewHolder viewHolder = null;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mInflater.inflate(R.layout.item_original_comment, null);
                viewHolder = new ViewHolder();
                viewHolder.riPhoto = (RoundedImageView) convertView.findViewById(R.id.ri_photo);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment);
                viewHolder.tvZan = (TextView) convertView.findViewById(R.id.tv_zan);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.rlItem = (RelativeLayout) convertView.findViewById(R.id.rl_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//        if (mOriginalCommentList == null || mOriginalCommentList.size() == 0) {
//            return convertView;
//        }
//        if (mOriginalCommentList.get(position) == null) {
//            return convertView;
//        }
            final WenyouListData.CommentItem originalCommentObject = mOriginalCommentList.get(position - 1);
            viewHolder.tvName.setText(addClickablePart(originalCommentObject), TextView.BufferType.SPANNABLE);
            viewHolder.tvName.setMovementMethod(LinkMovementMethod.getInstance());
            setImage(viewHolder.riPhoto, originalCommentObject.avatar);
            Drawable nav_up = null;
            if (originalCommentObject.is_zan == 0) {
                nav_up = mContext.getResources().getDrawable(R.drawable.zan);
            } else {
                nav_up = mContext.getResources().getDrawable(R.drawable.un_zan);
            }
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            viewHolder.tvZan.setCompoundDrawables(nav_up, null, null, null);
            setText(viewHolder.tvZan, Integer.toString(originalCommentObject.zan_num));
            viewHolder.tvComment.setText(FaceConversionUtil.getInstace().getExpressionString(mContext, originalCommentObject.content));
            FriendlyDate fd = new FriendlyDate(originalCommentObject.ctime * 1000);
            viewHolder.tvTime.setText(fd.toFriendlyDate(false));
            RxView.clicks(viewHolder.riPhoto)
                    .map(new Func1<Void, String>() {
                        @Override
                        public String call(Void aVoid) {
                            return originalCommentObject.uid;
                        }
                    })
                    .subscribe(gotoUserHomeAction);
            RxView.clicks(viewHolder.tvComment)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            onCommentClick(mContext, originalCommentObject.cid, originalCommentObject.uid, originalCommentObject.sname, 0);
                        }
                    });
            viewHolder.tvZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (originalCommentObject.is_zan == 0) {
                        zan(2, originalCommentObject);
                    } else {
                        zan(1, originalCommentObject);
                    }
                }
            });
            RxView.clicks(viewHolder.rlItem)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            onCommentClick(mContext, originalCommentObject.cid, originalCommentObject.uid, originalCommentObject.sname, 0);
                        }
                    });
            RxView.longClicks(viewHolder.rlItem)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if (TextUtils.equals(originalCommentObject.uid, AccountManager.getInstance().getUserId())) {
                                showDeleteCommentDialog(originalCommentObject);
                            }
                        }
                    });
            RxView.longClicks(viewHolder.tvComment)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if (TextUtils.equals(originalCommentObject.uid, AccountManager.getInstance().getUserId())) {
                                showDeleteCommentDialog(originalCommentObject);
                            }
                        }
                    });
            //            RxView.clicks(viewHolder.replyName)
//                    .map(new Func1<Void, String>() {
//                        @Override
//                        public String call(Void aVoid) {
//                            return originalCommentObject.reply_uid;
//                        }
//                    })
//                    .subscribe(gotoUserHomeAction);
//        setImage(viewHolder.iconImageView, originalCommentObject.getAvatar());
//        setText(viewHolder.nickNameTextView, originalCommentObject.getSname());

//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(originalCommentObject.getUid())) {
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, UserHomeActivity.class);
//                    intent.putExtra("uid", originalCommentObject.getUid());
//                    mContext.startActivity(intent);
//                }
//            }
//        };
//        viewHolder.iconImageView.setOnClickListener(listener);
//        viewHolder.nickNameTextView.setOnClickListener(listener);
//        RxView.clicks(viewHolder.iconImageView)
//                .map(new Func1<Void, String>() {
//                    @Override
//                    public String call(Void aVoid) {
//                        return originalCommentObject.uid;
//                    }
//                })
//                .subscribe(gotoUserHomeAction);
//        RxView.clicks(viewHolder.nickNameTextView)
//                .map(new Func1<Void, String>() {
//                    @Override
//                    public String call(Void aVoid) {
//                        return originalCommentObject.uid;
//                    }
//                })
//                .subscribe(gotoUserHomeAction);
//
//        long createTime = originalCommentObject.ctime;
//        if (createTime == -1) {
//            setText(viewHolder.timeTextView, "刚刚");
//        } else {
//            setText(viewHolder.timeTextView, String.valueOf(getShowTime(originalCommentObject.getCtime())));
//        }
//
////        setText(viewHolder.contentTextView, originalCommentObject.getContent());
//        viewHolder.contentTextView.setText(FaceConversionUtil.getInstace().getExpressionString(mContext, originalCommentObject.getContent()));
//
//        if (!TextUtils.isEmpty(originalCommentObject.reply_sname)) {
//            viewHolder.replyText.setVisibility(View.VISIBLE);
//            viewHolder.replyName.setVisibility(View.VISIBLE);
//            viewHolder.replyName.setText(originalCommentObject.reply_sname);
//
//            RxView.clicks(viewHolder.replyName)
//                    .map(new Func1<Void, String>() {
//                        @Override
//                        public String call(Void aVoid) {
//                            return originalCommentObject.reply_uid;
//                        }
//                    })
//                    .subscribe(gotoUserHomeAction);
//        } else {
//            viewHolder.replyText.setVisibility(View.GONE);
//            viewHolder.replyName.setVisibility(View.GONE);
//        }
            return convertView;
        }
    }
    /**
     * 删除评论
     * @param obj
     */
    /**
     * 长按删除的菜单
     */
    protected static String[] sDeleteMenuList = new String[]{"删除", "取消"};
    protected AlertDialog alertDialog;

    public void showDeleteCommentDialog(final WenyouListData.CommentItem cmt) {

        alertDialog = new AlertDialog.Builder(mContext)
                .setItems(sDeleteMenuList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                sendDeleteRequest(cmt);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    protected void sendDeleteRequest(final WenyouListData.CommentItem item) {
        final ProgressDialog dia = new ProgressDialog(mContext);
        dia.setMessage("请稍后");
        dia.show();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.DEL_ORIGINAL_COMMENT);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TID, item.tid);
        params.put(URLDefine.CID, item.cid);

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.SimpleJsonResponseHandler() {
            @Override
            public void success() {
                mOriginalCommentList.remove(item);
                notifyDataSetChanged();
                LToast.showToast("删除成功。");
                dia.dismiss();
            }

            @Override
            public void end() {
                dia.dismiss();
            }
        });
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

            str = likeUsers[0] + " 回复了 " + likeUsers[1];
            // 存在两个名字相同的情况，所以单独记录
            secondstartPos = str.length() - likeUsers[1].length();
        }
        SpannableString ssb = FaceConversionUtil.getInstace().getExpressionString(mContext, str);
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
                                            Intent intent = new Intent(mContext, UserHomeActivity.class);
                                            intent.putExtra(BundleParamKey.UID, object.uid);
                                            mContext.startActivity(intent);
                                        } else if (finalI == 1) {
                                            Intent intent = new Intent(mContext, UserHomeActivity.class);
                                            intent.putExtra(BundleParamKey.UID, object.reply_uid);
                                            mContext.startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        if (finalI == 0) {
                                            // 设置评论的人名颜色
                                            if (object.role == 1) {
                                                ds.setColor(mContext.getResources().getColor(R.color.wenyou_list_name_red)); // 设置文本颜色
                                            } else {
                                                ds.setColor(mContext.getResources().getColor(R.color.wenyou_list_name_normal));
                                            }
                                        } else {
                                            // 设置被回复的人名颜色
                                            if (object.reply_role == 1) {
                                                ds.setColor(mContext.getResources().getColor(R.color.wenyou_list_name_red)); // 设置文本颜色
                                            } else {
                                                ds.setColor(mContext.getResources().getColor(R.color.wenyou_list_name_normal));
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

    private Action1<String> gotoUserHomeAction = new Action1<String>() {
        @Override
        public void call(String s) {
            if (!TextUtils.isEmpty(s)) {
                Intent intent = new Intent();
                intent.setClass(mContext, UserHomeActivity.class);
                intent.putExtra("uid", s);
                mContext.startActivity(intent);
            }
        }
    };

    private void zan(final Integer operator, final WenyouListData.CommentItem item) {
        if (!AccountManager.getInstance().isLogin()) {
            AccountManager.getInstance().gotoLogin((Activity) mContext);
            return;
        } else {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(URLDefine.SCHEME);
            builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
            builder.encodedPath(URLDefine.ZAN_ADDARTICLE);
            Map<String, String> params = new HashMap<String, String>();
            params.put("uid", AccountManager.getInstance().getUserId());
            params.put("cid", item.cid);
            params.put("operator", Integer.toString(operator));
            HttpUtils.get(builder.build().toString(), params, new SimpleJsonResponseHandler() {
                @Override
                public void success() {
                    if (operator == 1) {
                        LToast.showToast("点赞成功");
                        item.zan_num++;
                        item.is_zan = 0;
                    } else {
                        LToast.showToast("取消点赞成功");
                        item.zan_num--;
                        item.is_zan = 1;
                    }
                    notifyDataSetChanged();
                }

                @Override
                protected void failed(FailedResult r) {
                    super.failed(r);
                    if (operator == 1) {
                        LToast.showToast("点赞失败");
                        item.is_zan = 0;
                    } else {
                        LToast.showToast("取消点赞失败");
                        item.is_zan = 1;
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void setImage(ImageView imageView, String url) {
        if (imageView == null) {
            return;
        }
        ImageLoaderUtil.updateImage(imageView, url, R.drawable.user_default);
    }

    private void setText(TextView textView, String text) {
        if (textView == null || TextUtils.isEmpty(text)) {
            return;
        }
        Log.e("ronaldo", text);
        textView.setText(text);
    }

    public class ViewHolder {
        public RoundedImageView riPhoto;
        public TextView tvName;
        public TextView tvZan;
        public TextView tvComment;
        public TextView tvTime;
        public RelativeLayout rlItem;
    }

    private String getShowTime(long time) {
        FriendlyDate friendlyDate = new FriendlyDate(time * 1000);
        return friendlyDate.toFriendlyDate(false);
    }

    public class InputDialog extends Dialog implements
            android.view.View.OnClickListener {
        private WenyouListData.CommentItem item;
        String r_cid;
        String r_uid;
        String r_sname;
        int r_role;
        EditText et;
        Button bt;
        FaceRelativeLayout faceLayout;

        public InputDialog(Context context) {
            super(context, R.style.MyDialog);
        }

        public void setData(String reply_cid, String reply_uid,
                            String reply_sname, int reply_role) {

            this.r_cid = reply_cid;
            this.r_uid = reply_uid;
            this.r_sname = reply_sname;
            this.r_role = reply_role;
            if (et != null) {
                et.setText("");
            }
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
                    String comment = et.getText().toString();
                    if (TextUtils.isEmpty(comment)) {
                        LToast.showToast("评论不能为空");
                        return;
                    }
                    if (comment.length() > Constants.commentMaxLength) {
                        LToast.showToast(MainApplication.getInstance().getResources().getString(R.string.comment_pub_beyond, Constants.commentMaxLength));
                        return;
                    }
                    toComment(comment, tid, r_cid, r_uid, r_sname, r_role);
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

    protected void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
        if (view != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }

    }


    protected void closeInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive(view);
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

    protected void onCommentClick(Context context, String reply_cid, String reply_uid,
                                  String reply_sname, int reply_role) {
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

        inputDialog.setData(reply_cid, reply_uid, reply_sname, reply_role);
        inputDialog.show();

    }

    protected void toComment(final String content, final String tid, final String reply_cid, final String reply_uid, final String reply_sname, final int reply_role) {
        final ProgressDialog dia = new ProgressDialog(mContext);
        dia.setMessage("请稍后");
        dia.show();
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
                LToast.showToast("发布成功");
                WenyouListData.CommentItem ci = new WenyouListData.CommentItem();
                ci.tid = tid;
                ci.cid = data.cid + "";
                ci.uid = AccountManager.getInstance().getUserId();
                ci.avatar = AccountManager.getInstance().getUserData().headimgurl;
                ci.sname = AccountManager.getInstance().getUserData().nick_name;
                ci.content = content;
                ci.reply_sname = reply_sname;
                ci.reply_cid = reply_cid;
                ci.reply_uid = reply_uid;
                ci.reply_role = reply_role;
                ci.ctime = new Date().getTime() / 1000;
                ci.is_zan = 1;
                ci.zan_num = 0;
                mOriginalCommentList.add(0, ci);
                notifyDataSetChanged();
            }

            @Override
            public void end() {
                super.end();
                dia.dismiss();
            }
        });
    }
}
