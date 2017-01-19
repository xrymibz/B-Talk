package com.scandev;

import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class FunctionActivity extends ActionBarActivity {
    private int laneId;
    private String arcType;
    private String TAG = "FunctionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated METHOD_LOGIN stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        laneId = Integer.parseInt(intent.getStringExtra("laneId"));
        arcType = intent.getStringExtra("arcType");

        System.out.println("landid : " + laneId + "arcType : "+ arcType);
    }

    public void beginScan(View v) {
        Log.i(TAG, "Go To Scan Page");
        Intent intent = new Intent();
        intent.putExtra("laneId", laneId + "");
        intent.putExtra("functionCode", 0);

        if(arcType.equals("Injection")){
            intent.setClass(FunctionActivity.this,  ScanActivity.class);
        }else{
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
        intent.setClass(FunctionActivity.this, ArcActivity.class);
        startActivity(intent);
    }

    public void queryException(View v) {
        Log.i(TAG, "Go To Exception Query Page");
        Intent intent = new Intent();
        intent.putExtra("laneId", laneId + "");
        intent.putExtra("functionCode", 2);
        intent.setClass(FunctionActivity.this, ArcActivity.class);
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
