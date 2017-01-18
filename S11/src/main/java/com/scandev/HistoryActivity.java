package com.scandev;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.scandev.tasks.GetScanHistoryTask;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends ActionBarActivity {
    private String TAG = "HistoryActivity";
    private EditText dateText = null;

    private SharedPreferences login_user = null;
    private Calendar calendar;
    private int userId;
    private String laneE;
    private String date;
    private Map<String, String> dataMap;
    private GetScanHistoryTask task;

    public ListView historylist = null;
    public TextView totalText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated METHOD_LOGIN stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateText = (EditText) findViewById(R.id.dateChoose);
        historylist = (ListView) findViewById(R.id.historylist);
        totalText = (TextView) findViewById(R.id.total);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        userId = login_user.getInt("userId", -1);
        laneE = login_user.getString("laneE", "");

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuffer nowDate = new StringBuffer();
        nowDate.append(year + "-")
                .append(month < 10 ? "0" + month : month)
                .append("-")
                .append(day < 10 ? "0" + day : day);
        date = nowDate.toString();
        dateText.setText(date);

        dataMap = new HashMap<>();
        dataMap.put("laneE", laneE);
        dataMap.put("userId", userId + "");
        dataMap.put("date", date);

        task = new GetScanHistoryTask(this);
        task.execute(dataMap);
    }

    public void chooseDate(View v) {
        Log.i(TAG, "Begain to choose date");

        DatePickerDialog datepicker = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // TODO Auto-generated METHOD_LOGIN stub
                //update date, add "0" when the num smaller than 10
                date = new StringBuilder().append(year).append("-")
                        .append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
                        .append("-")
                        .append((day < 10) ? "0" + day : day).toString();
                dateText.setText(date);
                dataMap.put("date", date);
                GetScanHistoryTask task = new GetScanHistoryTask(HistoryActivity.this);
                task.execute(dataMap);
                //new Thread(getHistory).start();
            }
        }
                , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datepicker.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        task.cancel(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }
}
