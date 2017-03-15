package com.scandev.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scandev.ExceptionCheckActivity;
import com.scandev.ExceptionEditActivity;
import com.scandev.R;
import com.scandev.ScanActivity;
import com.scandev.tasks.UploadScanListTask;

import java.util.Map;


public class PickupConfirmDialog extends Dialog{

    private TextView dialogTitle;
    private TextView operatorView;
    private TextView laneView;
    private TextView arcView;
    private TextView carstype;
    private TextView carnumber;
    private TextView scanedView;
    private TextView notScanedView;
    private TextView excessView;
    private TextView exceptionView;
    private TextView totalNumView;
    private TextView supposedScanView;

    private Button positive;
    private Button negative;
    private Button supException;
    private Button review;

    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;
    private View.OnClickListener createExceptionListener;
    private View.OnClickListener reviewExceptionListener;

    SharedPreferences login_user;

    private ScanActivity activity;

    public PickupConfirmDialog(ScanActivity context){
        super(context, R.style.CustomDialog);
        activity = context;
        setCustomView();

        positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickupConfirmDialog.this.dismiss();
                UploadScanListTask uploadScanListTask = new UploadScanListTask(activity);
                uploadScanListTask.execute(activity.uploadData);
            }
        };

        negativeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickupConfirmDialog.this.dismiss();
            }
        };

        createExceptionListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("fluency", 0);
                intent.setClass(activity, ExceptionEditActivity.class);
                activity.startActivityForResult(intent, 1);
                PickupConfirmDialog.this.dismiss();
            }
        };

        reviewExceptionListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("dataList", activity.exceptionItemList);
                intent.setClass(activity, ExceptionCheckActivity.class);
                activity.startActivityForResult(intent, 0);
                PickupConfirmDialog.this.dismiss();
            }
        };
    }

    private void setCustomView(){
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pickupconfirm,null);
        dialogTitle = (TextView) mView.findViewById(R.id.dialogTitle);
        operatorView = (TextView)mView.findViewById(R.id.operator);
        laneView = (TextView)mView.findViewById(R.id.lane);
   //     arcView = (TextView)mView.findViewById(R.id.arc);
        carstype = (TextView)mView.findViewById(R.id.cartype);
        carnumber = (TextView)mView.findViewById(R.id.carnumber);
        totalNumView = (TextView)mView.findViewById(R.id.totalnum);
        supposedScanView = (TextView)mView.findViewById(R.id.supposednum);
        scanedView = (TextView)mView.findViewById(R.id.scaned);
        notScanedView = (TextView)mView.findViewById(R.id.notscaned);
        excessView = (TextView)mView.findViewById(R.id.excess);
        exceptionView = (TextView)mView.findViewById(R.id.exception);
        positive = (Button)mView.findViewById(R.id.confirmupload);
        negative = (Button)mView.findViewById(R.id.returntoscan);
        supException = (Button)mView.findViewById(R.id.supException);
        review = (Button)mView.findViewById(R.id.reviewException);

        super.setContentView(mView);
    }


    public void setMessage(Map<String,String> datas){


        dialogTitle.setText(R.string.pickupconfirmtitle);
        operatorView.setText(datas.get("carriername"));
    //    arcView.setText(datas.get("arcname"));
        if(datas.get("arctype").equals("Injection")) {
            laneView.setText(datas.get("lanename"));
            carstype.setText("³µÐÍ £º " + datas.get("carType"));
            carnumber.setText("³µÅÆ £º " + datas.get("carNumber"));
        }else{
            carstype.setText("");
            carnumber.setText("");
            laneView.setText(datas.get("lanename")+"  "+datas.get("arcname"));
        }
        totalNumView.setText(datas.get("listtotalnum"));
        supposedScanView.setText(datas.get("supposedscannum"));
        scanedView.setText(datas.get("scanednum"));
        notScanedView.setText(datas.get("notscanednum"));
        excessView.setText(datas.get("excessnum"));
        exceptionView.setText(datas.get("exceptionnum"));

        positive.setOnClickListener(positiveListener);
        negative.setOnClickListener(negativeListener);
        supException.setOnClickListener(createExceptionListener);
        review.setOnClickListener(reviewExceptionListener);
    }
}
