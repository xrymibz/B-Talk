package com.scandev;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private String TAG = "ScanTypeActivity";
    SharedPreferences login_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated METHOD_LOGIN stub
        super.onCreate(savedInstanceState);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);

        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));



    }

    @Override
    protected int getContentView() {
        return R.layout.activity_scantype;//任意非空布局
    }

    public void outofFC(View v) {
        Log.i(TAG, "出库");
        Intent intent = new Intent();
        intent.putExtra("functionCode", 0);
        SharedPreferences.Editor editor = login_user.edit();
        editor.putString("scanType","out");
        editor.commit();
        intent.setClass(ScanTypeActivity.this,  HomeActivity.class);


        startActivity(intent);
    }

    public void inofFC(View v) {
        Log.i(TAG, "入库");
        Intent intent = new Intent();
        if(login_user.getString("isInjection","0").equals("1")){
            new AlertDialog.Builder(ScanTypeActivity.this).setTitle("系统提示")//设置对话框标题

                    .setMessage("Injection类型目前不支持入库操作，请重新选择扫描类型")//设置显示的内容

                    .setNegativeButton("确定",new DialogInterface.OnClickListener() {//添加返回按钮



                        @Override

                        public void onClick(DialogInterface dialog, int which) {//响应事件

                            // TODO Auto-generated method stub

                            Log.i("alertdialog"," 请保存数据！");

                        }

                    }).show();//在按键响应事件中显示此对话框
        }else{
            SharedPreferences.Editor editor = login_user.edit();
            editor.putString("scanType","in");
            editor.commit();
            intent.setClass(ScanTypeActivity.this, HomeActivity.class);
            startActivity(intent);
        }
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

