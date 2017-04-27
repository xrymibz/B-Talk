package com.scandev;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.scandev.model.Constant;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MySimpleAdapter;
import com.scandev.utils.MyTrustManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ArcActivity extends BaseTitleAcitvity {

    private String title = "请选择Arc";
    private final OkHttpClient client = new OkHttpClient();
    private static final int COMPLETED = 0;
    private static final int COMFAILED = -2;
    private static final int FAILED = -1;
    private static final String TAG = "ArcActivity";
    private String scanType = "";
    private int laneId = 0;
    private int functionCode = 0;
    private JSONObject res = null;
    private JSONArray resArray = null;
    private ListView listView = null;
    MySimpleAdapter adapter = null;
    List<Map<String, Object>> arclist = null;
    private SharedPreferences login_user = null;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 给左上角图标的左边加上一个返回的图标
        //      setContentView(R.layout.activity_arc);

        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        setTitle(title);
        setRtTitle(login_user.getString("carrierName", ""));
        Intent intent = getIntent();
        laneId = Integer.parseInt(intent.getStringExtra("laneId"));
        scanType = login_user.getString("scanType", "");
        functionCode = intent.getIntExtra("functionCode", 0);//need check the code?

        System.out.println("Selected lane is " + laneId);
        System.out.println("Selected scanType is " + scanType);
        new Thread(getArc).start();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_arc;
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                System.out.println("Aquire information complete!");
                showResult(res);
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
                            getString(R.string.networkfail), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (Exception exe) {
                    exe.printStackTrace();
                }
            }
        }

    };
    Runnable getArc = new Runnable() {

        @Override
        public void run() {
            try {
                RequestBody formBody = new FormEncodingBuilder()
                        .add("laneId", laneId + "")
                        .add("scanType",scanType)
                        .build();
                Request request = new Request.Builder()
                        .url(Constant.URL_GETARC)
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
                                System.out.println("arcs:" + string);
                                JSONObject res = new JSONObject(string);
                                //System.out.println(res.get("message"));
                                //showResultList(res);
                                if (res.getBoolean("success")) {
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
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    public void showResult(JSONObject res) {
        listView = (ListView) findViewById(R.id.arclist);
        adapter = new MySimpleAdapter(this, arclist, R.layout.list_arc,
                new String[]{"arcInfo"},
                new int[]{R.id.arcItemText});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long arg3) {

                adapter.selectIndex = pos;
                adapter.notifyDataSetChanged();
                SharedPreferences.Editor editor = login_user.edit();
                try {
                    JSONObject arc = (JSONObject) resArray.get(pos);
                    int arcId = Integer.parseInt(arc.getString("arcId"));
                    String sourceFC = arc.getString("sourceFC");
                    String destinationFC = arc.getString("destinationFC");
                    String sortCode = arc.getString("sortCode");
                    String arcType = arc.getString("arcType");
                    editor.putString("sourceFC", sourceFC);
                    editor.putString("destinationFC", destinationFC);
                    editor.putString("sortCode", sortCode);
                    editor.putString("arcType", arcType);
                    editor.putInt("arcId", Integer.parseInt(arc.getString("arcId")));
                    editor.putString("arcName", sourceFC + "-" + destinationFC + "/" + arcType);
                    editor.commit();

                    Intent intent = new Intent();
                    intent.putExtra("laneId", laneId + "");
                    intent.putExtra("arcType", arcType + "");
                    intent.putExtra("arcIdentify", arcId + arcType);
//                    switch (functionCode) {
//                        case 0:
//                            if (arcType.equals("AITS")) {
////                                System.out.println("AITS page!");
//                                intent.setClass(ArcActivity.this, ScanOrManualAty.class);
//                            } else {
//                                intent.setClass(ArcActivity.this, ScanActivity.class);
//                            }
//                            break;
//                        case 1:
//                            intent.setClass(ArcActivity.this, ExceptionEditActivity.class);
//                            intent.putExtra("fluency", -1);
//                            break;
//                        case 2:
//                            intent.setClass(ArcActivity.this, ExceptionQueryActivity.class);
//                            break;
//                        default:
//                            break;
//                    }
                    intent.setClass(ArcActivity.this, FunctionActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getData(JSONObject res) {
        arclist = new ArrayList<>();
        try {
            resArray = res.getJSONArray("data");
            for (int i = 0; i < resArray.length(); i++) {
                try {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject obj = (JSONObject) resArray.get(i);
                    String sourceFC = (String) obj.get("sourceFC");
                    String destinationFC = (String) obj.get("destinationFC");
                    String sortCode = obj.getString("sortCode");
                    String arcType = obj.getString("arcType");
                    String arcCPT = obj.getString("arcCPT");
                    if (!(sortCode.equals(""))) {
                        map.put("arcInfo", sourceFC + "-" + destinationFC + "," + sortCode + "/" + arcType + "\n" + arcCPT);
                    } else {
                        map.put("arcInfo", sourceFC + "-" + destinationFC + "/" + arcType + "\n" + arcCPT);
                    }
                    arclist.add(map);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
