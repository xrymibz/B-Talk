package com.scandev.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.scandev.CustomHttpClient;
import com.scandev.HomeActivity;
import com.scandev.MainActivity;
import com.scandev.R;
import com.scandev.S11Application;
import com.scandev.model.Constant;
import com.scandev.model.LoginResult;
import com.scandev.model.ServerAddr;
import com.scandev.model.Urls;
import com.scandev.utils.Md5Util;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MyTrustManager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class LoginTask extends AsyncTask<String, Integer, LoginResult> {
    private static String TAG = "LoginTask";
    private MainActivity activity;
    private OkHttpClient client = new OkHttpClient();

    private String carrierName;
    private String userName;
    private String passWord;

    // AITS
   // private String LocalHost = "http://127.0.0.1:8080/";
     private String LocalHost = "https://aits.cn-north-1.eb.amazonaws.com.cn/";
    private HttpResponse responsepost;
    private InputStream inputStream;
    private String cookie = "none";
//    private S11Application application;
    // end of AITS

    public LoginTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.progressDialog = ProgressDialog.show(activity, activity.getString(R.string.pleasewait),
                activity.getString(R.string.logining), true);
    }

    @Override
    protected LoginResult doInBackground(String... params) {

        carrierName = params[0];
        userName = params[1];
        passWord = params[2];

        JSONObject returnMsg = null;
        String aits = "";

        try {
            // 1. Linker login
            String sign = Md5Util.getMD5Str(Constant.app_secret_true + Constant.app_key_true +
                    Constant.METHOD_LOGIN).toUpperCase();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("sign", sign)
                    .add("carrierName", carrierName)
                    .add("username", userName)
                    .add("password", passWord)
                    .build();

            System.out.println(Urls.URL_LOGIN.url());

            Request request = new Request.Builder()
                    .url(Urls.URL_LOGIN.url())
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Accept", "application/json; q=0.5")
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .post(formBody)
                    .build();

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new MyHostnameVerifier());      //客户端的ssl加密

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                returnMsg = new JSONObject(response.body().string());  //接收到的响应报文
            }

            // 2. AITS login TODO
            HttpPost httppost = new HttpPost(LocalHost
                    + "AITS/j_spring_security_check");
            HttpClient httppostClient = CustomHttpClient.getHttpClient();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", userName));
            nameValuePairs.add(new BasicNameValuePair("password", passWord));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            responsepost = httppostClient.execute(httppost);
            int status = responsepost.getStatusLine().getStatusCode();
            System.out.println("status："+status);

            HttpEntity mHttpEntity = responsepost.getEntity();
            inputStream = mHttpEntity.getContent();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line = "";

            while (null != (line = bufferedReader.readLine())) {
                aits += line;
            }

            List<Cookie> cookies = ((AbstractHttpClient) httppostClient)
                    .getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("#Cookie为空");
            } else {
                System.out.println("#cookie size：" + cookies.size());
                for (int i = 0; i < cookies.size(); i++) {
                    String[] cookie1 = cookies.get(i).toString().split(":");
                    String[] cookie2 = cookie1[3].split("]");
                    cookie = cookie2[0];
                    System.out.println("登录后的session id是：" + cookie);
                }
                // record cookie
                SharedPreferences sp = activity.getSharedPreferences("AITS_Cookie", Activity.MODE_PRIVATE);  //SharedPreferences 共享的数据存储
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("cookie", cookie);
                editor.commit();
            }
            httppost.abort();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return new LoginResult(returnMsg, aits);
        }
    }

    @Override
    protected void onPostExecute(LoginResult loginResult) {
        activity.progressDialog.dismiss();
        try {
            // 1. AITS login success TODO
            // 保存账号密码？

            // 2. Linker login success
            JSONObject res = loginResult.getLinker();
            if (res == null) {
                Toast toast = Toast.makeText(activity,
                        R.string.networkfail, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            System.out.println("res :"+res.toString());
            boolean isSuccess = res.getBoolean("success");
            if (isSuccess == true) {
                System.out.println("#success");
                SharedPreferences login_user = activity.getSharedPreferences("login_user", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = login_user.edit();
                JSONArray arr = res.getJSONArray("data");
                int userId = Integer.parseInt(arr.get(0).toString());
                String carrierAbbr = arr.get(1).toString();
                String isInjection = arr.get(2).toString();

                System.out.println("userId:" + userId  +"carrierAbbr :"+carrierAbbr);

                editor.putString("carrierAbbr", carrierAbbr);
                editor.putString("username", userName);
                editor.putString("pwd", passWord);
                editor.putInt("userId", userId);
                editor.putString("isInjection", isInjection);
                editor.putString("carrierName", carrierName);
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(activity, HomeActivity.class);
                activity.startActivity(intent);
            } else {
                String message = res.getString("message");
                try {
                    Toast toast = Toast.makeText(activity,
                            message, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "exception", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        activity.progressDialog.dismiss();
    }
}
