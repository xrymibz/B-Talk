package com.scandev;

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

public class CarsTypeActivity  extends ActionBarActivity {

    private ListView listView = null;
    MySimpleAdapter adapter = null;
    List<Map<String, Object>> carstypelist = null;
    private static final int COMPLETED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 给左上角图标的左边加上一个返回的图标
        setContentView(R.layout.activity_carstype);


        new Thread(getArc).start();

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
        s.add("4.2M(12)");
        s.add("5.2M(18)");
        s.add("6.8M(30)");
        s.add("Jinbei(2.7)");
        s.add("IVECO(5)");
        s.add("4.7M(28)");
        s.add("CHANGAN(4.7)");
        s.add("EVIKE(7)");
        for(int i=0;i<s.size();i++){
            Map<String, Object> map = new HashMap<>();
            map.put("carstypeInfo",s.get(i));
            System.out.println(s.get(i));
            carstypelist.add(map);
        }
        listView = (ListView) findViewById(R.id.carstypelist);
        adapter = new MySimpleAdapter(this, carstypelist, R.layout.list_carstype,
                new String[]{"carstypeInfo"},
                new int[]{R.id.carstypeItemText});



        listView.setAdapter(adapter);


    }
}
