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
    private String title = "ɨ�跽ʽ";
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
        return R.layout.activity_scantype;//����ǿղ���
    }

    public void outofFC(View v) {
        Log.i(TAG, "����");
        Intent intent = new Intent();
        intent.putExtra("functionCode", 0);
        SharedPreferences.Editor editor = login_user.edit();
        editor.putString("scanType","out");
        editor.commit();
        intent.setClass(ScanTypeActivity.this,  HomeActivity.class);


        startActivity(intent);
    }

    public void inofFC(View v) {
        Log.i(TAG, "���");
        Intent intent = new Intent();
        if(login_user.getString("isInjection","0").equals("1")){
            new AlertDialog.Builder(ScanTypeActivity.this).setTitle("ϵͳ��ʾ")//���öԻ������

                    .setMessage("Injection����Ŀǰ��֧����������������ѡ��ɨ������")//������ʾ������

                    .setNegativeButton("ȷ��",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť



                        @Override

                        public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�

                            // TODO Auto-generated method stub

                            Log.i("alertdialog"," �뱣�����ݣ�");

                        }

                    }).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���
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

