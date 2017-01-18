package com.scandev;

import org.json.JSONObject;

import com.scandev.model.UpdateInfo;
import com.scandev.tasks.LoginTask;
import com.scandev.tasks.UpdateVersionService;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    public ProgressDialog progressDialog = null;

    private UpdateVersionService updateVersionService;
    private UpdateInfo info;
    private static TextView carrier = null;
    private String carried = "";
    private String userName = "";
    private String pwd = "";
    JSONObject paramData = new JSONObject();
    LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // �ص�������
     checkUpdate();
        setContentView(R.layout.activity_login);

        final String[] carrieds = getResources().getStringArray(R.array.carriers);
        carrier = (TextView) findViewById(R.id.carrierlist);
        carrier.setText(getString(R.string.pleaseselect));
        carrier.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                System.out.println("choose a carriers");
                Log.d("TAG","choose a carriers");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.selectcarrier);

                builder.setItems(carrieds, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("The Carrier you choosed is" + carrieds[which]);
                        carrier.setText(carrieds[which]);
                    }
                });
                builder.show();
            }
        });
    }

    public void login(View v) {
        carried = carrier.getText().toString();
        EditText usernameText = (EditText) findViewById(R.id.username);
        EditText pwdText = (EditText) findViewById(R.id.pwdInput);
        userName = usernameText.getText().toString();
        pwd = pwdText.getText().toString();
        System.out.println("userName:" + userName);
        System.out.println("password:" + pwd);
        System.out.println("carried:" + carried);
        try {
            paramData.put("carried", carried);        //json���ݣ�������������õģ�
            paramData.put("userName", userName);
            paramData.put("pwd", pwd);
            loginTask = new LoginTask(this);
            loginTask.execute(carried, userName, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUpdate() {
        Toast.makeText(MainActivity.this, "���ڼ��汾����..", Toast.LENGTH_SHORT).show();
        // �Զ������û���°汾 ������°汾����ʾ����
        new Thread() {
            public void run() {
                try {
                    updateVersionService = new UpdateVersionService(MainActivity.this);
                    info = updateVersionService.getUpDateInfo();
                    handler1.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            // ����и��¾���ʾ
            if (updateVersionService.isNeedUpdate()) {
                showUpdateDialog();
            } else {
                Toast.makeText(MainActivity.this, "�������°汾" + info.getVersion(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("������APP���汾" + info.getVersion());
        builder.setMessage(info.getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    downFile(info.getUrl());
                } else {
                    Toast.makeText(MainActivity.this, "SD�������ã������SD��",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    void downFile(final String url) {
        progressDialog = new ProgressDialog(MainActivity.this);    //�������������ص�ʱ��ʵʱ���½��ȣ�����û��Ѻö�
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("��������");
        progressDialog.setMessage("���Ժ�...");
        progressDialog.setProgress(0);
        progressDialog.show();
        updateVersionService.downLoadFile(url, progressDialog);
    }
}