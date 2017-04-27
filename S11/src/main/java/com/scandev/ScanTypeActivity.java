package com.scandev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by xietian on 2017/4/27.
 */

public class ScanTypeActivity extends BaseTitleAcitvity {
    private String title = "扫描方式";
    private String arcType;
    private String TAG = "ScanTypeActivity";
    SharedPreferences login_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated METHOD_LOGIN stub
        super.onCreate(savedInstanceState);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);

        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        Intent intent = getIntent();
        arcType = intent.getStringExtra("arcType");
        System.out.println(arcType+"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_scantype;//任意非空布局
    }

    public void outofFC(View v) {
        Log.i(TAG, "Go To Scan Page");
        Intent intent = new Intent();
        intent.putExtra("functionCode", 0);
        SharedPreferences.Editor editor = login_user.edit();
        editor.putString("ScanType","out");
        editor.commit();

        if(arcType.equals("Injection")){
            //Injection,跳转到车型选择界面,没有来源和目的仓库，所以显式置空
            editor.putString("sourceFC", "");
            editor.putString("destinationFC", "");
            intent.setClass(ScanTypeActivity.this,  CarTypeActivity.class);
        }else{
            //非Injection，跳转到arc选择界面
            intent.setClass(ScanTypeActivity.this, ArcActivity.class);
        }

        startActivity(intent);
    }

    public void inofFC(View v) {
        Log.i(TAG, "Go To History Page");
        Intent intent = new Intent();
        SharedPreferences.Editor editor = login_user.edit();
        editor.putString("ScanType","in");
        editor.commit();
        intent.setClass(ScanTypeActivity.this, ArcActivity.class);
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

