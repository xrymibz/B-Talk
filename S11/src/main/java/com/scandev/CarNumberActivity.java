package com.scandev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.scandev.utils.MySimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xietian on 2017/2/13.
 */

public class CarNumberActivity extends BaseTitleAcitvity {

    private String title = "请输入车牌号";
    private String carType = "";
    private TextView carnumberType = null;
    private ListView listView = null;
    private EditText carNumberEdit = null;
    SharedPreferences login_user;
    MySimpleAdapter adapter = null;
    List<Map<String, Object>> carstypelist = null;
    private static final int COMPLETED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carNumberEdit = (EditText)findViewById(R.id.carnumberedit);
        carnumberType = (TextView)findViewById(R.id.carnumberType);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 给左上角图标的左边加上一个返回的图标
//        setContentView(R.layout.activity_carnumber);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        carType = login_user.getString("carType","");
        carnumberType.setText(carType);
        new Thread(getArc).start();

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_carnumber;
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                System.out.println("Aquire information complete!");
                showResult();
            }
        }
    };


    Runnable getArc = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }
        };


    public void showResult() {
        carstypelist = new ArrayList();
        ArrayList s = new ArrayList<String>();


        s.add("京Q33541");
        s.add("京Q54213");
        s.add("冀A66266");
        for(int i=0;i<s.size();i++){
            Map<String, Object> map = new HashMap<>();
            map.put("carNumberInfo",s.get(i));
            System.out.println(s.get(i));
            carstypelist.add(map);
        }
        listView = (ListView) findViewById(R.id.carnumberlist);
        adapter = new MySimpleAdapter(this, carstypelist, R.layout.list_carnumber,
                new String[]{"carNumberInfo"},
                new int[]{R.id.carnumberItemText});



        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {

                try {
                    Map<String, Object> temp = new HashMap<>();
                    temp = carstypelist.get(pos);
                    String selectedCarNumber = (String)temp.get("carNumberInfo");
                    carNumberEdit.setText(selectedCarNumber);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        });
    }
    public void nextStep(View view){

        SharedPreferences.Editor editor = login_user.edit();
        editor.putString("carNumber", carNumberEdit.getText().toString());
        editor.commit();
        System.out.println("You've choosed :" +  carNumberEdit.getText().toString());
//                    System.out.println(login_user.getString("arcType", null));
        Intent intent = new Intent();
        intent.putExtra("carNumber", carNumberEdit.getText().toString() + "");
        intent.setClass(CarNumberActivity.this, ScanActivity.class);
        startActivity(intent);

    }
}
