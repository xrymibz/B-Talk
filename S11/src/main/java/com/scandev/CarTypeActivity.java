package com.scandev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class CarTypeActivity  extends BaseTitleAcitvity {

    @Override
    protected int getContentView() {return R.layout.activity_cartype;}
    SharedPreferences login_user;
    private static final String TAG = "CarTypeActivity";
    private final String title = "«Î—°‘Ò≥µ–Õ";
    private static String carrier = "";
    private String arcType = "";
    private final OkHttpClient client = new OkHttpClient();
    private static final int COMPLETED = 0;
    private static final int COMFAILED = -2;
    private static final int FAILED = -1;
    private int laneId = 0;
    private ListView listView = null;
    MySimpleAdapter adapter = null;
    private JSONObject res = null;
    private JSONArray cartypes = null;
    List<Map<String, Object>> cartypelist = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        carrier = login_user.getString("carrierAbbr", "");
        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        Intent intent = getIntent();
        laneId = Integer.parseInt(intent.getStringExtra("laneId"));
        arcType = intent.getStringExtra("arcType");
        try {
            new Thread(getCarType).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                showResultList(res);
            } else if (msg.what == COMFAILED) {
                try {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            res.getString("message"), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (Exception exe) {
                    exe.printStackTrace();
                }
            } else if (msg.what == FAILED) {
                try {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.networkfail, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (Exception exe) {
                    exe.printStackTrace();
                }
            }
        }
    };

    Runnable getCarType = new Runnable() {

        @Override
        public void run() {
            try{
                RequestBody formBody = new FormEncodingBuilder()
                        .add("carrierAbbr", carrier)
                        .build();
                Request request = new Request.Builder()
                        .url(Urls.URL_GETCARTYPEBYCARRIER.url())
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

    protected void showResultList(JSONObject res) {

        listView = (ListView) findViewById(R.id.cartypelist);
        adapter = new MySimpleAdapter(this, cartypelist, R.layout.list_cartype,
                new String[]{"carstypeInfo"},
                new int[]{R.id.cartypeItemText});



        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                JSONObject cartype;
                try {
                    adapter.selectIndex = pos;
                    adapter.notifyDataSetChanged();
                    cartype = (JSONObject) (cartypes.get(pos));
                    SharedPreferences.Editor editor = login_user.edit();
                    String carType = (String) cartype.get("cartype");
                    editor.putString("carType", carType);
                    editor.commit();
                    System.out.println("You've choosed :" + cartype.get("cartype") +"   ");
//                    System.out.println(login_user.getString("arcType", null));
                    Intent intent = new Intent();
                    intent.putExtra("carType", carType + "");
                    intent.putExtra("laneId", laneId + "");
                    intent.putExtra("arcType", arcType + "");
                    intent.setClass(CarTypeActivity.this, CarNumberActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        });
    }

    private void getData(JSONObject res) {
        cartypelist = new ArrayList<Map<String, Object>>();
        try {
            cartypes = res.getJSONArray("data");
            for (int i = 0; i < cartypes.length(); i++) {
                JSONObject cartype = (JSONObject) (cartypes.get(i));
                Map<String, Object> cartypeitem = new HashMap<String, Object>();

                cartypeitem.put("carstypeInfo", cartype.get("cartype"));
                cartypelist.add(cartypeitem);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
