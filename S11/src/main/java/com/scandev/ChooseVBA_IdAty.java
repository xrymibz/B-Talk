package com.scandev;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.widget.AdapterView;

public class ChooseVBA_IdAty extends Activity {

    private List<String> list = new ArrayList<String>();
    private TextView myTextView;
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    //private String result = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_vbaid);
        Button vbabutton = (Button) findViewById(R.id.vbasearchbutton);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String str = bundle.getString("str");
        if ((str != "") && (str.length() > 2)) {
            str = str.substring(1, str.length() - 1);
            String strlist[] = str.split(",");
            myTextView = (TextView) findViewById(R.id.choose_vba);
            for (int i = 0; i < strlist.length; i++) {
                list.add(strlist[i].substring(1, strlist[i].length() - 1));

            }
            vbabutton.setOnClickListener(mSendClickListener);
        } else {
            Toast toast = new Toast(this);
            toast = Toast.makeText(getApplicationContext(),
                    "搜索结果为空", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        mySpinner = (Spinner) findViewById(R.id.vba_id);
        // 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        // 第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(adapter);
        // 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                        /* 将所选mySpinner 的值带入myTextView 中 */
                myTextView.setText(adapter.getItem(arg2));
						/* 将mySpinner 显示 */
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                myTextView.setText("NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });
		/* 下拉菜单弹出的内容选项触屏事件处理 */
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                /**
                 *
                 */
                return false;
            }
        });

		/* 下拉菜单弹出的内容选项焦点改变事件处理 */
        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });
    }

    private OnClickListener mSendClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            String vbaId = myTextView.getText().toString();
            Intent intent = new Intent();
            intent.setClass(ChooseVBA_IdAty.this, DetailAty.class);
            intent.putExtra("str", vbaId);
            startActivity(intent);
        }

    };
}
