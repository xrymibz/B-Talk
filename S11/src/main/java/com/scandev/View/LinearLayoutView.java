package com.scandev.View;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by renjicui on 16/7/13.
 */
public class LinearLayoutView extends LinearLayout {
    public static final int KEYBOARD_HIDE = 0;
    public static final int KEYBOARD_SHOW = 1;
    private static final int SOFTKEYPAD_MIN_HEIGHT = 50;

    private Handler uiHandler = new Handler();
    public LinearLayoutView(Context context){
        super(context);
    }

    public LinearLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface KeyBoardStateListener{
        public void stateChange(int state);
    }

    private KeyBoardStateListener keyBoardStateListener;

    public void setKeyBoardStateListener(KeyBoardStateListener kenBoardStateListener){
        this.keyBoardStateListener = kenBoardStateListener;
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        uiHandler.post(new Runnable(){

            @Override
            public void run() {
                if(oldh - h > SOFTKEYPAD_MIN_HEIGHT){
                    keyBoardStateListener.stateChange(KEYBOARD_SHOW);
                }else{
                    if(keyBoardStateListener != null){
                        keyBoardStateListener.stateChange(KEYBOARD_HIDE);
                    }
                }
            }
        });
    }
}
