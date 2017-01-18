package com.scandev.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
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
import com.scandev.model.Constant;

import java.io.File;
import java.util.List;

public class PhotoListAdapter extends BaseAdapter {

    private List<Bitmap> eventPhotos;
    private List<String> imgPathList;
    private String picPathTemp;
    private ExceptionEditActivity context;

    public PhotoListAdapter(ExceptionEditActivity context, List<Bitmap> eventPhotos, List<String> urlArray) {
        this.context = context;
        this.eventPhotos = eventPhotos;
        this.imgPathList = urlArray;
    }

    @Override
    public int getCount() {
        if (eventPhotos.size() >= 3) {
            return eventPhotos.size();
        }
        return eventPhotos.size() + 1;
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
        if (position == eventPhotos.size()) {
            ImageView currImg = (ImageView) currView.findViewById(R.id.grid_photo_item);
            currImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_attend));
            currImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new AlertDialog.Builder(context).setTitle("选择图片")
                            .setItems(new String[]{"从相册选取", "拍照"},
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            switch (which) {
                                                case 0: {
                                                    Intent gallaryIntent = new Intent();

                                                    gallaryIntent.setAction(Intent.ACTION_PICK);
                                                    gallaryIntent.setType("image/*");
                                                    context.startActivityForResult(gallaryIntent, 0);
                                                }
                                                break;
                                                case 1: {
                                                    String status = Environment.getExternalStorageState();
                                                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                                                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                        StringBuilder sb = new StringBuilder();
                                                        String dirName, fileName;
                                                        dirName = Environment.getExternalStorageDirectory() + Constant.folderName;
                                                        fileName = System.currentTimeMillis() + ".jpg";
                                                        sb.append(dirName + "/" + fileName);
                                                        File dir = new File(dirName);
                                                        if (!dir.exists())
                                                            dir.mkdirs();
                                                        context.picPathTemp = sb.toString();
                                                        context.fileName = fileName;

                                                        Uri imageUri = Uri.fromFile(new File(dir, fileName));
                                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                                        context.startActivityForResult(cameraIntent, 1);
                                                    }
                                                }
                                                break;
                                                default:
                                                    break;

                                            }
                                        }
                                    }).show();
                }
            });
        } else {
            ImageView currImg = (ImageView) currView.findViewById(R.id.grid_photo_item);
            final Bitmap bp = eventPhotos.get(position);
            currImg.setImageBitmap(bp);

            //放大图片
            currImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String imgPath = imgPathList.get(position);
                    Intent bigPicIntent = new Intent();
                    bigPicIntent.putExtra("index", position);
                    bigPicIntent.putExtra("url", imgPathList.get(position));
                    //bigPicIntent.putExtra("bmp", bp);
                    bigPicIntent.setClass(context, FullscreenPicActivity.class);
                    context.startActivityForResult(bigPicIntent, 1);
                }
            });
        }
        return currView;
    }
}
