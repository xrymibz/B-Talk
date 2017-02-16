package com.scandev;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.scandev.View.ExceptionCheckAdapter;
import com.scandev.model.ExceptionItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ExceptionCheckActivity extends BaseTitleAcitvity implements AdapterView.OnItemClickListener {

    @Override
    protected int getContentView() {
        return R.layout.activity_exception_check;
    }
    private ListView listView;
    private String title = "²é¿´Òì³£";
    SharedPreferences login_user;
    private boolean clean;
    private Set<Integer> array_selected;
    private ArrayList<ExceptionItem> dataList;
    private ExceptionItem modifyException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        clean = true;
//        setContentView(R.layout.activity_exception_check);
        listView = (ListView) findViewById(R.id.exceptlist);
        array_selected = new HashSet<>();

        Intent intent = getIntent();
        dataList = intent.getParcelableArrayListExtra("dataList");

        ExceptionCheckAdapter adapter = new ExceptionCheckAdapter(this, dataList, array_selected);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }



    public void delete(View v) {
        if (array_selected.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.deleteexception1) + array_selected.size() + getString(R.string.deleteexception2));
            builder.setTitle(R.string.notify);
            builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                            delException();
                        }
                    }
            );
            builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void delException() {
        clean = false;
        int k = 0;

        while (array_selected.size() > 0) {
            int i = getMin(array_selected);
            if (i == -1) Log.e("Error", "error when getMin");
            array_selected.remove(i);
            dataList.remove(i - k);
            k++;
        }
        array_selected.clear();
        ExceptionCheckAdapter adapter = new ExceptionCheckAdapter(this, dataList, array_selected);
        listView.setAdapter(adapter);
    }

    private int getMin(Set<Integer> set) {
        Iterator<Integer> iter = set.iterator();
        int min;
        int cur;
        if (!iter.hasNext()) {
            return -1;
        }
        min = iter.next();
        while (iter.hasNext()) {
            cur = iter.next();
            if (min > cur) min = cur;
        }
        return min;
    }

    private void backToScan() {
        if (!clean) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("data", dataList);
            setResult(3, intent);
        }
    }

    public void cancel(View v) {
        backToScan();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        backToScan();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToScan();
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clean = false;
        ExceptionItem msg = dataList.get(position);
        modifyException = msg;
        Intent intent = new Intent();
        intent.putExtra("fluency", 2);
        intent.putExtra("exception", msg);
        intent.setClass(this, ExceptionEditActivity.class);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                ExceptionItem item = data.getParcelableExtra("exception");
                int index = dataList.indexOf(modifyException);
                dataList.add(index, item);
                dataList.remove(modifyException);
                array_selected = new HashSet<>();
                ExceptionCheckAdapter adapter = new ExceptionCheckAdapter(this, dataList, array_selected);
                listView.setAdapter(adapter);
                break;
            default:
                break;
        }
    }
}
