package com.scandev.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.scandev.ExceptionDetailActivity;
import com.scandev.ExceptionEditActivity;
import com.scandev.FullscreenPicActivity;
import com.scandev.R;
import com.scandev.S11Application;
import com.scandev.model.Constant;
import com.scandev.utils.DensityUtil;

import java.io.File;
import java.util.List;

/**
 * Created by renjicui on 16/8/26.
 */
public class PhotoCheckAdapter extends BaseAdapter {

    private List<Bitmap> eventPhotos;
    private List<String> imgPathList;
    private String picPathTemp;
    private ExceptionDetailActivity context;
    private String[] urlArray;

    public PhotoCheckAdapter(ExceptionDetailActivity context, String[] urlArray) {
        this.context = context;
        this.eventPhotos = context.eventPhotos;
        this.urlArray = urlArray;
        //this.imgPathList = context.imgPathList;
    }

    @Override
    public int getCount() {
        if(urlArray!=null)
            return urlArray.length;
        return eventPhotos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View currView = context.getLayoutInflater().inflate(R.layout.grid_photo_item, null);
        ImageView currImg = (ImageView) currView.findViewById(R.id.grid_photo_item);
        /*final Bitmap originImg = eventPhotos.get(position);
        int w = originImg.getWidth();
        int h = originImg.getHeight();

        int reqWidth = DensityUtil.dip2px(context, 64f);
        w = reqWidth>w?w:reqWidth;
        h = reqWidth>h?h:reqWidth;
        Bitmap thumbNail = Bitmap.createBitmap(originImg, 0, 0, w, h);
        currImg.setImageBitmap(originImg);*/
        context.imageLoader.displayImage(urlArray[position], currImg, S11Application.options);

        //放大图片
        currImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bigPicIntent = new Intent();
                //bigPicIntent.putExtra("bmp", originImg);
                bigPicIntent.putExtra("url", urlArray[position]);
                bigPicIntent.setClass(context, FullscreenPicActivity.class);
                context.startActivityForResult(bigPicIntent, 1);
            }
        });

        return currView;
    }
}
