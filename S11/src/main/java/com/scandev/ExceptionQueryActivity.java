package com.scandev;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.scandev.model.Constant;
import com.scandev.tasks.ExceptionQueryTask;
import com.scandev.utils.DataLoad;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExceptionQueryActivity extends BaseTitleAcitvity implements AdapterView.OnItemClickListener {

    @Override
    protected int getContentView() {
        return R.layout.activity_exception_query;
    }
    private String title = "“Ï≥£≤È—Ø";
    private TextView laneName;
    private TextView arcName;
    private Button startDateButton;
    private Button endDateButton;
    private Button startTimeButton;
    private Button endTimeButton;
    public ListView listView;
    public ProgressDialog progressDialog = null;

    private SharedPreferences login_user;
    private Map<String, String> datas = new HashMap<>();
    private Calendar calendar = Calendar.getInstance();
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    public List<String[]> exceptionArray;

    private int sYear = calendar.get(Calendar.YEAR);
    private int sMon = calendar.get(Calendar.MONTH);
    private int sDay = calendar.get(Calendar.DAY_OF_MONTH);
    private int eYear = calendar.get(Calendar.YEAR);
    private int eMon = calendar.get(Calendar.MONTH);
    private int eDay = calendar.get(Calendar.DAY_OF_MONTH);
    private int sHour = 0;
    private int sMin = 0;
    private int eHour = 23;
    private int eMin = 59;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.activity_exception_query);
 //       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        laneName = (TextView) findViewById(R.id.laneName);
        arcName = (TextView) findViewById(R.id.arcName);
        startDateButton = (Button) findViewById(R.id.fromDate);
        endDateButton = (Button) findViewById(R.id.toDate);
        startTimeButton = (Button) findViewById(R.id.fromTime);
        endTimeButton = (Button) findViewById(R.id.toTime);
        listView = (ListView) findViewById(R.id.resultList);

        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        DataLoad.uploadData(this, login_user, datas);

        laneName.setText(datas.get("lanename"));
        arcName.setText(datas.get("arcname"));

        startDate = fillDate(sYear, sMon, sDay);
        endDate = fillDate(eYear, eMon, eDay);
        startTime = fillTime(sHour, sMin);
        endTime = fillTime(eHour, eMin);

        startDateButton.setText(startDate);
        endDateButton.setText(endDate);
        startTimeButton.setText(startTime);
        endTimeButton.setText(endTime);

        listView.setOnItemClickListener(this);
    }



    public void changeStartDate(View v) {
        DatePickerDialog datepicker = new DatePickerDialog(ExceptionQueryActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        sYear = year;
                        sMon = month;
                        sDay = day;
                        startDate = fillDate(year, month, day);
                        startDateButton.setText(startDate);
                    }
                }, sYear, sMon, sDay);
        datepicker.show();
    }

    public void changeEndDate(View v) {
        DatePickerDialog datepicker = new DatePickerDialog(ExceptionQueryActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        eYear = year;
                        eMon = month;
                        eDay = day;
                        endDate = fillDate(year, month, day);
                        endDateButton.setText(endDate);
                    }
                }, eYear, eMon, eDay);
        datepicker.show();
    }

    public void changeStartTime(View v) {
        TimePickerDialog timepicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime = fillTime(hourOfDay, minute);
                sHour = hourOfDay;
                sMin = minute;
                startTimeButton.setText(startTime);
            }
        }, sHour, sMin, true);
        timepicker.show();
    }

    public void changeEndTime(View v) {
        TimePickerDialog timepicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime = fillTime(hourOfDay, minute);
                eHour = hourOfDay;
                eMin = minute;
                endTimeButton.setText(endTime);
            }
        }, eHour, eMin, true);
        timepicker.show();
    }

    public void queryException(View v) {
        String startStr = startDate + " " + startTime + ":00";
        String endStr = endDate + " " + endTime + ":00";

        try {
            Date start = Constant.parse(startStr);
            Date end = Constant.parse(endStr);
            if (start.before(end)) {
                datas.put("startTime", startStr);
                datas.put("endTime", endStr);
                ExceptionQueryTask task = new ExceptionQueryTask(ExceptionQueryActivity.this);
                task.execute(datas);
            } else {
                Toast.makeText(ExceptionQueryActivity.this, R.string.time_error, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fillTime(int hour, int min) {
        String h = hour < 10 ? "0" + hour : hour + "";
        String m = min < 10 ? "0" + min : min + "";
        return h + ":" + m;
    }

    private String fillDate(int year, int mon, int day) {
        mon++;
        String m = mon < 10 ? "0" + mon : mon + "";
        String d = day < 10 ? "0" + day : day + "";
        return year + "-" + m + "-" + d;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] msg = exceptionArray.get(position);
        Intent intent = new Intent();
        intent.putExtra("message", msg);
        intent.setClass(this, ExceptionDetailActivity.class);
        startActivity(intent);
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
