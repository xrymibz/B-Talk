package com.scandev.tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.scandev.HistoryActivity;
import com.scandev.R;
import com.scandev.model.Constant;
import com.scandev.model.Urls;
import com.scandev.utils.Md5Util;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MyTrustManager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class GetScanHistoryTask extends AsyncTask<Map<String, String>, Integer, List<Map<String, String>>> {
    private final OkHttpClient client = new OkHttpClient();
    private final static String TAG = "GetScanHistoryTask";
    private HistoryActivity activity;
    private String laneE;
    private String date;

    private ProgressDialog progressDialog;
    private int totalnum;

    public GetScanHistoryTask(HistoryActivity activity) {
        this.activity = activity;
        totalnum = 0;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(activity.getString(R.string.pleasewait));
        progressDialog.setMessage(activity.getString(R.string.downloading));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                GetScanHistoryTask.this.cancel(true);
            }
        });
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected List<Map<String, String>> doInBackground(Map<String, String>... params) {
        List<Map<String, String>> list = null;
        laneE = params[0].get("laneE");
        date = params[0].get("date");

        String sign = Md5Util.getMD5Str(Constant.app_secret_true + Constant.app_key_true +
                Constant.METHOD_HISTORY).toUpperCase();

        RequestBody formBody = new FormEncodingBuilder().add("sign", sign)
                .add("laneE", laneE)
                .add("date", date)
                .add("userIdStr", params[0].get("userId"))
                .build();

            Log.d("du it :",laneE + "   " + date + " " + params[0].get("userId"));
        Request request = new Request.Builder()
                .url(Urls.URL_HISTORY.url())
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .post(formBody)
                .build();

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new MyHostnameVerifier());

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject jsonResult = new JSONObject(response.body().string());
                System.out.println(response.body().toString());
                JSONArray resultArray = jsonResult.getJSONArray("data");

                list = new ArrayList<>();
                for (int i = 0; i < resultArray.length(); i++) {
                    Map<String, String> map = new HashMap<>();
                    JSONArray array = (JSONArray) resultArray.get(i);
                    String time = array.getString(4).substring(0, 5);
                    String arc = array.getString(1) + "-" + array.getString(2);
                    String cargoType = array.getString(3);
                    int total = array.getInt(5);
                    totalnum += total;
                    map.put("historyinfo", time + "\t\t\t\t\t" + laneE + "\t\t\t\t\t" + arc + "\n" + cargoType + "\t\t\t\t\t共" + total + "条");
                    list.add(map);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "come some error");
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    @Override
    protected void onPostExecute(List<Map<String, String>> array) {
        progressDialog.dismiss();
        if (array != null) {
            SimpleAdapter adapter = new SimpleAdapter(activity, array, R.layout.list_history,
                    new String[]{"historyinfo"},
                    new int[]{R.id.historyText});
            activity.historylist.setAdapter(adapter);
            activity.totalText.setText(date + "共上传" + totalnum + "条数据");
        } else {
            try {
                Toast toast = Toast.makeText(activity,
                        R.string.downloadfail, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
