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
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus()
    {
        //判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }

    }

    /**
     * 点击左侧按钮
     * 默认是退出
     */
    protected void onClickLeft() {
        finish();
    }

    /**
     * 点击右侧按钮
     * 默认什么都不做
     */
    protected void onClickRight() {

    }

    /**
     * 设置左侧按钮显示与隐藏
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
     * 设置右侧按钮显示与隐藏
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

//直接显示
        rightBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 获取自定义标题栏
     * 如果子类复写并返回不等于0的布局文件，将会覆盖默认标题
     * 返回0 将会采用默认标题
     * @return
     */
    protected int getTitlebarResId() {
        return 0;
    }

    /**
     * 设置中间标题
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
     * 设置右边你按钮文字属性
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
     * 获取中间内容显示区
     * @return
     */
    protected abstract int getContentView();
}
