package com.scandev.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by renjicui on 16/7/6.
 */
public class GridViewForScroll extends GridView {
    public GridViewForScroll(Context context) {
        super(context);
    }

    public GridViewForScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewForScroll(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
