package com.scandev.View;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.scandev.R;
import com.scandev.ScanActivity;
import com.scandev.model.BoxCodeMap;
import com.scandev.model.Constant;


public class CaseScanDialog extends Dialog {
    private String barCode;
    private ScanActivity context;
    private Button confirmBtn;
    private MyRadioGroup group0;
    private RadioButton preBtn;
    private RadioButton curBtn;

    View mView;
    String boxCode = null;

    @Override
    public void cancel() {
        context.unregisterReceiver(dynamicReceiver);
        super.cancel();
    }

    public CaseScanDialog(ScanActivity activity, String barCode) {
        super(activity, R.style.CustomDialog);
        context = activity;
        this.barCode = barCode;

        IntentFilter broadFilter = new IntentFilter();
        broadFilter.addAction(Constant.STATICATION);
        context.registerReceiver(dynamicReceiver, broadFilter);
        setCustomView();
    }

    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            curBtn = (RadioButton) mView.findViewById(checkedId);
            if (!curBtn.isSelected()) {
                curBtn.setSelected(true);
                if (preBtn == null) {
                    preBtn = curBtn;
                } else {
                    preBtn.setSelected(false);
                    preBtn = curBtn;
                }
            }

            boxCode = curBtn.getText().toString();
            confirmBtn.setText(context.getString(R.string.positive) + boxCode);
        }
    };

    private void setCustomView() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_case_scan, null);
        confirmBtn = (Button) mView.findViewById(R.id.confirmBox);
        group0 = (MyRadioGroup) mView.findViewById(R.id.group0);
        group0.setOnCheckedChangeListener(listener);
        listener.onCheckedChanged(group0, R.id.Private);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxCode != null) {
                    context.callBackRecord(barCode, boxCode);
                    context.unregisterReceiver(dynamicReceiver);
                    CaseScanDialog.this.dismiss();
                }
            }
        });
        super.setContentView(mView);
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.STATICATION)) {
                boxCode = intent.getStringExtra("boxCode");
                //让对应的button变成selected
                //listener.onCheckedChanged(group0, BoxCodeMap.getButtonId(boxCode));
                CaseScanDialog.this.context.callBackRecord(barCode, boxCode);
                CaseScanDialog.this.context.unregisterReceiver(dynamicReceiver);
                CaseScanDialog.this.dismiss();
            }
        }
    };
}
