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
                    "??????????", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        mySpinner = (Spinner) findViewById(R.id.vba_id);
        // ?????????????งา??????????????????????????ีว???list??
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        // ????????????????????????งา???????????????
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // ????????????????????????งา???
        mySpinner.setAdapter(adapter);
        // ???แด????????งา??????????????????????????????????
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                        /* ?????mySpinner ???????myTextView ?? */
                myTextView.setText(adapter.getItem(arg2));
						/* ??mySpinner ??? */
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                myTextView.setText("NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });
		/* ????????????????????????????? */
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                /**
                 *
                 */
                return false;
            }
        });

		/* ??????????????????????????????? */
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
