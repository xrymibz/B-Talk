package com.scandev;

import java.sql.SQLOutput;
import java.util.*;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.*;

import com.scandev.View.CaseScanDialog;
import com.scandev.View.PickupConfirmDialog;
import com.scandev.View.ScanAdapter;
import com.scandev.model.BoxCodeMap;
import com.scandev.model.Constant;
import com.scandev.model.CurrentScanItem;
import com.scandev.model.ExceptionItem;
import com.scandev.tasks.GetCheckListTsk;
import com.scandev.tasks.UploadScanListTask;
import com.scandev.utils.*;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

/*** honeywell *******************************/
import static com.honeywell.aidc.BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL;

public class ScanActivity extends BaseTitleAcitvity
        implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener {
    @Override
    protected int getContentView() {
        return R.layout.activity_scan;
    }
    public final static String TAG = "ScanActivity";
    private String title = "¿ªÊ¼É¨Ãè";
    //VIEW
    public EditText editTextMessage;
    public Button pickupButton;
    public EditText subNumEditText;
    public ScanAdapter adapter;
    public ListView scanList;
    public LinearLayout operator;//will be replaced by new Dialog class
    public ProgressDialog progressDialog = null;
    //data util
    private SharedPreferences login_user;
    //activity trasfer
    private Context context;
    public String nowArc;
    public Map<String, String> uploadData;
    private PickupConfirmDialog confirmDialog;
    //reserved data
    public static int scanNums = 0;
    public static int subScanNums = 0;
    public static List<CurrentScanItem> currentScanItemList = new ArrayList<>();
    public static Map<String, String[]> scanRecordList = new HashMap<>();
    public static ArrayList<ExceptionItem> exceptionItemList = new ArrayList<>();
    public static List<CurrentScanItem> selectedItems = new ArrayList<>();
    public static String prevArc = Constant.PREARCIDENTIFY;
    public boolean isuploaded = false;
    public int supposedScanNum;
    public int listTotalNum;
    //sound

    //network
    public HashSet<String> checkedList = new HashSet<>();
    public HashSet<String> notCheckedList = new HashSet<>();
    public boolean excessFlag = true;
    private GetCheckListTsk getCheckListTsk;
    private UploadScanListTask uploadScanListTask;
    /***
     * honeywell
     *******************************/
    private BarcodeReader barcodeReader;
    private boolean seeBox = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      setContentView(R.layout.activity_scan);
  //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        nowArc = login_user.getString("arcType", null) + login_user.getInt("arcId", 0);
 //       System.out.println("login_user.getString(\"arcType\", null) "+ login_user.getString("arcType", null));


        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));

        if (nowArc == null) {//!!!!!!!!!!!!!!!!!!!!!!!
            Log.e(TAG, "nowArc aquire fail");
        }

        editTextMessage = (EditText) findViewById(R.id.nowScanIdEdit);
        pickupButton = (Button) findViewById(R.id.upload);
        subNumEditText = (EditText) findViewById(R.id.num);
        scanList = (ListView) findViewById(R.id.scans);
        operator = (LinearLayout) findViewById(R.id.operator);
        if (!nowArc.equals(prevArc)) {
            currentScanItemList = new ArrayList<>();
            scanRecordList = new HashMap<>();
            exceptionItemList = new ArrayList<>();
            subScanNums = 0;
            checkedList = new HashSet<>();
            notCheckedList = new HashSet<>();
        }

        uploadData = new HashMap<>();
        DataLoad.uploadData(this, login_user, uploadData);

        getCheckListTsk = new GetCheckListTsk(this);

 //       if(!login_user.getString("arcType", "").equals("Injection"))
        getCheckListTsk.execute(uploadData);

        scanNums = currentScanItemList.size();
        pickupButton.setText(getString(R.string.upload) + "(" + scanNums + ")");
        subNumEditText.setText(subScanNums + "");
        subNumEditText.invalidate();
        context = this;
        adapter = new ScanAdapter(context, currentScanItemList);
        scanList.setAdapter(adapter);
        Intent intent = new Intent("ACTION_BAR_TRIGSCAN");
        intent.putExtra("timeout", 3);

        /*** honeywell *******************************/

        barcodeReader = S11Application.barcodeReader;
        if(barcodeReader!=null){
            barcodeReader.addBarcodeListener(this);
            barcodeReader.addTriggerListener(this);
        }

    }



    private void addBox(String barcodeData) {
        String boxCode = Parser.validateBoxCode(barcodeData);
        //???????,
        // ?????????,????????
        if (boxCode != null) {
            Intent intent = new Intent();
            intent.putExtra("boxCode", boxCode);
            intent.setAction(Constant.STATICATION);
            sendBroadcast(intent);
        } else {
            S11Application.playSound(3, 1);
        }
    }

    public void addScanResult(final String scanResult) {
        System.out.println("scanRecordList : " +scanRecordList +" checkedList  :  "+ checkedList+"   notCheckedList: "+notCheckedList);

        if (scanRecordList.containsKey(scanResult) || checkedList.contains(scanResult)) {
            S11Application.playSound(1, 1);
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.havechecked, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        System.out.println(notCheckedList.contains(scanResult)+"   arctype :   "+uploadData.get("arctype")) ;
        if (!notCheckedList.contains(scanResult)) {
            if (!Parser.validateBarCode(scanResult, uploadData.get("arctype"), uploadData.get("sourceFC"))) {
                //?????????????,????TOAST???
                S11Application.playSound(3, 1);
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.barcode_error, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if (!excessFlag||uploadData.get("arctype").equals("Injection")) {
                checkBox(scanResult);
                return;
            }
            S11Application.playSound(2, 1);
            AlertDialog.Builder builder = new Builder(this);
            builder.setMessage(R.string.suretoexcess);
            builder.setTitle(R.string.notify);
            builder.setPositiveButton(R.string.positive, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                            checkBox(scanResult);
                        }
                    }
            );
            builder.setNegativeButton(R.string.negative, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else {
            checkBox(scanResult);
        }
    }

    private void checkBox(String barCode) {
        if (uploadData.get("arctype").equals("VReturn")) {

            CaseScanDialog csDialog = new CaseScanDialog(this, barCode);
            seeBox = true;
            csDialog.show();
        } else {

            callBackRecord(barCode);
        }
        //callBackRecord(barCode);
    }

    public void callBackRecord(String barCode) {
        CurrentScanItem currentScanItem = new CurrentScanItem(barCode);
        currentScanItemList.add(0, currentScanItem);
        String[] cargoData = new String[]{Constant.formateDate(new Date()), null};

        scanRecordList.put(barCode, cargoData);
        subNumEditText.setText(++subScanNums + "");
        subNumEditText.invalidate();
        scanNums++;
        adapter.array = currentScanItemList;
        adapter.notifyDataSetChanged();
        pickupButton.setText(getString(R.string.upload) + "(" + scanNums + ")");
    }

    public void callBackRecord(String barCode, String boxCode) {
        CurrentScanItem currentScanItem = new CurrentScanItem(barCode);
        currentScanItem.setBoxCode(boxCode);
        currentScanItemList.add(0, currentScanItem);
        String[] cargoData = new String[]{Constant.formateDate(new Date()), boxCode};
        scanRecordList.put(barCode, cargoData);
        subNumEditText.setText(++subScanNums + "");
        subNumEditText.invalidate();
        scanNums++;
        adapter.array = currentScanItemList;
        adapter.notifyDataSetChanged();
        seeBox = false;//????????????????
        pickupButton.setText(getString(R.string.upload) + "(" + scanNums + ")");
    }

    public void pickup(View v) {
        Log.i(TAG, "begin to pick up scan data");
        if (scanNums == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.haventscan, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            fillData();
            confirmDialog = new PickupConfirmDialog(this);
            confirmDialog.setMessage(uploadData);
            confirmDialog.getWindow().setGravity(Gravity.TOP);
            confirmDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ExceptionItem exceptionItem;

        switch (resultCode) {
            case 1:
                if (data.getParcelableExtra("exception") != null) {
                    exceptionItem = data.getParcelableExtra("exception");
                    notifyDamageUi(exceptionItem);
                    exceptionItemList.add(exceptionItem);

                }
                fillData();
                confirmDialog = new PickupConfirmDialog(this);
                confirmDialog.setMessage(uploadData);
                confirmDialog.show();
                break;
            case 2://post damage
                if (data.getParcelableExtra("exception") != null) {
                    exceptionItem = data.getParcelableExtra("exception");
                    //got you
                    notifyDamageUi(exceptionItem);
                    exceptionItemList.add(exceptionItem);
                }
                System.out.println(exceptionItemList.toString());
                break;
            case 3:
                if (data.getParcelableArrayListExtra("data") != null) {
                    exceptionItemList = data.getParcelableArrayListExtra("data");
                    for (CurrentScanItem ct : currentScanItemList) {
                        ct.setDamaged(false);
                    }
                    for (ExceptionItem et : exceptionItemList) {
                        notifyDamageUi(et);
                    }
                }
                fillData();
                confirmDialog = new PickupConfirmDialog(this);
                confirmDialog.setMessage(uploadData);
                confirmDialog.show();
                break;
            default:
                break;
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void notifyDamageUi(ExceptionItem exceptionItem) {
        for (CurrentScanItem it : currentScanItemList) {
            if (it.getBarCode().equals(exceptionItem.getBarCode())) {
                it.setDamaged(true);
                adapter.array = currentScanItemList;
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void fillData() {
        int notScanedNum = 0;
        int excessNum = 0;

        for (String string : scanRecordList.keySet()) {
            if (notCheckedList.contains(string)) notScanedNum++;
            else {
                if (excessFlag) excessNum++;
            }
        }
        notScanedNum = notCheckedList.size() - notScanedNum;

        uploadData.put("listtotalnum", getString(R.string.listtotalnum) + listTotalNum + getString(R.string.piece));
        uploadData.put("supposedscannum", getString(R.string.supposedscannum) + supposedScanNum + getString(R.string.piece));
        uploadData.put("notscanednum", getString(R.string.notscanednum) + notScanedNum + getString(R.string.piece));
        uploadData.put("scanednum", getString(R.string.scanednum) + currentScanItemList.size() + getString(R.string.piece));
        uploadData.put("excessnum", getString(R.string.excessnum) + excessNum + getString(R.string.piece));
        uploadData.put("exceptionnum", getString(R.string.exceptionnum) + exceptionItemList.size() + getString(R.string.piece));
    }

    public void addToList(View v) {
        String editScanId = editTextMessage.getText().toString();
        if (editScanId.length() > 0) {
            addScanResult(editScanId);
            this.editTextMessage.setText("");
            this.editTextMessage.invalidate();
        }
    }

    public void zeroNum(View v) {
        if (subScanNums == 0) {
            return;
        }
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(R.string.suretozeorclear);
        builder.setTitle(R.string.notify);
        builder.setPositiveButton(R.string.positive, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();

                        for (CurrentScanItem item : currentScanItemList) {
                            if (item.isCurrentCount())
                                item.setCurrentCount(false);
                        }
                        subScanNums = 0;
                        subNumEditText.setText(subScanNums + "");
                        subNumEditText.invalidate();

                        adapter.array = currentScanItemList;
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        builder.setNegativeButton(R.string.negative, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void cancel(View v) {
        if (selectedItems.size() > 0) {
            for (CurrentScanItem item : selectedItems) {
                if (item.isSelected())
                    item.setSelected(false);
            }

            adapter.array = currentScanItemList;
            adapter.notifyDataSetChanged();
            selectedItems.clear();
        }
    }

    public void delete(View v) {
        if (selectedItems.size() > 0) {
            AlertDialog.Builder builder = new Builder(this);
            builder.setMessage(getString(R.string.suretodelete1) + selectedItems.size() +
                    getString(R.string.suretodelete2));
            builder.setTitle(R.string.notify);
            builder.setPositiveButton(R.string.positive, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                    for (CurrentScanItem item : selectedItems) {
                        if (item.isSelected()) {
                            if (item.isCurrentCount()) subScanNums--;
                            currentScanItemList.remove(item);
                            scanRecordList.remove(item.getBarCode());
                            scanNums--;
                        }
                    }
                    subNumEditText.setText(subScanNums + "");
                    subNumEditText.invalidate();
                    pickupButton.setText(getString(R.string.upload) + " (" + scanNums + ")");
                    adapter.array = currentScanItemList;
                    adapter.notifyDataSetChanged();
                    selectedItems.clear();
                }
            });
            builder.setNegativeButton(R.string.negative, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    public int getUserId() {
        return login_user.getInt("userId", 0);
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
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isuploaded)
            prevArc = nowArc;
        else
            prevArc = Constant.PREARCIDENTIFY;

        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);
            // unregister trigger state change listener,move to Application
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

                if (seeBox) {
                    addBox(barcodeReadEvent.getBarcodeData());
                } else {
                    addScanResult(barcodeReadEvent.getBarcodeData());
                }
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "no data ");
            }
        });
    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding
            barcodeReader.aim(triggerStateChangeEvent.getState());
            barcodeReader.light(triggerStateChangeEvent.getState());
            barcodeReader.decode(triggerStateChangeEvent.getState());

            S11Application.scanning = triggerStateChangeEvent.getState();
        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}
