package com.scandev;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by xietian on 2017/2/15.
 */

public abstract class BaseTitleAcitvity extends Activity {


    public ViewGroup contentView;
    private TextView rightBtn;
    private View leftBtn;
    private TextView titltTv;
    private View titlebar;
    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTranslucentStatus();


        int titlebarResId = getTitlebarResId();
        if (titlebarResId!=0) {
            LinearLayout view=(LinearLayout) findViewById(R.id.base_view);
            view.removeViewAt(0);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ViewGroup titleView=(ViewGroup) View.inflate(this, titlebarResId, null);
            view.addView(titleView, 0,lp);
            view.setBackgroundDrawable(titleView.getBackground());
            titlebar=titleView;
        }else {
            titlebar=findViewById(R.id.base_titlebar);
            leftBtn = findViewById(R.id.comeback);
            leftBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onClickLeft();
                }
            });
            rightBtn = (TextView) findViewById(R.id.base_menu_btn);
            rightBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onClickRight();
                }
            });
            titltTv=(TextView) findViewById(R.id.base_title_tv);
        }

        contentView=(ViewGroup) findViewById(R.id.base_contentview);
        contentView.addView(View.inflate(this, getContentView(), null));
        setRightBtnVisible(false);

    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * ����״̬������״̬
     */
    private void setTranslucentStatus()
    {
        //�жϵ�ǰSDK�汾�ţ������4.4���ϣ�����֧�ֳ���ʽ״̬����
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }

    }

    /**
     * �����ఴť
     * Ĭ�����˳�
     */
    protected void onClickLeft() {
        finish();
    }

    /**
     * ����Ҳఴť
     * Ĭ��ʲô������
     */
    protected void onClickRight() {

    }

    /**
     * ������ఴť��ʾ������
     * @param visible
     */
    public void setLeftBtnVisible(Boolean visible) {
        if (leftBtn!=null) {
            if (visible) {
                leftBtn.setVisibility(View.VISIBLE);
            }else{
                leftBtn.setVisibility(View.GONE);
            }
        }

    }

    /**
     * �����Ҳఴť��ʾ������
     * @param visible
     */
    public void setRightBtnVisible(Boolean visible) {
//        if (rightBtn!=null) {
//            if (visible) {
//                rightBtn.setVisibility(View.VISIBLE);
//            }else{
//                rightBtn.setVisibility(View.GONE);
//            }
//        }

//ֱ����ʾ
        rightBtn.setVisibility(View.VISIBLE);
    }

    /**
     * ��ȡ�Զ��������
     * ������ิд�����ز�����0�Ĳ����ļ������Ḳ��Ĭ�ϱ���
     * ����0 �������Ĭ�ϱ���
     * @return
     */
    protected int getTitlebarResId() {
        return 0;
    }

    /**
     * �����м����
     * @param title
     */
    public void setTitle(String title){
        if (titltTv!=null) {
            if (titltTv!=null) {
                titltTv.setText(title);
            }
        }
    }


    /**
     * �����ұ��㰴ť��������
     * @param title
     */
    public void setRtTitle(String title){
        if (rightBtn!=null) {
            rightBtn.setText(title);
        }
    }

    public View getTitleBar(){

        return titlebar;
    }

public void comeback(View view){
    finish();
}

    /**
     * ��ȡ�м�������ʾ��
     * @return
     */
    protected abstract int getContentView();
}
