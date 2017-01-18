package com.scandev.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.scandev.model.Constant;
import com.scandev.model.UpdateInfo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateVersionService {
    ProgressDialog progressDialog;
    Context context;
    UpdateInfo updateInfo;

    public UpdateVersionService(Context context){
        this.context=context;
    }

    public UpdateInfo getUpDateInfo() throws Exception {
        String path = Constant.URL_UPDATE + "/update.txt";
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {

            // 创建一个url对象
            URL url = new URL(path);
            // 通過url对象，创建一个HttpURLConnection对象（连接）
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestProperty("contentType", "");
            urlConnection.setRequestProperty("Content-type", "text/html");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            // 通过HttpURLConnection对象，得到InputStream
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "UTF-8"));
            // 使用io流读取文件
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String info = sb.toString();
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setVersion(info.split("&")[0]);
        updateInfo.setDescription(info.split("&")[1]);
        updateInfo.setUrl(info.split("&")[2]);
        this.updateInfo=updateInfo;
        return updateInfo;
    }

    public boolean isNeedUpdate(){
        String new_version = updateInfo.getVersion(); // 最新版本的版本号
        //获取当前版本号
        String now_version="";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            now_version= packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (new_version.equals(now_version)) {
            return false;
        } else {
            return true;
        }
    }

    public void downLoadFile(final String url,final ProgressDialog pDialog){
        DownloadVersionTask task = new DownloadVersionTask(pDialog);
        task.execute(url);
    }

    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath(), "S11.apk")),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private class DownloadVersionTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressBar;

        public DownloadVersionTask(ProgressDialog progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            progressBar.setProgress(progress);
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(params[0]).build();
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {

                    byte[] buf = new byte[2048];
                    int len = 0;

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(
                            Environment.getExternalStorageDirectory().getAbsolutePath(),
                            "S11.apk");
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        publishProgress(progress);
                    }
                    fos.flush();
                }
                Log.d("h_bl", "文件下载成功");
            } catch (Exception e) {
                Log.d("h_bl", "文件下载失败");
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {

                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {

                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.cancel();
            update();
        }
    }
}

/*public void downLoadFile(final String url,final ProgressDialog pDialog,Handler h){
        progressDialog=pDialog;
        handler=h;
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    progressDialog.setMax(length);                            //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "Test.apk");
                        fileOutputStream = new FileOutputStream(file);
                        //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        //看不出progressbar的效果。
                        byte[] buf = new byte[10];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            progressDialog.setProgress(process);       //这里就是关键的实时更新进度了！
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }*/

    /*void down() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog.cancel();
                update();
            }
        });
    }*/
