package com.scandev;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScanOrManualAty extends Activity {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_or_manual);

        mTextView = (TextView) findViewById(R.id.result);

        Button mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(ScanOrManualAty.this, InfraredDetailAty.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //?§Ø??õô?????????????????????????
        //PDA?õô????????Glory50??
        if (!Build.MODEL.equals("Glory50")) {
            mButton.setEnabled(false);
        }

//		Button mButton1 = (Button) findViewById(R.id.button1);
//		mButton1.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(ScanOrManualAty.this, CaptureAty.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//				//startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
//			}
//		});


        Button mButton2 = (Button) findViewById(R.id.button2);
        mButton2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ScanOrManualAty.this, ManualSearchAty.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * listen back_key???????????
     */
    @SuppressWarnings("deprecation")
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // ????????????  
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // ???????????  
            isExit.setTitle("?????");
            // ???????????  
            isExit.setMessage("??????????");
            // ???????????????  
            isExit.setButton("???", listener);
            isExit.setButton2("???", listener);
            // ????????  
            isExit.show();
        }

        return false;
    }

    /**
     * ??????????????button??????
     */

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "???"??????????

                    //finish();
                    System.exit(0);
                    //android.os.Process.killProcess(android.os.Process.myPid());
                    //ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                    //am.killBackgroundProcesses("QR_CodeScan");
                    //am.restartPackage("packagename");

                    break;

                case AlertDialog.BUTTON_NEGATIVE:// "???"????????????????

                    break;

                default:

                    break;

            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    //?????Úb??????
                    mTextView.setText(bundle.getString("result"));
                    //???
                    //	mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }
}

