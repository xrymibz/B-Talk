package com.scandev;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.scandev.utils.MySimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xietian on 2017/2/13.
 */

public class CarNumberActivity extends BaseTitleAcitvity {

    private String title = "�����복�ƺ�";
    private ListView listView = null;
    SharedPreferences login_user;
    MySimpleAdapter adapter = null;
    List<Map<String, Object>> carstypelist = null;
    private static final int COMPLETED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// �����Ͻ�ͼ�����߼���һ�����ص�ͼ��
//        setContentView(R.layout.activity_carnumber);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));

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


        s.add("��Q33541");
        s.add("��Q54213");
        s.add("��A66266");
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


    }
}
