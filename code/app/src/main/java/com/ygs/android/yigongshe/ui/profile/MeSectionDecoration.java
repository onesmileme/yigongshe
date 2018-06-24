package com.ygs.android.yigongshe.ui.profile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ygs.android.yigongshe.R;

import java.util.List;

public class MeSectionDecoration extends RecyclerView.ItemDecoration {

    private List<Integer> mShowList ;
    private Paint paint;
    private int sectionHight = 10;

    public MeSectionDecoration(List<Integer> showList, Context context){
        super();
        mShowList = showList;
        paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.gray));
    }

    @Override
    public void getItemOffsets(Rect outRect , View view ,
                               RecyclerView parent , RecyclerView.State state){

        super.getItemOffsets(outRect,view,parent,state);

        int pos = parent.getChildAdapterPosition(view);

        if (mShowList != null && mShowList.contains(pos)){
            outRect.top = sectionHight;
        }else{
            outRect.top = 0;
        }

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);


            if (mShowList != null && mShowList.contains(position)) {
                float top = view.getTop() - sectionHight;
                float bottom = view.getTop();
                //绘制矩形
                Rect rect = new Rect(left, (int) top, right, (int) bottom);
                c.drawRect(rect, paint);
            }
        }

    }

    public void setHintHight(int height){
        sectionHight = height;
    }


}
