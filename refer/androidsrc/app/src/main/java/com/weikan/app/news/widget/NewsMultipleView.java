package com.weikan.app.news.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.common.util.Contract;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.util.FriendlyDate;


/**
 * 新闻列表中的项，有文本，带多张图。这里只显示三张。
 *
 * @author kailun on 16/1/8
 */
public class NewsMultipleView extends BaseListItemView<OriginalItem> implements INewsView {

    private TextView tvTitle;
    private ImageView ivPic0;
    private ImageView ivPic1;
    private ImageView ivPic2;

    private LinearLayout llAuthor;
    private TextView tvAuthor;
    private TextView tvTime;
    private TextView tvBrief;  // 摘要
    private TextView tvRead; // 阅读数

    public NewsMultipleView(Context context) {
        super(context);
    }

    @Override
    protected void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvBrief = (TextView) findViewById(R.id.tv_brief);
        ivPic0 = (ImageView) findViewById(R.id.iv_pic0);
        ivPic1 = (ImageView) findViewById(R.id.iv_pic1);
        ivPic2 = (ImageView) findViewById(R.id.iv_pic2);

        llAuthor = (LinearLayout) findViewById(R.id.ll_author);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvRead = (TextView) findViewById(R.id.tv_read);
    }
    /**
     * 设置阅读数数量等到达（*万）是显示（*W+）
     *
     * @param view
     * @param num
     */
    private void setReadNumText(@NonNull TextView view, @NonNull int num) {
        if (num <= 0) {
            view.setText("阅读 " + " 0");
        } else if (num >= 10000) {
            view.setText("阅读 " + num / 10000 + "W+");
        } else {
            view.setText("阅读 " + num + "");
        }
    }

    @Override
    public void set(@Nullable OriginalItem item) {
        super.set(item);

        if (item != null) {
            tvTitle.setText(item.title);

            String brief = item.contentAbstract.replaceAll("\n", "");//“\n”导致文字格式混乱问题。
            tvBrief.setText(brief);

            NewsViewFactory.setPictureInto(ivPic0, item.imgs, 0);
            NewsViewFactory.setPictureInto(ivPic1, item.imgs, 1);
            NewsViewFactory.setPictureInto(ivPic2, item.imgs, 2);

            setReadNumText(tvRead, item.read_num);

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
        return TemplateConfig.getConfig().getLayoutNewsMultiple();
    }
}
