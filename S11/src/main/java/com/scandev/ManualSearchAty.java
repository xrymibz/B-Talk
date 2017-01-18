package com.scandev;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.Spinner;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManualSearchAty extends Activity {
    private String LocalHost;
    private S11Application application; // 获得自定义的应用程序MyApp
    private static final String TAG = "ASYNC_TASK";
    private VBA_IdTask task3;
    private LocationTask task6;
    private String cookieid = "none";
    private InputStream inputStream = null;
    private InputStream inputStreamarea = null;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String parameters = "";
    //private String result="";
    private String location = "ALL";
    private String pickdate;
    private String month;
    private String day;

    private Spinner mySpinner;
    private TextView editCarrierCode;
    private TextView editTruckNum;
    private TextView editVendorCode;
    private TextView editBol;
    private TextView editPo;
    private DatePicker datePicker;
    private int status = 0;
    Toast toast;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search_wait);

        application = (S11Application) this.getApplication();
        SharedPreferences sp = getSharedPreferences("AITS_Cookie", MODE_PRIVATE);
        cookieid = sp.getString("cookie", "");

        LocalHost = this.getResources().getString(R.string.Localhost);
        //request location
        task6 = new LocationTask();
        task6.execute();
    }

    private OnClickListener mSendClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String carriercode = editCarrierCode.getText().toString();
            String trucknum = editTruckNum.getText().toString();
            String vendorcode = editVendorCode.getText().toString();
            String bol = editBol.getText().toString();
            String po = editPo.getText().toString();

            if (location != "ALL") {
                parameters += "area='" + location + "',";
            }
            if (carriercode.length() > 0) {
                parameters = "carr.code='" + carriercode + "',";
            }
            if (trucknum.length() > 0) {
                parameters += "truck_number='" + trucknum + "',";
            }
            if (vendorcode.length() > 0) {
                parameters = "a.vendor_code='" + vendorcode + "',";
            }
            if (bol.length() > 0) {
                parameters += "del.bol='" + bol + "',";
            }
            if (po.length() > 0) {
                parameters += "po='" + po + "',";
            }

            datePicker.clearFocus();
            int monthlen = String.valueOf(datePicker.getMonth() + 1).length();
            int daylen = String.valueOf(datePicker.getDayOfMonth()).length();
            int year = datePicker.getYear();
            if (monthlen < 2) {
                month = "0" + String.valueOf(datePicker.getMonth() + 1);
            } else {
                month = String.valueOf(datePicker.getMonth() + 1);
            }
            if (daylen < 2) {
                day = "0" + String.valueOf(datePicker.getDayOfMonth());
            } else {
                day = String.valueOf(datePicker.getDayOfMonth());
            }
            pickdate = " pickup_time='" + year + "-" + month + "-" + day + "'";
            parameters += pickdate;
            //System.out.println("parameters："+parameters);
            task3 = new VBA_IdTask();
            task3.execute(parameters);
        }

    };

    private class LocationTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "ManualSearch request locations doInBackground() called");

            String locations = "";
            try {
                HttpPost httppostarea = new HttpPost(LocalHost
                        + "asn/dispatch_location");
                HttpClient httppostClienarea = CustomHttpClient.getHttpClient();
                httppostarea.setHeader("Cookie", "JSESSIONID=" + cookieid);

                HttpResponse responsepostarea = httppostClienarea
                        .execute(httppostarea);
                // 获得响应的消息实体
                HttpEntity mHttpEntityarea = responsepostarea.getEntity();
                // 获取一个输入流
                inputStreamarea = mHttpEntityarea.getContent();
                BufferedReader bufferedReadertest = new BufferedReader(
                        new InputStreamReader(inputStreamarea));
                String lineArea = "";
                while (null != (lineArea = bufferedReadertest.readLine())) {
                    locations += lineArea;
                }
                // 将结果打印出来，可以在LogCat查看
                //System.out.println("提货城市列表："+locations);
                httppostarea.abort();

            } catch (ConnectTimeoutException e1) {
                Toast t = new Toast(ManualSearchAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e1.printStackTrace();
            } catch (SocketTimeoutException e2) {
                Toast t = new Toast(ManualSearchAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e2.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStreamarea != null) {
                    try {
                        inputStreamarea.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return locations;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "ManualSearch request locations onPostExecute() called");
            //取消工作线程
            if (task6 != null && task6.getStatus() == AsyncTask.Status.RUNNING) {
                task6 = null; // 如果Task还在运行，则先取消它
            }
            if (result.contains("signin") || result.equals("")) {
                Intent toLogin = new Intent();
                toLogin.putExtra("reLogin", true);
                toLogin.setClass(ManualSearchAty.this, MainActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
            } else {
                result = result.substring(1, result.length() - 1);
                String[] locationList = result.split(",");
                list.add("ALL");
                if (locationList.length > 2) {
                    for (int i = 1; i < locationList.length; i++) {
                        list.add(locationList[i].split(":")[1].substring(1, locationList[i].split(":")[1].length() - 2));
                        i++;
                    }
                }

                setContentView(R.layout.activity_manual_search);

                editCarrierCode = (TextView) findViewById(R.id.carriercode);
                editTruckNum = (TextView) findViewById(R.id.trucknum);
                editVendorCode = (TextView) findViewById(R.id.vendorcode);
                editBol = (TextView) findViewById(R.id.bol);
                editPo = (TextView) findViewById(R.id.po);
                Button msearchButton = (Button) findViewById(R.id.manualsearch);
                msearchButton.setOnClickListener(mSendClickListener);
                datePicker = (DatePicker) findViewById(R.id.datePicker);

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int monthOfYear = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int monthlen = String.valueOf(monthOfYear + 1).length();
                int daylen = String.valueOf(dayOfMonth).length();

                if (monthlen < 2) {
                    month = "0" + String.valueOf(monthOfYear + 1);
                } else {
                    month = String.valueOf(monthOfYear + 1);
                }
                if (daylen < 2) {
                    day = "0" + String.valueOf(dayOfMonth);
                } else {
                    day = String.valueOf(dayOfMonth);
                }
                pickdate = " pickup_time='" + year + "-" + month + "-" + day + "'";

                datePicker.init(year, monthOfYear, dayOfMonth,
                        new OnDateChangedListener() {

                            public void onDateChanged(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

        						/*int monthlen = String.valueOf(monthOfYear+1).length();
                                int daylen = String.valueOf(dayOfMonth).length();
        						if(monthlen<2){
        							month = "0"+String.valueOf(monthOfYear+1);
        						}else{
        							month = String.valueOf(monthOfYear+1);
        						}
        						if(daylen<2){
        							day="0"+String.valueOf(dayOfMonth);
        						}else{
        							day= String.valueOf(dayOfMonth);
        						}
        						pickdate =" pickup_time='"+year+"-"+month+"-"+day+"'";*/
                            }
                        });

                mySpinner = (Spinner) findViewById(R.id.location);
                // 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
                adapter = new ArrayAdapter<String>(ManualSearchAty.this, android.R.layout.simple_spinner_item, list);
                // 第三步：为适配器设置下拉列表下拉时的菜单样式。
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // 第四步：将适配器添加到下拉列表上
                mySpinner.setAdapter(adapter);
                // 第五步：为下拉列表设置各种事件的响应，这个是响应菜单被选中
                mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        arg0.setVisibility(View.VISIBLE);
                        location = adapter.getItem(arg2);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        arg0.setVisibility(View.VISIBLE);
                        location = "ALL";
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
        }
    }

    private class VBA_IdTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "ManualSearch request handoverlist onPreExecute() called");
            dialog = ProgressDialog.show(ManualSearchAty.this, "", "正在查询");
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "ManualSearch request handoverlist doInBackground() called");
            String result = "";
            try {
                HttpPost httppost = new HttpPost(LocalHost
                        + "asnOperationRecord/handoverlist");

                HttpClient httppostClient = CustomHttpClient.getHttpClient();
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("parms", arg0[0]));
                httppost.setEntity(new UrlEncodedFormEntity(
                        nameValuePairs, HTTP.UTF_8));
                httppost.setHeader("Cookie", "JSESSIONID=" + cookieid);
                HttpResponse responsepost = httppostClient.execute(httppost);
                status = responsepost.getStatusLine().getStatusCode();
                System.out.println("status：" + status);

                HttpEntity mHttpEntitytest = responsepost.getEntity();
                inputStream = mHttpEntitytest.getContent();
                BufferedReader bufferedReadertest = new BufferedReader(
                        new InputStreamReader(inputStream));
                result = "";
                String line = "";
                while (null != (line = bufferedReadertest.readLine())) {
                    result += line;
                }
                // 将结果打印出来，可以在LogCat查看
                //System.out.println("查询handoverlist返回的如下：");
                //System.out.println(result);
                httppost.abort();
            } catch (ConnectTimeoutException e1) {
                Toast t = new Toast(ManualSearchAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "您当前的网络状况不佳，请稍后重试", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e1.printStackTrace();
            } catch (SocketTimeoutException e2) {
                Toast t = new Toast(ManualSearchAty.this);
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
            Log.i(TAG, "ManualSearch request handoverlist onPostExecute() called");
            //关闭dialog
            dialog.dismiss();
            //取消工作线程
            if (task3 != null && task3.getStatus() == AsyncTask.Status.RUNNING) {
                task3 = null; // 如果Task还在运行，则先取消它
            }
            if (result.contains("signin")) {

                System.out.println("进入signin");
                Intent toLogin = new Intent();
                toLogin.putExtra("reLogin", true);
                toLogin.setClass(ManualSearchAty.this, MainActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
            } else if ((status == 200) && (!result.equals("[]"))) {

                //清空参数
                parameters = "";
                Intent intent = new Intent();
                intent.setClass(ManualSearchAty.this, ChooseVBA_IdAty.class);
                intent.putExtra("str", result);
                //清空查询结果
                result = "";
                startActivity(intent);
            } else {

                toast = new Toast(ManualSearchAty.this);
                toast = Toast.makeText(getApplicationContext(),
                        "无查询结果", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                parameters = "";
                result = "";
            }
        }
    }

}
