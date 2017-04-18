package com.scandev.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.scandev.R;
import com.scandev.ScanActivity;
import com.scandev.model.Constant;
import com.scandev.model.ServerAddr;
import com.scandev.model.Urls;
import com.scandev.utils.Md5Util;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MyTrustManager;
import com.squareup.okhttp.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class GetCheckListTsk extends AsyncTask<Map<String, String>, Integer, Integer> {
    private static final String TAG = "GetCheckListTask";
    private final OkHttpClient client = new OkHttpClient();
    private ScanActivity activity;
    private Toast toast;
    private boolean hasToBeScanedIdList = false;

    public GetCheckListTsk(ScanActivity context) {
        this.activity = context;
    }

    @Override
    protected void onPreExecute() {
        activity.progressDialog = ProgressDialog.show(activity, activity.getString(R.string.pleasewait),
                activity.getString(R.string.downloading), true);
    }

    @Override
    protected Integer doInBackground(Map<String, String>... params) {

        String sourceFC = params[0].get("sourceFC");
        String destinationFC = params[0].get("destinationFC");
        String arcType = params[0].get("arctype");
        String laneE = params[0].get("laneE");
        String time = Constant.formateDate(new Date());
 /*       String time = "2016-06-27 19:31:00";
        String sourceFC = "SHA2";/
        String destinationFC = "SHE1";
        String arcType = "Transfer";
        String laneE = "TSN2-PEK3-SHE1";*/

        String sign = Md5Util.getMD5Str(Constant.app_secret_true + Constant.app_key_true +
                Constant.METHOD_GETCOMPARELIST).toUpperCase();

        RequestBody formBody = new FormEncodingBuilder()
                .add("sign", sign)
                .add("sourceFc", sourceFC)
                .add("destinationFc", destinationFC)
                .add("arcType", arcType)
                .add("laneE", laneE)
                .add("time", time)
                .build();
        Request request = new Request.Builder()
                .url(Urls.URL_GETCHECKLIST.url())
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .post(formBody)
                .build();

        int returnValue = -1;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new MyHostnameVerifier());

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println(response.code());

                String string = response.body().string();
                System.out.println(string);

                JSONObject jsonResult = new JSONObject(string);

                hasToBeScanedIdList = jsonResult.getJSONObject("data").getBoolean("hasToBeScanedIdList");

                JSONArray checkedList = jsonResult.getJSONObject("data").getJSONArray("scanedIdSet");
                for (int i = 0; i < checkedList.length(); i++) {
                    activity.checkedList.add(checkedList.getString(i));
                }

                JSONArray notCheckedList = jsonResult.getJSONObject("data").getJSONArray("toBeScanedIdSet");
                for (int i = 0; i < notCheckedList.length(); i++) {
                    activity.notCheckedList.add(notCheckedList.getString(i));
                }

                System.out.println(jsonResult.getString("message"));
                returnValue = Constant.COMPLETED;
            } else {
                System.out.println(response.code());
                String string = response.body().string();
                System.out.println(string);
                returnValue = Constant.FAILED;
            }
        } catch (Exception e) {
            Log.e(TAG, "come some error");
            e.printStackTrace();
            returnValue = Constant.ERROR;
        } finally {
            return returnValue;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        activity.progressDialog.dismiss();
        activity.listTotalNum = activity.checkedList.size() + activity.notCheckedList.size();
        activity.supposedScanNum = activity.notCheckedList.size();
        try {
            switch (result) {
                case Constant.COMPLETED:
                    if (!hasToBeScanedIdList) {
                        activity.excessFlag = false;
                        toast = Toast.makeText(activity, R.string.list_null, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        activity.excessFlag = true;
                        toast = Toast.makeText(activity, R.string.list_get, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    break;
                case Constant.FAILED:
                    toast = Toast.makeText(activity,
                            R.string.networkfail, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                case Constant.ERROR:
                    toast = Toast.makeText(activity,
                            R.string.downloadfail, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
