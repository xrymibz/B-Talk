package com.scandev;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;
import com.scandev.View.GridViewForScroll;
import com.scandev.View.LinearLayoutView;
import com.scandev.View.PhotoListAdapter;
import com.scandev.model.Constant;
import com.scandev.model.ExceptionItem;
import com.scandev.model.ExceptionType;
import com.scandev.tasks.UploadExceptionTask;
import com.scandev.utils.DataLoad;
import com.scandev.utils.DensityUtil;
import com.scandev.utils.ImageUtil;
import com.scandev.utils.Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExceptionEditActivity extends BaseTitleAcitvity
        implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener, LinearLayoutView.KeyBoardStateListener {
    public final static String TAG = "ExceptionEditActivity";
    private String title = "“Ï≥£±‡º≠";
    private SharedPreferences login_user;
    private TextView laneName;
    private TextView arcName;
    private Spinner exceptionType;
    private EditText barcodeInput;

    public EditText exceptionMessage;
    public TextView barcode;

    private Button confirm;
    private Button upload;
    private LinearLayout hideLayout;
    private LinearLayoutView resizeLayout;
    private LinearLayout mannualInput;
    private boolean postDamagelag;

    public ProgressDialog progressDialog = null;
    private static BarcodeReader barcodeReader;

    public ExceptionItem exception;
    private String typeString;
    public Map<String, String> datas;

    //for Picture
    public List<String> imgPathList;
    private GridViewForScroll gv;
    private PhotoListAdapter pa;
    public List<Bitmap> eventPhotos;
    int reqWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.activity_exception_edit);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        laneName = (TextView) findViewById(R.id.laneForExcept);
        arcName = (TextView) findViewById(R.id.arcForExcept);
        exceptionType = (Spinner) findViewById(R.id.exception_type);
        barcodeInput = (EditText) findViewById(R.id.nowScanIdForExcep);
        exceptionMessage = (EditText) findViewById(R.id.excepDescription);
        barcode = (TextView) findViewById(R.id.id_with_exception);
        confirm = (Button) findViewById(R.id.exception_confirm);
        upload = (Button) findViewById(R.id.exception_upload);
        resizeLayout = (LinearLayoutView) findViewById(R.id.rootLayout);
        hideLayout = (LinearLayout) findViewById(R.id.hideLayout);
        resizeLayout.setKeyBoardStateListener(this);
        mannualInput = (LinearLayout) findViewById(R.id.code_mannual_input);

        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);

        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        datas = new HashMap<>();
        DataLoad.uploadData(this, login_user, datas);

        laneName.setText(datas.get("lanename"));
        arcName.setText(datas.get("arcname"));

        //Picture
        imgPathList = new ArrayList<>();
        reqWidth = DensityUtil.dip2px(this, 256f);
        eventPhotos = new ArrayList<>();
        gv = (GridViewForScroll) findViewById(R.id.act_exception_photo);
        pa = new PhotoListAdapter(this, this.eventPhotos, this.imgPathList);
        gv.setAdapter(pa);

        barcodeReader = S11Application.barcodeReader;
        if(barcodeReader!=null){
            barcodeReader.addTriggerListener(this);
            barcodeReader.addBarcodeListener(this);
        }

        Intent intent = getIntent();
        switch (intent.getIntExtra("fluency", -1)) {
            case -1://upload single
                confirm.setVisibility(View.GONE);
                upload.setVisibility(View.VISIBLE);
                //initDevice();
                break;
            case 0://single suplement
                confirm.setVisibility(View.VISIBLE);
                upload.setVisibility(View.GONE);
                //initDevice();
                break;
            case 1://post damage
                confirm.setVisibility(View.VISIBLE);
                upload.setVisibility(View.GONE);
                mannualInput.setVisibility(View.GONE);
                barcode.setText(intent.getStringExtra("barcode"));
                postDamagelag = true;
                break;
            case 2://reedit
                ExceptionItem item = intent.getParcelableExtra("exception");
                initView(item);
                break;
            default:
                break;
        }

        exceptionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        typeString = ExceptionType.CARGO_DAMAGE;
                        break;
                    case 1:
                        typeString = ExceptionType.BARCODE_MISS;
                        break;
                    case 2:
                        typeString = ExceptionType.CARGO_EXCESS;
                        break;
                    case 3:
                        typeString = ExceptionType.OTHERS;
                        break;
                    default:
                        typeString = ExceptionType.OTHERS;
                        break;
                }
                TextView tv = (TextView) view;
                tv.setBackgroundColor(getResources().getColor(R.color.title_bg));
                tv.setTextSize(27);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        exceptionMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideLayout.setVisibility(View.GONE);
                } else {
                    hideLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_exception_edit;
    }

    private void initView(ExceptionItem item) {
        mannualInput.setVisibility(View.GONE);
        exceptionMessage.setText(item.getDescription());
        barcode.setText(item.getBarCode());
        int index = 3;
        switch (item.getType()) {
            case ExceptionType.CARGO_DAMAGE:
                index = 0;
                break;
            case ExceptionType.BARCODE_MISS:
                index = 1;
                break;
            case ExceptionType.CARGO_EXCESS:
                index = 2;
                break;
            case ExceptionType.OTHERS:
                index = 3;
                break;
        }
        exceptionType.setSelection(index);
        //ª÷∏¥“Ï≥£Õº∆¨
        String[] urls = item.getImgUris();

        for (String s : urls) {
            if (s == null) break;
            imgPathList.add(s);
            Bitmap pic = ImageUtil.decodeSampledBitmapFromPath(s, reqWidth, reqWidth);
            eventPhotos.add(pic);
        }
        gv.setAdapter(new PhotoListAdapter(this, eventPhotos, imgPathList));
    }

    @Override
    public void stateChange(int state) {
        if (state == LinearLayoutView.KEYBOARD_HIDE)
            exceptionMessage.clearFocus();
    }

    public void addToExceptionList(View v) {
        String editScanId = barcodeInput.getText().toString();
        if (editScanId.length() > 0) {
            addScanResult(editScanId);
            this.barcodeInput.setText("");
            this.barcodeInput.invalidate();
        }
    }

    public void confirm() {
        getException();

        Intent intent = new Intent();
        intent.putExtra("exception", exception);
        int resultCode = 1;
        if (postDamagelag) {
            resultCode = 2;
        }
        this.setResult(resultCode, intent);
        this.finish();
    }

    public void upload() {
        getException();
        UploadExceptionTask uploadExceptionTask = new UploadExceptionTask(TAG, ExceptionEditActivity.this);
        uploadExceptionTask.execute(exception);
    }

    public void notifyConfirm(final Button btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (btn == confirm) {
            builder.setMessage(R.string.sure_to_addexception);
        } else {
            builder.setMessage(R.string.sure_to_uploadexception);
        }
        builder.setTitle(R.string.notify);
        builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        if (btn == confirm) {
                            confirm();
                        } else {
                            upload();
                        }
                    }
                }
        );
        builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void confirmOrUpload(View v) {
        Button btn = (Button) v;
        checkExceptionInfo(btn);
    }


    private void addScanResult(String codeString) {
        //validate
        if (Parser.validateBarCode(codeString, datas.get("arctype"), datas.get("sourceFC"))) {
            barcode.setText(codeString);
            barcode.invalidate();
        } else {
            S11Application.playSound(1, 1);
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.barcode_error, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public int getUserId() {
        return login_user.getInt("userId", 0);
    }

    private void checkExceptionInfo(final Button btn) {
        exceptionMessage = (EditText) findViewById(R.id.excepDescription);
        if (barcode.getText().equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.hint_nowScan, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        String description = exceptionMessage.getText().toString();
        if (description == null || description.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.sure_no_description);
            builder.setTitle(R.string.notify);
            builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                            notifyConfirm(btn);
                        }
                    }
            );
            builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else {
            notifyConfirm(btn);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check_exphistory:
                Intent intent = new Intent();
                intent.setClass(this, ExceptionQueryActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("picture de chuli","chulistart   "+requestCode+"   " +resultCode);
        if(picPathTemp!=null){
            MediaScannerConnection.scanFile(this, new String[]
                    {Environment.getExternalStoragePublicDirectory
                            (Constant.folderName).getPath()+"/"+fileName}, null, null);
        }
        if (resultCode == RESULT_OK && requestCode == 0) {
            Uri uri = data.getData();
            String imgPath = ImageUtil.getAbsoluteImagePath(this, uri);
            imgPathList.add(imgPath);
            eventPhotos.add(ImageUtil.decodeSampledBitmapFromPath(imgPath, reqWidth, reqWidth));
            pa.notifyDataSetChanged();
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            System.out.println(picPathTemp);
            CompressImgToFileTask compressTask = new CompressImgToFileTask();
            compressTask.execute(picPathTemp);
        } else if (resultCode == 1) {
            int position = data.getIntExtra("position", -1);
            imgPathList.remove(position);
            eventPhotos.remove(position);
            pa = new PhotoListAdapter(this, this.eventPhotos, this.imgPathList);
            gv.setAdapter(pa);
        }
    }

    public String picPathTemp;
    public String fileName;

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            barcodeReader.removeBarcodeListener(this);
            barcodeReader.removeTriggerListener(this);
        }
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update UI to reflect the data
                Log.i(TAG, "Barcode data: " + barcodeReadEvent.getBarcodeData());
                Log.i(TAG, "Character Set: " + barcodeReadEvent.getCharset());
                Log.i(TAG, "Code ID: " + barcodeReadEvent.getCodeId());
                Log.i(TAG, "AIM ID: " + barcodeReadEvent.getAimId());
                Log.i(TAG, "Timestamp: " + barcodeReadEvent.getTimestamp());

                addScanResult(barcodeReadEvent.getBarcodeData());
            }
        });

    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "no data ");
            }
        });
    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {
        try {
            barcodeReader.aim(triggerStateChangeEvent.getState());
            barcodeReader.light(triggerStateChangeEvent.getState());
            barcodeReader.decode(triggerStateChangeEvent.getState());

            S11Application.scanning = triggerStateChangeEvent.getState();
        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
        }
    }

    public ExceptionItem getException() {
        exception = new ExceptionItem();

        String description = exceptionMessage.getText().toString();
        if (description.length() == 0) {
            description = getString(R.string.wu);
        }
        exception.setDescription(description);
        exception.setBarCode(barcode.getText().toString());
        exception.setType(typeString);
        exception.setTime(Constant.formateDate(new Date()));
        int i = 0;
        String[] exptUriList = new String[3];
        for (String s : imgPathList) {
            exptUriList[i++] = s;
        }
        exception.setImgUris(exptUriList);
        return exception;
    }

    private class CompressImgToFileTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d("compressBmpToFile","yasuotupian");
            Bitmap pic = ImageUtil.decodeSampledBitmapFromPath(params[0], reqWidth, reqWidth);
            File file = new File(params[0]);
            ImageUtil.compressBmpToFile(pic, file);
            System.out.println("File size = " + file.length() / 1024);
            return pic;
        }

        @Override
        protected void onPostExecute(Bitmap pic) {
            super.onPostExecute(pic);
            if (pic != null) {
                eventPhotos.add(pic);
                System.out.println("pic memory size = " + pic.getByteCount() / 1024);
                imgPathList.add(picPathTemp);
                pa.notifyDataSetChanged();
            } else {
                Toast.makeText(ExceptionEditActivity.this, "ªÒ»°’’∆¨ ß∞‹", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
