package com.scandev.utils;

import java.util.List;
import java.util.Map;

import com.scandev.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class MySimpleAdapter extends SimpleAdapter {

    public int selectIndex = -1;

    public MySimpleAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource, String[] from,
                           int[] to) {
        super(context, data, resource, from, to);
        // TODO Auto-generated constructor stub
    }

    private static int[] mColors = {R.drawable.bkcolor2, R.drawable.selector};

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View localView = super.getView(position, convertView, parent);
        if (position % 2 == 0) {
            localView.setBackgroundResource(R.drawable.item);
        } else if (position % 2 != 0) {
            localView.setBackgroundResource(R.drawable.bkcolor);
        }
        if (position == selectIndex) {
            localView.setBackgroundResource(R.drawable.selector);
        }

        return localView;
    }

}
