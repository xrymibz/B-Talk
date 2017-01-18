package com.scandev.View;

import java.util.*;

import android.content.Intent;
import android.graphics.Color;

import com.scandev.ExceptionEditActivity;
import com.scandev.R;
import com.scandev.ScanActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.scandev.model.CurrentScanItem;

public class ScanAdapter extends BaseAdapter {
	private LayoutInflater inflater = null;
	//public HashMap<String,Boolean> isCheck = null;
    public List<CurrentScanItem> array = new LinkedList<CurrentScanItem>();
    private ScanActivity activity;
	private String arcType;

	public ScanAdapter(Context context,List<CurrentScanItem>  array) {
		this.array = array;
        activity = (ScanActivity)context;
		arcType = activity.uploadData.get("arctype");
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated METHOD_LOGIN stub
		return array.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated METHOD_LOGIN stub
		return array.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated METHOD_LOGIN stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

        CurrentScanItem item = array.get(position);
		System.out.println("position:"+position);
		View view = inflater.inflate(R.layout.list_scan, null);
		TextView scanId = (TextView) view.findViewById(R.id.scanId);
		Button boxCode = (Button) view.findViewById(R.id.caseImage);
		final CheckBox ceb = (CheckBox)view.findViewById(R.id.check);
		Button btn = (Button) view.findViewById(R.id.postDamage);

		if(arcType.equals("VReturn")){
			boxCode.setVisibility(View.VISIBLE);
			boxCode.setText(item.getBoxCode());
		}
		else{
			boxCode.setVisibility(View.INVISIBLE);
		}
        if(item.isCurrentCount()){
            scanId.setTextColor(activity.getResources().getColor(R.color.new_code));
        }
        else{
            scanId.setTextColor(Color.BLACK);
        }
        scanId.setText(item.getBarCode());
		ceb.setChecked(item.isSelected());
		ceb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				CurrentScanItem item = array.get(position);
				item.setSelected(isChecked);
				if(isChecked) activity.selectedItems.add(item);
				else activity.selectedItems.remove(item);
			}
		});

		if(item.isDamaged()){
			btn.setBackgroundResource(R.drawable.selector_radio_button);
		}
		else{
			btn.setBackgroundResource(R.drawable.selector_button_function);
		}

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CurrentScanItem item = (CurrentScanItem)ScanAdapter.this.getItem(position);
                Intent intent = new Intent();
                intent.setClass(activity, ExceptionEditActivity.class);
                intent.putExtra("barcode", item.getBarCode());
                intent.putExtra("fluency", 1);
                activity.startActivityForResult(intent, 2);
			}
		});
		return view;
	}

}
 

