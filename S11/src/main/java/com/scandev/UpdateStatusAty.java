package com.scandev;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateStatusAty extends Activity {

    private List<String> statusList = new ArrayList<String>();
    private List<String> cancelList = new ArrayList<String>();
    private HashMap<String, Integer> statusMap;
    private ArrayAdapter<String> statusAdapter;
    private ArrayAdapter<String> cancelAdapter;

    private String LocalHost;
    private static final String TAG = "ASYNC_TASK";
    CurrentStatusTask task10;
    private UpdateTask task4;
    private EverStatusTask task7;
    private CheckStatusTask task8;
    private String params;
    private String vbaId;
    private String bol;
    //private String result;
    private String sessionid = "none";
    private String currentStatus;
    private String totalcount;
    private String weight;
    private String cube;
    int statuscode;
    private String reason = "no";

    private S11Application application;
    private Button finalsaveButton;
    private Button backButton;
    private Spinner statusSpinner;
    private Spinner cancelSpinner;
    private TextView weightView;
    private TextView cubeView;
    private TextView currentStatusView;
    private TextView totalcountView;
    private TableRow hiddenrow;
    private ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status_wait);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        params = bundle.getString("str");
        totalcount = bundle.getString("totalcount");
        weight = bundle.getString("weight");
        cube = bundle.getString("cube");


        String[] records1 = params.split(",");
        String[] records2 = records1[0].split("&");
        vbaId = records2[0];
        bol = records2[1];

        application = (S11Application) this.getApplication();
        SharedPreferences sp = getSharedPreferences("AITS_Cookie", MODE_PRIVATE);
        sessionid = sp.getString("cookie", "");

        LocalHost = this.getResources().getString(R.string.Localhost);
        task10 = new CurrentStatusTask();
        task10.execute(vbaId);
    }

    //判断过往状态中是否包含某一特定状态
    private boolean containStatus(String result, String statusCheck) {
        result = result.substring(1, result.length() - 1);
        result = result.replace("\"", "");
        String[] statusEver = result.split(",");
        if (statusEver != null && statusEver.length > 0) {
            for (int i = 0; i < statusEver.length; i++) {
                if (statusEver[i].equals(statusCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

    private OnClickListener mSendClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            params += reason + ',' + statuscode + ',';
            params += weightView.getText().toString() + ',' + cubeView.getText().toString() + ',';
            //日期待定,因为可能涉及没网情况的处理
            params += "time no";
            task8 = new CheckStatusTask();
            task8.execute();
        }
    };

    private OnClickListener backClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(UpdateStatusAty.this, ScanOrManualAty.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    };

    private class CurrentStatusTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "CurrentStatusTask doInBackground() called");
            InputStream inputStream = null;
            String result = "";
            HttpPost httppost = new HttpPost(LocalHost
                    + "asnOperationRecord/mobilelist");
            try {
                HttpClient httppostClienttest = CustomHttpClient.getHttpClient();
                List<NameValuePair> nameValuePairstest = new ArrayList<NameValuePair>(
                        1);
                nameValuePairstest.add(new BasicNameValuePair("id", arg0[0]));
                httppost.setEntity(new UrlEncodedFormEntity(
                        nameValuePairstest, HTTP.UTF_8));
                httppost.setHeader("Cookie", "JSESSIONID=" + sessionid);

                HttpResponse mHttpResponse = httppostClienttest.execute(httppost);

                // 获得响应的消息实体
                HttpEntity mHttpEntity = mHttpResponse.getEntity();
                // 获取一个输入流
                inputStream = mHttpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream));
                result = "";
                String line = "";
                while (null != (line = bufferedReader.readLine())) {
                    result += line;
                }

                httppost.abort();

            } catch (ConnectTimeoutException e1) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e1.printStackTrace();
            } catch (SocketTimeoutException e2) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e2.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "CurrentStatusTask onPostExecute() called");
            String data = "";
            int ListNum = 0;
            //取消工作线程
            if (task10 != null && task10.getStatus() == AsyncTask.Status.RUNNING) {
                task10 = null; // 如果Task还在运行，则先取消它
            }

            if (result.contains("signin") || result.equals("")) {
                Intent toLogin = new Intent();
                toLogin.putExtra("reLogin", true);
                toLogin.setClass(UpdateStatusAty.this, MainActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
            } else if (!result.equals("")) {
                try {
                    JSONObject dataJsonobj = new JSONObject(result);
                    data = dataJsonobj.getString("data");
                    if (!data.equals("[]")) {
                        data = data.substring(2, data.length() - 2);
                    }
                    ListNum = Integer.parseInt(dataJsonobj.getString("recordsFiltered"));
                } catch (JSONException e) {
                    System.out.println("mobilelist返回结果——JSON解析出错");
                    e.printStackTrace();
                }

                if (ListNum > 0) {
                    String strlist[] = data.split("\",\"|\\],\\[");
                    //获取当前状态
                    currentStatus = strlist[10];
                }
            }
            task7 = new EverStatusTask();
            task7.execute();
        }
    }

    private class EverStatusTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            //获得以前的状态
            Log.i(TAG, "EverStatusTask doInBackground() called");
            InputStream inputStream = null;
            String statusresult = "";
            try {
                HttpPost httppost = new HttpPost(LocalHost
                        + "asnOperationRecord/listEverRecord");
                HttpClient hc = CustomHttpClient.getHttpClient();
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
                pairs.add(new BasicNameValuePair("vbaId", vbaId));
                pairs.add(new BasicNameValuePair("bol", bol));
                httppost.setEntity(new UrlEncodedFormEntity(
                        pairs, HTTP.UTF_8));
                httppost.setHeader("Cookie", "JSESSIONID=" + sessionid);
                HttpResponse responsepost = hc.execute(httppost);
                HttpEntity mHttpEntityarea = responsepost.getEntity();
                inputStream = mHttpEntityarea.getContent();
                BufferedReader bufferedReadertest = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line = "";
                while (null != (line = bufferedReadertest.readLine())) {
                    statusresult += line;
                }
                System.out.println("状态列表：" + statusresult);
                httppost.abort();
            } catch (ConnectTimeoutException e1) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e1.printStackTrace();
            } catch (SocketTimeoutException e2) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e2.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return statusresult;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "EverStatusTask onPostExecute() called");
            //取消工作线程
            if (task7 != null && task7.getStatus() == AsyncTask.Status.RUNNING) {
                task7 = null; // 如果Task还在运行，则先取消它
            }

            if (result.contains("signin") || result.equals("")) {
                Intent toLogin = new Intent();
                toLogin.putExtra("reLogin", true);
                toLogin.setClass(UpdateStatusAty.this, MainActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
            } else {
                setContentView(R.layout.activity_update_status);

                hiddenrow = (TableRow) findViewById(R.id.hidden_part);
                currentStatusView = (TextView) findViewById(R.id.currentstatus);
                weightView = (TextView) findViewById(R.id.weight);
                cubeView = (TextView) findViewById(R.id.cube);
                totalcountView = (TextView) findViewById(R.id.totalcount);
                currentStatusView.setText(currentStatus);
                weightView.setText(weight);
                cubeView.setText(cube);
                totalcountView.setText(totalcount);

                finalsaveButton = (Button) findViewById(R.id.finalsave);
                finalsaveButton.setOnClickListener(mSendClickListener);

                backButton = (Button) findViewById(R.id.back);
                backButton.setOnClickListener(backClickListener);

                //构造状态选择下拉列表项
                statusMap = new HashMap<String, Integer>();
                statusMap.put("请选择", -1);//对应statusList首个字符串
                statusMap.put("已派车", 3);
                statusMap.put("已提货", 5);
                statusMap.put("第一次中转入库", 6);
                statusMap.put("第一次中转出库", 7);
                statusMap.put("第一次库外交接", 8);
                statusMap.put("第二次中转入库", 9);
                statusMap.put("第二次中转出库", 10);
                statusMap.put("第二次库外交接", 11);
                statusMap.put("到达目的库房", 12);
                statusMap.put("Canceled", 16);

                String[] items = new String[]{"已提货", "第一次中转入库", "第一次中转出库", "第一次库外交接",
                        "第二次中转入库", "第二次中转出库", "第二次库外交接", "到达目的库房", "Canceled"};
                System.out.println("currentStatus" + currentStatus);
                int n = statusMap.get(currentStatus);
                System.out.println("当前状态为：" + n);
                if (containStatus(result, "12")) {

                }
                //空字符串作为默认值
                statusList.add("请选择");
                if (n == 3) {
                    //当前为"已派车"，下一状态只有"已提货"
                    statusList.add(items[0]);
                } else if (n == 5) {
                    //已提货之后，可进行第一次中转入库or第一次库外交接or直接到达目的库房
                    statusList.add(items[1]);
                    statusList.add(items[3]);
                    statusList.add(items[7]);
                } else if (n == 6 || n == 9) {
                    //中转入库之后，只能中转出库
                    statusList.add(items[n - 4]);
                } else if (n == 7) {
                    //第一次中转出库之后，第二次中转or第一次库外交接or第二次库外交接or到达目的库房
                    statusList.add(items[4]);//第二次中转入库
                    if (!containStatus(result, "11")) {//如果没有进行第二次库外交接，
                        if (containStatus(result, "8")) {//则判断是否进行了第一次库外交接
                            statusList.add(items[6]);
                        } else {
                            statusList.add(items[3]);
                        }
                    }
                    statusList.add(items[7]);
                } else if (n == 8) {//第一次库外交接之后，第二次交接or第一次中转or第二次中转or到达目的库房
                    statusList.add(items[6]);
                    if (!containStatus(result, "9")) {
                        if (containStatus(result, "6")) {
                            statusList.add(items[4]);
                        } else {
                            statusList.add(items[1]);
                        }
                    }
                    statusList.add(items[7]);
                } else if (n == 10) {//第二次中转之后，第一次库外交接or第二次库外交接or到达目的库房
                    if (!containStatus(result, "11")) {
                        if (containStatus(result, "8")) {
                            statusList.add(items[6]);
                        } else {
                            statusList.add(items[3]);
                        }
                    }
                    statusList.add(items[7]);
                } else if (n == 11) {//第二次库外交接之后，第一次中转or第二次中转or到达目的库房
                    if (!containStatus(result, "9")) {
                        if (containStatus(result, "6")) {
                            statusList.add(items[4]);
                        } else {
                            statusList.add(items[1]);
                        }
                    }
                    statusList.add(items[7]);
                }

                if (n < 12) {//加入Canceled
                    statusList.add(items[8]);
                }


                statusSpinner = (Spinner) findViewById(R.id.newstatus);

                // 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
                statusAdapter = new ArrayAdapter<String>(UpdateStatusAty.this,
                        android.R.layout.simple_spinner_item, statusList);
                // 第三步：为适配器设置下拉列表下拉时的菜单样式。
                statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // 第四步：将适配器添加到下拉列表上
                statusSpinner.setAdapter(statusAdapter);
                // 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
                statusSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub

                        statuscode = statusMap.get(statusAdapter.getItem(arg2));
                                /* 将statusSpinner 显示 */
                        arg0.setVisibility(View.VISIBLE);

                        if (statuscode == -1) {
                            //未选择状态时按钮为disabled
                            finalsaveButton.setEnabled(false);
                        } else if (statuscode == 6 || statuscode == 7 ||
                                statuscode == 9 || statuscode == 10) {
                            //状态是“第一次/第二次中转出入”(6\7\9\10),不保存，提示
                            Toast t = new Toast(UpdateStatusAty.this);
                            t = Toast.makeText(getApplicationContext(),
                                    "该状态无法在手机端保存，请联系中转区", Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                            finalsaveButton.setEnabled(false);
                        } else {
                            finalsaveButton.setEnabled(true);
                        }

                        if (statuscode == 16) {
                            hiddenrow.setVisibility(View.VISIBLE);
                        } else {
                            hiddenrow.setVisibility(View.GONE);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                        arg0.setVisibility(View.VISIBLE);
                    }
                });
    			/* 下拉菜单弹出的内容选项触屏事件处理 */
                statusSpinner.setOnTouchListener(new Spinner.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        /**
                         *
                         */
                        return false;
                    }
                });

    			/*
    			 * 取消原因
    			 */
                cancelList.add("(1)到达后15分钟内找不到有效联系人");
                cancelList.add("(2)实际地址与计划安排地址不符");
                cancelList.add("(3)无发货标签，或发货标签无PO或者目的库信息");
                cancelList.add("(4)包装不符合运输要求，或包装破损");
                cancelList.add("(5)供应商未备货，或者等待备货时间超过30分钟");
                cancelList.add("(6)遇供应商休息，不发货");
                cancelList.add("(7)体积或重量超出预计安排，导致车辆无法装下");
                cancelList.add("(8)酒类货物无随附单");
                cancelList.add("(9)亚马逊要求取消提货");
                cancelList.add("(10)该PO货物之前已经完成提货");
                cancelList.add("(11)同一天出现多张一样的提货单，该单作废");
                cancelList.add("(12)车辆已满载，无法完成该单提货");
                cancelList.add("(13)时间来不及，无法在要求时间内到达供应商处提货");
                cancelList.add("(14)在计划要求日期内遗漏安排车辆进行提货");

                cancelSpinner = (Spinner) findViewById(R.id.reason);
                cancelAdapter = new ArrayAdapter<String>(UpdateStatusAty.this,
                        android.R.layout.simple_spinner_item, cancelList);
                // 第三步：为适配器设置下拉列表下拉时的菜单样式。
                //cancelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cancelAdapter.setDropDownViewResource(R.layout.dropdown_item);
                // 第四步：将适配器添加到下拉列表上
                cancelSpinner.setAdapter(cancelAdapter);
                // 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
                cancelSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        reason = cancelAdapter.getItem(arg2);
                        arg0.setVisibility(View.VISIBLE);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        //myTextView.setText("NONE");
                        arg0.setVisibility(View.VISIBLE);
                    }
                });
    			/* 下拉菜单弹出的内容选项触屏事件处理 */
                statusSpinner.setOnTouchListener(new Spinner.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        /**
                         *
                         */
                        return false;
                    }
                });
            }
        }
    }

    private class CheckStatusTask extends AsyncTask<String, Integer, String> {

        String statusSelected = "" + statuscode;

        protected void onPreExecute() {
            Log.i(TAG, "CheckStatusTask onPreExecute() called");
            dialog = ProgressDialog.show(UpdateStatusAty.this, "", "正在保存");
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "CheckStatusTask doInBackground() called");
            InputStream inputStream = null;
            String statusresult = "";
            try {
                HttpPost httppost = new HttpPost(LocalHost
                        + "asnOperationRecord/listEverRecord");
                HttpClient hc = CustomHttpClient.getHttpClient();
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
                pairs.add(new BasicNameValuePair("vbaId", vbaId));
                pairs.add(new BasicNameValuePair("bol", bol));
                httppost.setEntity(new UrlEncodedFormEntity(
                        pairs, HTTP.UTF_8));
                httppost.setHeader("Cookie", "JSESSIONID=" + sessionid);
                HttpResponse responsepost = hc.execute(httppost);
                HttpEntity mHttpEntityarea = responsepost.getEntity();
                inputStream = mHttpEntityarea.getContent();
                BufferedReader bufferedReadertest = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line = "";
                while (null != (line = bufferedReadertest.readLine())) {
                    statusresult += line;
                }
                System.out.println("状态列表：" + statusresult);
                httppost.abort();
            } catch (ConnectTimeoutException e1) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e1.printStackTrace();
            } catch (SocketTimeoutException e2) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e2.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return statusresult;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "CheckStatusTask onPostExecute() called");
            //关闭dialog
            if (dialog != null) {
                dialog.dismiss();
            }

            if (task8 != null && task8.getStatus() == AsyncTask.Status.RUNNING) {
                task8 = null; // 如果Task还在运行，则先取消它
            }

            if (result.contains("signin") || result.equals("")) {
                Intent toLogin = new Intent();
                toLogin.putExtra("reLogin", true);
                toLogin.setClass(UpdateStatusAty.this, MainActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
            } else {
                if (containStatus(result, statusSelected)) {
                    Toast t = new Toast(UpdateStatusAty.this);
                    t = Toast.makeText(getApplicationContext(),
                            "该状态已保存，请返回", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                    finalsaveButton.setEnabled(false);
                } else {
                    task4 = new UpdateTask();
                    task4.execute(params);
                }
            }
        }
    }

    private class UpdateTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "UpdateTask doInBackground() called");
            InputStream inputStream = null;
            String result = "";
            try {
                //添加状态
                HttpClient hc = CustomHttpClient.getHttpClient();
                HttpPost httppost = new HttpPost(LocalHost
                        + "asnOperationRecord/mobileadd");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("params", params));
                httppost.setEntity(new UrlEncodedFormEntity(
                        nameValuePairs, HTTP.UTF_8));
                httppost.setHeader("Cookie", "JSESSIONID=" + sessionid);
                HttpResponse mHttpResponse = hc.execute(httppost);
                int statustest = mHttpResponse.getStatusLine().getStatusCode();
                System.out.println("status:" + statustest);
                // 获得响应的消息实体
                HttpEntity mHttpEntity = mHttpResponse.getEntity();
                // 获取一个输入流
                inputStream = mHttpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream));
                result = "";
                String line = "";
                while (null != (line = bufferedReader.readLine())) {
                    result += line;
                }
                //application.checkSession(result);
                httppost.abort();
            } catch (ConnectTimeoutException e1) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e1.printStackTrace();
            } catch (SocketTimeoutException e2) {
                Toast t = new Toast(UpdateStatusAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e2.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "UpdateTask onPostExecute() called");
            //取消工作线程
            if (task4 != null && task4.getStatus() == AsyncTask.Status.RUNNING) {
                task4 = null; // 如果Task还在运行，则先取消它
            }
            if (result.contains("signin")) {
                Intent toLogin = new Intent();
                toLogin.putExtra("reLogin", true);
                toLogin.setClass(UpdateStatusAty.this, MainActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
            } else if (result.equals("save succeed")) {

                Toast toast = new Toast(UpdateStatusAty.this);
                toast = Toast.makeText(getApplicationContext(),
                        "保存成功", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finalsaveButton.setEnabled(false);
                backButton.performClick();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //屏蔽返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
