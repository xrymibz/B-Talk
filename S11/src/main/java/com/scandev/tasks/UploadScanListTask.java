package com.scandev.tasks;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.scandev.ExceptionEditActivity;
import com.scandev.R;
import com.scandev.ScanActivity;
import com.scandev.model.Constant;
import com.scandev.model.ExceptionItem;
import com.scandev.model.ExceptionType;
import com.scandev.model.Urls;
import com.scandev.utils.DataLoad;
import com.scandev.utils.GZIPInputStream;
import com.scandev.utils.Md5Util;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MyTrustManager;
import com.squareup.okhttp.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;


public class UploadScanListTask extends AsyncTask<Map<String, String>, Integer, Integer> {
    private final String TAG = "UploadScanListTask";
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private final OkHttpClient client = new OkHttpClient();
    private JSONObject scanContent;
    private int returnValue = -1;
    private ScanActivity activity;
    private String passBackMessage;
    //for test
    private Date date1;
    private Date date2;

    private String taskId;
    private String out1;

    public UploadScanListTask(ScanActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.progressDialog = ProgressDialog.show(activity, activity.getString(R.string.pleasewait),
                activity.getString(R.string.uploading), true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Integer doInBackground(Map<String, String>... params) {
        scanContent = new JSONObject();
        try {
            GenerateUploadData(params[0]);
        } catch (JSONException e) {
            System.out.println("json exception~~");
            e.printStackTrace();
        }

        System.out.println("Let's FUCK"+scanContent.toString());

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(scanContent.toString().getBytes());
            gzip.close();
           out1 = out.toString("ISO-8859-1");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.i(TAG, "begin request new");
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new MyHostnameVerifier());
            String sign = Md5Util.getMD5Str(Constant.app_secret_true + Constant.app_key_true
                    + Constant.METHOD_UPLOAD).toUpperCase();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("sign", sign)
                    .add("scanContent", out1)
                    .build();
            Request request = new Request.Builder()
                    .url(Urls.URL_UPLOAD.url())
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Accept", "application/json; q=0.5")
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .post(formBody)
                    .build();
            date1 = new Date();
            Response response = client.newCall(request).execute();
            date2 = new Date();


            if (response.isSuccessful()) {
                System.out.println(response.code());
                String string = response.body().string();
                JSONObject jsonResult = new JSONObject(string);
                passBackMessage = jsonResult.getString("message");
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

    private void GenerateUploadData(Map<String, String> datas) throws JSONException {

        JSONObject taskInfo = new JSONObject();

        String laneE = datas.get("laneE");
        String laneName = datas.get("lanename");
        String carrierAbbr = datas.get("carrierabbr");
        String sourceFC = datas.get("sourceFC");
        String destinationFC = datas.get("destinationFC");
        String arcType = datas.get("arctype");
        String carType = datas.get("carType");
        String carNumber = datas.get("carNumber");
        String time = Constant.formateDate(new Date());

        taskId = arcType.substring(0, 1) + sourceFC + destinationFC + sdf2.format(new Date());
        taskInfo.put("userId", activity.getUserId());
        taskInfo.put("source", sourceFC);
        taskInfo.put("destination", destinationFC);
        if (arcType.equals("VReturn")) taskInfo.put("sortCode", datas.get("sortcode"));
        taskInfo.put("cargoType", arcType);
        taskInfo.put("taskId", taskId);
        taskInfo.put("creDate", time);
        taskInfo.put("laneE", laneE);
        taskInfo.put("laneName", laneName);
        taskInfo.put("carrierAbbr", carrierAbbr);
        taskInfo.put("carType", carType);
        taskInfo.put("carNumber", carNumber);

      /*  String time = "2016-06-27 19:30:00";
        String sourceFC = "SHA2";
        String destinationFC = "SHE1";
        String arcType = "Transfer";
        String laneE = "TSN2-PEK3-SHE1";*/
        JSONObject scanInfo = new JSONObject();
        int i = 0;
        JSONArray scanDetails = new JSONArray();
        for (Map.Entry<String, String[]> entry : activity.scanRecordList.entrySet()) {
            JSONObject obj = new JSONObject();
            obj.put("scanId", entry.getKey());
            String[] cargoData = entry.getValue();
            obj.put("scanDatetime", cargoData[0]);
            if (cargoData[1] == null)
                obj.put("box", activity.getString(R.string.wu));
            else {
                obj.put("box", cargoData[1]);
            }
            scanDetails.put(i++, obj);
        }


        scanInfo.put("taskId", taskId);
        scanInfo.put("scanItems", scanDetails);

        System.out.println(scanDetails.toString());


        /*while(i<625){
            JSONObject obj = new JSONObject();
            obj.put("scanId", "test_num_long_for_length_enough");
            obj.put("exceptionType", ExceptionType.BARCODE_MISS);
            obj.put("description", "asdf;kljasdf;kljasdf;lkjasdfkl;j");
            exceptionDetails.put(i++,obj);
        }*/

        /*for (ExceptionItem entry : activity.exceptionItemList) {
            JSONObject obj = new JSONObject();
            obj.put("scanId", entry.getKey());
            obj.put("exceptionType",entry.getValue().getType());
            obj.put("description", entry.getValue().getDescription());
            obj.put("time", entry.getValue().getTime());
            DataLoad.changeType(activity, entry);
            exceptionDetails.put(i++, entry);
        }*/
        scanContent.put("scanInfo", scanInfo);
        scanContent.put("taskInfo", taskInfo);
        //scanContent.put("exceptionItems", exceptionDetails);
    }

    @Override
    protected void onPostExecute(Integer returnValue) {
        /*String s = "本次上传时间为" + *//*out1.getBytes().length;*//*   (date2.getTime() - date1.getTime()) / 1000 + "秒";
        Toast toast1 = Toast.makeText(activity, s
                , Toast.LENGTH_LONG);
        toast1.setGravity(Gravity.CENTER, 0, 0);
        toast1.show();*/

        if (returnValue == Constant.COMPLETED) {
            if(activity.exceptionItemList.size()!=0){
                try{
                    for (ExceptionItem entry : activity.exceptionItemList) {
                        entry.put("taskId", taskId);
                        UploadExceptionTask task = new UploadExceptionTask(TAG, activity);
                        task.execute(entry);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            //activity.progressDialog.dismiss();
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(passBackMessage);
                builder.setTitle(R.string.notify);
                builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        activity.isuploaded = true;
                        activity.finish();
                    }
                });
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (returnValue == Constant.FAILED) {
            try {
                Toast toast = Toast.makeText(activity,
                        R.string.networkfail, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        } else if (returnValue == Constant.ERROR) {
            try {
                Toast toast = Toast.makeText(activity,
                        R.string.uploadfail, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        }
        activity.progressDialog.dismiss();
    }

}
