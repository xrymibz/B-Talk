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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

    private static TextView TitleName = null;
    private static TextView CarrierName = null;
    private static Button ComeBack = null;
    private static final String TAG = "LanesActivity";
    private final OkHttpClient client = new OkHttpClient();
    private static String carrier = "";
    private static String isInjection = "";
    private JSONObject res = null;
    private JSONArray lanes = null;
    private ListView listView = null;
    MySimpleAdapter adapter = null;
    SharedPreferences login_user;
    private static final int COMPLETED = 0;
    private static final int COMFAILED = -2;
    private static final int FAILED = -1;
    private List<Map<String, Object>> lanelist = null;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_home);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title);
        TitleName = (TextView) findViewById(R.id.titlename);
        CarrierName = (TextView) findViewById(R.id.carriername);
        ComeBack = (Button)findViewById(R.id.comeback);
        TitleName.setText("请选择路线 ");
        CarrierName.setText("上海展欣");




  //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        String username = login_user.getString("username", "");
        carrier = login_user.getString("carrierAbbr", "");
        isInjection = login_user.getString("isInjection", "");
        try {
            new Thread(getLanes).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void showResultList(JSONObject res) {

        listView = (ListView) findViewById(R.id.lanelist);
        adapter = new MySimpleAdapter(this, lanelist, R.layout.list_lane,
                new String[]{"laneName"},
                new int[]{R.id.laneItemText});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                JSONObject lane;
                try {
                    adapter.selectIndex = pos;
                    adapter.notifyDataSetChanged();
                    lane = (JSONObject) (lanes.get(pos));
                    SharedPreferences.Editor editor = login_user.edit();
                    String laneName = (String) lane.get("laneName");
                    String laneE = (String) lane.get("laneE");
                    String arcType = (String) lane.get("arcType");
                    editor.putString("laneName", laneName);
                    editor.putString("laneE", laneE);
                    editor.putString("arcType", arcType);
                    editor.commit();
                    int laneId = (Integer) lane.get("laneId");
                    System.out.println("You've choosed" + laneId + "," + lane.get("laneName") +"   ");
//                    System.out.println(login_user.getString("arcType", null));
                    Intent intent = new Intent();
                    intent.putExtra("laneId", laneId + "");
                    intent.putExtra("arcType", arcType + "");
                    intent.setClass(HomeActivity.this, FunctionActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        });
    }

    private void getData(JSONObject res) {
        lanelist = new ArrayList<Map<String, Object>>();

        try {
            lanes = res.getJSONArray("data");
            for (int i = 0; i < lanes.length(); i++) {
                JSONObject lane = (JSONObject) (lanes.get(i));
                Map<String, Object> laneitem = new HashMap<String, Object>();

                laneitem.put("laneName", lane.get("laneName"));
                laneitem.put("laneId", lane.get("laneId"));
                lanelist.add(laneitem);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Runnable getLanes = new Runnable() {

        @Override
        public void run() {
            try {
                RequestBody formBody = new FormEncodingBuilder()
                        .add("carrierId", carrier)
                        .add("isInjection",isInjection)
                        .build();
                System.out.println("isInjection zzz   :   "+isInjection);
                Request request = new Request.Builder()
                        .url(Urls.URL_GETLANESByCARRIER.url())
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME))
                && event.getRepeatCount() == 0) {
            dialog_exit(this);
        }
        return false;
    }

    public static void dialog_exit(Context context) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setMessage(R.string.suretoexit);
        builder.setTitle(R.string.notify);

        builder.setPositiveButton(R.string.positive, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());

            }

        });
        builder.setNegativeButton(R.string.negative, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }


        });
        builder.create().show();
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

    public void comeback(View v) {
        finish();
    }
}
