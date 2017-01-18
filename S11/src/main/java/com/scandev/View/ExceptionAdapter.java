package com.scandev.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scandev.R;

import java.util.List;

public class ExceptionAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext = null;
    private List<String[]> items;

    public ExceptionAdapter(Context context, List<String[]> list){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        items = list;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView typeView;
        TextView codeView;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_exception, null);
        }

        typeView = (TextView) convertView.findViewById(R.id.exceptionType);
        codeView = (TextView) convertView.findViewById(R.id.barCode);

        String expType = items.get(position)[2];
        /*switch (expType){
            case "CargoDamage":
                expType = mContext.getString(R.string.CargoDamage);
                break;
            case "CargoExcess":
                expType = mContext.getString(R.string.CargoExcess);
                break;
            case "BarcodeMiss":
                expType = mContext.getString(R.string.BarcodeMiss);
                break;
            case "Others":
                expType = mContext.getString(R.string.Others);
                break;
            default:
                expType = mContext.getString(R.string.Others);
                break;
        }*/
        typeView.setText(expType);
        codeView.setText(items.get(position)[0]);

        return convertView;
    }
}
