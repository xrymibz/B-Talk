package com.scandev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.scandev.model.Urls;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MySimpleAdapter;
import com.scandev.utils.MyTrustManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by xietian on 2017/2/13.
 */

public class CarNumberActivity extends BaseTitleAcitvity {

    @Override
    protected int getContentView() {
        return R.layout.activity_carnumber;
    }
    SharedPreferences login_user;
    private static final String TAG = "CarNumberActivity";
    private final String title = "«Î ‰»Î≥µ≈∆∫≈";
    private final OkHttpClient client = new OkHttpClient();
    private String carType = "";
    private String arcType = "";
    private static String carrier = "";
    private TextView carnumberType = null;
    private ListView listView = null;
    private EditText carNumberEdit = null;
    List<Map<String, Object>> carNumberlist = null;
    private JSONObject res = null;
    private JSONArray carNumbers = null;
    MySimpleAdapter adapter = null;
    List<Map<String, Object>> carstypelist = null;
    private int laneId = 0;
    private static final int COMPLETED = 0;
    private static final int COMFAILED = -2;
    private static final int FAILED = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carNumberEdit = (EditText)findViewById(R.id.carnumberedit);
        carnumberType = (TextView)findViewById(R.id.carnumberType);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        carrier = login_user.getString("carrierAbbr", "");
        carType = login_user.getString("carType", "");
        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        Intent intent = getIntent();
        laneId = Integer.parseInt(intent.getStringExtra("laneId"));
        arcType = intent.getStringExtra("arcType");
        carnumberType.setText(carType);
        new Thread(getCarNumber).start();

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
    Runnable getCarNumber = new Runnable() {

        @Override
        public void run() {
            try{
                RequestBody formBody = new FormEncodingBuilder()
                        .add("carrierAbbr", carrier)
                        .add("carType",carType)
                        .build();
                Request request = new Request.Builder()
                        .url(Urls.URL_GETCARNUMBERBYCARRIER.url())
                        .header("User-Agent", "OkHttp Headers.java")
                        .addHeader("Accept", "application/json; q=0.5")
                        .addHeader("Accept", "application/vnd.github.v3+json")
                        .post(formBody)
                        .build();

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                client.setSslSocketFactory(sc.getSocketFactory());
                client.setHostnameVerifier(new MyHostnameVerifier());
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String string = response.body().string();
                                System.out.println("response.body().string() :" + string);
                                JSONObject res = new JSONObject(string);
                                //System.out.println(res.get("message"));
                                //showResultList(res);
                                boolean isSuccess = res.getBoolean("success");
                                if (isSuccess) {
                                    getData(res);
                                    Message msg = new Message();
                                    msg.what = COMPLETED;
                                    handler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    msg.what = COMFAILED;
                                    handler.sendMessage(msg);
                                }
                            } catch (JSONException e) {
                                Log.i(TAG, "response body can not change to jsonobject");
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.i(TAG, "fail to conn");
                        Message msg = new Message();
                        msg.what = FAILED;
                        handler.sendMessage(msg);
                    }
                });


            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        };


    public void showResult() {
        carstypelist = new ArrayList();
        listView = (ListView) findViewById(R.id.carnumberlist);
        adapter = new MySimpleAdapter(this, carNumberlist, R.layout.list_carnumber,
                new String[]{"carNumberInfo"},
                new int[]{R.id.carnumberItemText});



        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {

                try {
                    Map<String, Object> temp = new HashMap<>();
                    temp = carNumberlist.get(pos);
                    String selectedCarNumber = (String)temp.get("carNumberInfo");
                    carNumberEdit.setText(selectedCarNumber);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        });
    }
    private void getData(JSONObject res) {
        carNumberlist = new ArrayList<Map<String, Object>>();

        try {
            carNumbers = res.getJSONArray("data");
            for (int i = 0; i < carNumbers.length(); i++) {
                JSONObject carNumber = (JSONObject) (carNumbers.get(i));
                Map<String, Object> carNumberitem = new HashMap<String, Object>();

                carNumberitem.put("carNumberInfo", carNumber.get("carNumber"));
                carNumberlist.add(carNumberitem);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void nextStep(View view){

        SharedPreferences.Editor editor = login_user.edit();
        editor.putString("carNumber", carNumberEdit.getText().toString());
        editor.commit();
        System.out.println("You've choosed :" +  carNumberEdit.getText().toString());
//                    System.out.println(login_user.getString("arcType", null));

        Intent intent = new Intent();
        intent.putExtra("laneId", laneId + "");
        intent.putExtra("arcType", arcType + "");
        intent.putExtra("carNumber", carNumberEdit.getText().toString() + "");
        intent.setClass(CarNumberActivity.this, FunctionActivity.class);
        startActivity(intent);
    }

}
