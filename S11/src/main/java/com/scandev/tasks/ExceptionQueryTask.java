package com.scandev.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.scandev.ExceptionQueryActivity;
import com.scandev.R;
import com.scandev.View.ExceptionAdapter;
import com.scandev.model.Constant;
import com.scandev.model.Urls;
import com.scandev.utils.Md5Util;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MyTrustManager;
import com.scandev.utils.ZipUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;
import org.json.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;


public class ExceptionQueryTask extends AsyncTask<Map<String, String>, Integer, List<String[]>> {

    private final OkHttpClient client = new OkHttpClient();
    private final static String TAG = "GetScanHistoryTask";
    private ExceptionQueryActivity activity;

    private JSONArray resultArray = new JSONArray();

    public ExceptionQueryTask(ExceptionQueryActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.progressDialog = ProgressDialog.show(activity, activity.getString(R.string.pleasewait),
                activity.getString(R.string.downloading), true);
    }

    @Override
    protected List<String[]> doInBackground(Map<String, String>... params) {
        List<String[]> list = new ArrayList<>();

        String laneE = params[0].get("laneE");
        String sourceFC = params[0].get("sourceFC");
        String destinationFC = params[0].get("destinationFC");
        String date1 = params[0].get("startTime");
        String date2 = params[0].get("endTime");
        String cargoType = params[0].get("arctype");
        String sortCode = "";
        if (params[0].get("sortcode") != null) {
            sortCode = params[0].get("sortcode");
        }

        String sign = Md5Util.getMD5Str(Constant.app_secret_true + Constant.app_key_true +
                Constant.METHOD_EXCEPTION_HISTORY).toUpperCase();

  //      Log.d("date",sign+" "+sourceFC+"  "+destinationFC+"  "+cargoType+"  "+sortCode.length()+"  ");
        RequestBody formBody = new FormEncodingBuilder().add("sign", sign)
                .add("laneE", laneE)
                .add("source", sourceFC)
                .add("destination", destinationFC)
                .add("cargoType", cargoType)
                .add("sortCode", sortCode)
                .add("fromTime", date1)
                .add("toTime", date2)
                .build();

        Request request = new Request.Builder()
                .url(Urls.URL_EXCEPTION_HISTORY.url())
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
                System.out.println(response.body().toString()+"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
                String data = jsonResult.getString("data");
                data = ZipUtil.uncompress(data);
                System.out.println(data+"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
                if (data.length() > 0) {
                    resultArray = new JSONArray(data);
                }

                for (int i = 0; i < resultArray.length(); i++) {
                    JSONArray array = (JSONArray) resultArray.get(i);
                    String[] exceptionMsg = new String[5];
                    for (int j = 0; j < array.length(); ++j) {
                        exceptionMsg[j] = (String) array.get(j);
                    }

                    /*ExceptionItem ei = new ExceptionItem(resultArray.getJSONObject(i));*/
                    list.add(exceptionMsg);
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
    protected void onPostExecute(List<String[]> strings) {
        super.onPostExecute(strings);

        activity.progressDialog.dismiss();
        if (strings != null) {
            Toast.makeText(activity, "获取到" + strings.size() + "条异常", Toast.LENGTH_SHORT).show();
            ExceptionAdapter adapter = new ExceptionAdapter(activity, strings);
            activity.exceptionArray = strings;
            activity.listView.setAdapter(adapter);
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
}
