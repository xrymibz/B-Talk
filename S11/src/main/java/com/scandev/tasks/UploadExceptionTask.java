package com.scandev.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.scandev.ExceptionEditActivity;
import com.scandev.R;
import com.scandev.model.Constant;
import com.scandev.model.ExceptionItem;
import com.scandev.model.Urls;
import com.scandev.utils.DataLoad;
import com.scandev.utils.Md5Util;
import com.scandev.utils.MyHostnameVerifier;
import com.scandev.utils.MyTrustManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class UploadExceptionTask extends AsyncTask<ExceptionItem, Integer, Integer> {
    private final String TAG = "UploadExceptionTask";
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private String actTag;
    private final OkHttpClient client = new OkHttpClient();
    private JSONObject scanContent;
    private int returnValue = -1;
    private Activity activity;
    private ExceptionEditActivity eeActivity;
    private String passBackMessage;
    private ProgressDialog dialog;
    //private String out1;

    public UploadExceptionTask(String act, Activity activity) {
        this.actTag = act;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        if (actTag.equals(ExceptionEditActivity.TAG)) {
            eeActivity = (ExceptionEditActivity) activity;
            dialog = ProgressDialog.show(activity, activity.getString(R.string.pleasewait),
                    activity.getString(R.string.uploading), true);
        }
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(ExceptionItem... params) {

        scanContent = new JSONObject();
        if (actTag.equals(ExceptionEditActivity.TAG)) {
            try {
                JSONObject taskInfo = new JSONObject();
                GenerateUploadData(eeActivity, taskInfo, eeActivity.datas);
                Log.d("taskInfo",taskInfo.toString());
                scanContent.put("taskInfo", taskInfo);
                params[0].put("taskId",taskInfo.get("taskId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        DataLoad.changeType(activity, params[0]);
        try {
            scanContent.put("exptInfo", params[0]);
        } catch (JSONException e) {
            System.out.println("json exception~~");
            e.printStackTrace();
        }

      /*  try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(scanContent.toString().getBytes());
            gzip.close();
            out1 = out.toString("ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            Log.i(TAG, "begin request new");
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new MyHostnameVerifier());

            String sign = Md5Util.getMD5Str(Constant.app_secret_true + Constant.app_key_true
                    + Constant.METHOD_UPLOAD_EXCEPTION).toUpperCase();

            MultipartBuilder multipartBuilder = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("sign", sign)
                    .addFormDataPart("scanContent", scanContent.toString());

            String[] uriList = params[0].getImgUris();
            for (int i = 0; i < uriList.length; i++) {
                if (uriList[i] == null) break;
                File file = new File(uriList[i]);
                multipartBuilder.addFormDataPart("images", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file));
                Log.d("file.getName()",uriList[i]);

            }

            Request request = new Request.Builder()
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Accept", "application/json; q=0.5")
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .url(Urls.URL_UPLOADEXPT.url())
                    .post(multipartBuilder.build())
                    .build();
            Response response = client.newCall(request).execute();
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

    private void GenerateUploadData(ExceptionEditActivity ac, JSONObject jb, Map<String, String> datas) throws JSONException {
        String laneE = datas.get("laneE");
        String laneName = datas.get("lanename");
        String carrierAbbr = datas.get("carrierabbr");
        String source = datas.get("sourceFC");
        String destination = datas.get("destinationFC");
        String cargoType = datas.get("arctype");
        String carNumber = datas.get("carNumber");
        String carType = datas.get("carType");
        String time = Constant.formateDate(new Date());

        String taskId = cargoType.substring(0, 1) + source + destination + sdf2.format(new Date());
        jb.put("userId", ac.getUserId());
        jb.put("source", source);
        jb.put("destination", destination);
        if (cargoType.equals("VReturn")) jb.put("sortCode", datas.get("sortcode"));
        jb.put("cargoType", cargoType);
        jb.put("taskId", taskId);
        jb.put("creDate", time);
        jb.put("laneE", laneE);
        jb.put("laneName", laneName);
        jb.put("carrierAbbr", carrierAbbr);
        jb.put("carNumber",carNumber);
        jb.put("carType",carType);

        /*JSONObject obj = new JSONObject();
        obj.put("scanId", activity.exception.getBarCode());
        obj.put("exceptionType",activity.exception.getType());
        obj.put("description", activity.exception.getDescription());
        obj.put("time", activity.exception.getTime());

        exceptionDetails.put(activity.exception);*/
    }

    @Override
    protected void onPostExecute(Integer returnValue) {
        //if(returnValue != Constant.COMPLETED)
        if (actTag.equals(ExceptionEditActivity.TAG)) {
            dialog.dismiss();
            //eeActivity.progressDialog.dismiss();
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(passBackMessage);
                builder.setTitle(R.string.notify);
                builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        activity.finish();
                    }
                });
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPostExecute(returnValue);
    }
}
