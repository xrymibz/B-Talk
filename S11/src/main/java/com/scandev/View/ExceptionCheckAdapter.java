package com.scandev.View;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.scandev.R;
import com.scandev.model.CurrentScanItem;
import com.scandev.model.ExceptionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExceptionCheckAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<ExceptionItem> dataList;
    private Set<Integer> checkList;
    public ExceptionCheckAdapter(Context context, ArrayList<ExceptionItem> dataList, Set<Integer> checkList) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.checkList = checkList;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView typeView;
        TextView codeView;
        CheckBox cb;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_excepiton_check, null);
        }

        typeView = (TextView) convertView.findViewById(R.id.excep_type);
        codeView = (TextView) convertView.findViewById(R.id.excep_code);
        cb = (CheckBox) convertView.findViewById(R.id.check);

        ExceptionItem e = (ExceptionItem) getItem(position);
        String expType;
        switch (e.getType()){
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
        }
        typeView.setText(expType);
        codeView.setText(e.getBarCode());

        cb.setChecked(checkList.contains(position));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) checkList.add(position);
                else checkList.remove(position);
            }
        });

        return convertView;
    }
}
