package com.weikan.app.news.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.common.widget.IHasLayoutResource;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.util.FriendlyDate;


/**
 * 新闻列表中的项，有文本，带一张图
 *
 * @author kailun on 16/1/8
 */
public class NewsSingleView extends BaseListItemView<OriginalItem> implements IHasLayoutResource, INewsView {

    private TextView tvTitle;
    private TextView tvBrief; //摘要

    private ImageView ivPic;
    private LinearLayout llAuthor;
    private TextView tvRead; //阅读数

    private TextView tvAuthor;
    private TextView tvTime;

    public NewsSingleView(Context context) {
        super(context);
    }

    @Override
    protected void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvBrief = (TextView) findViewById(R.id.tv_brief);
        ivPic = (ImageView) findViewById(R.id.iv_pic);

        llAuthor = (LinearLayout) findViewById(R.id.ll_author);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvRead = (TextView) findViewById(R.id.tv_read);
    }

    /**
     * 设置阅读数数量等到达（*万）是显示（*W+）
     *
     * @param view view
     * @param num num
     */
    private void setReadNumText(@NonNull TextView view, int num) {
        if (num <= 0) {
            view.setText("阅读 " + "0");
        } else if (num >= 10000) {
            view.setText("阅读 " + num / 10000 + "W+");
        } else {
            view.setText("阅读 " + num);
        }
    }

    @Override
    public void set(@Nullable OriginalItem item) {
        super.set(item);

        if (item != null) {

            tvTitle.setText(item.title);

            String brief = item.contentAbstract.replaceAll("\n", "");//“\n”导致文字格式混乱问题。
            tvBrief.setText(brief);

            NewsViewFactory.setPictureIntoForceVisible(ivPic, item.imgs, 0);

            setReadNumText(tvRead,item.read_num);

            tvAuthor.setText(item.author);
            FriendlyDate favDate = new FriendlyDate(item.pubtime * 1000L);
            tvTime.setText(favDate.toFriendlyDate(false));
        }
    }

    @Override
    public void setOnItemClickListener(OnClickListener listener) {
        setOnClickListener(listener);
    }

    @Override
    public int layoutResourceId() {
        return TemplateConfig.getConfig().getLayoutNewsSingle();
    }
}
