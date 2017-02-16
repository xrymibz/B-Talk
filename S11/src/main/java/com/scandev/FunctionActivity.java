package com.scandev;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class FunctionActivity extends BaseTitleAcitvity {
    private String title = "功能选择";
    private int laneId;
    private String arcType;
    private String TAG = "FunctionActivity";
    SharedPreferences login_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated METHOD_LOGIN stub
        super.onCreate(savedInstanceState);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);

        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        Intent intent = getIntent();

        laneId = Integer.parseInt(intent.getStringExtra("laneId"));
        arcType = intent.getStringExtra("arcType");

        System.out.println("landid : " + laneId + "arcType : "+ arcType);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_functions;//任意非空布局
    }

    public void beginScan(View v) {
        Log.i(TAG, "Go To Scan Page");
        Intent intent = new Intent();
        intent.putExtra("laneId", laneId + "");
        intent.putExtra("functionCode", 0);

        if(arcType.equals("Injection")){
            //Injection,跳转到车型选择界面
            intent.setClass(FunctionActivity.this,  CarTypeActivity.class);
        }else{
            //非Injection，跳转到arc选择界面
            intent.setClass(FunctionActivity.this, ArcActivity.class);
        }

        startActivity(intent);
    }

    public void checkHistory(View v) {
        Log.i(TAG, "Go To History Page");
        Intent intent = new Intent();
        intent.putExtra("laneId", laneId + "");
        intent.setClass(FunctionActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    public void postException(View v) {
        Log.i(TAG, "Go To Exception Edit Page");
        Intent intent = new Intent();
        intent.putExtra("laneId", laneId + "");
        intent.putExtra("functionCode", 1);
        if(arcType.equals("Injection")){
            intent.setClass(FunctionActivity.this, ExceptionEditActivity.class);
            intent.putExtra("fluency", -1);
        }else{
            intent.setClass(FunctionActivity.this, ArcActivity.class);
        }


        startActivity(intent);
    }

    public void queryException(View v) {
        Log.i(TAG, "Go To Exception Query Page");
        Intent intent = new Intent();
        intent.putExtra("laneId", laneId + "");
        intent.putExtra("functionCode", 2);
        if(arcType.equals("Injection")){
            intent.setClass(FunctionActivity.this, ExceptionQueryActivity.class);
        }else{
            intent.setClass(FunctionActivity.this, ArcActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }


}
