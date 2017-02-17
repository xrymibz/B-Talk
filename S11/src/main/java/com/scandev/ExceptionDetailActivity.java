package com.scandev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.scandev.View.GridViewForScroll;
import com.scandev.View.PhotoCheckAdapter;
import com.scandev.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class ExceptionDetailActivity extends BaseTitleAcitvity {

    @Override
    protected int getContentView() {return R.layout.activity_exception_detail;}
    private String title = "“Ï≥£œÍ«È";
    SharedPreferences login_user;
    private TextView barCodeView;
    private TextView timeView;
    private TextView typeView;
    private TextView descriptionView;
    private GridViewForScroll gv;
    public List<Bitmap> eventPhotos;

    public ImageLoader imageLoader;
    private ImageSize imageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.activity_exception_detail);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        login_user = getSharedPreferences("login_user", Activity.MODE_PRIVATE);
        setTitle(title);
        setRtTitle(login_user.getString("carrierName",""));
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        ImageLoader.getInstance().init(configuration);

        imageLoader = ImageLoader.getInstance();

        imageSize = new ImageSize(DensityUtil.dip2px(this, 64f), DensityUtil.dip2px(this, 64f));

        barCodeView = (TextView) findViewById(R.id.id_with_exception);
        timeView = (TextView) findViewById(R.id.exception_time);
        typeView = (TextView) findViewById(R.id.exception_type);
        descriptionView = (TextView) findViewById(R.id.excepDescription);
        gv = (GridViewForScroll) findViewById(R.id.detail_exception_photo);

        Intent intent = getIntent();
        String[] msg = intent.getStringArrayExtra("message");
        String urls = msg[4];
        String[] urlArray = urls.split("\\t");
        /*for(String s : urlArray){
            Bitmap bmp = imageLoader.loadImageSync(s, imageSize);
            eventPhotos.add(bmp);
        }*/

        gv.setAdapter(new PhotoCheckAdapter(this, urlArray));

        /*LoadPictureTask task = new LoadPictureTask(this);
        task.execute(urlArray);*/

        barCodeView.setText(msg[0]);
        timeView.setText(msg[1]);
        //typeView.setText(getType(msg[2]));
        typeView.setText(msg[2]);
        descriptionView.setText(msg[3]);

    }



    public void back(View v) {
        ExceptionDetailActivity.this.finish();
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

    private String getType(String expType) {
        String s;
        switch (expType) {
            case "CargoDamage":
                s = this.getString(R.string.CargoDamage);
                break;
            case "CargoExcess":
                s = this.getString(R.string.CargoExcess);
                break;
            case "BarcodeMiss":
                s = this.getString(R.string.BarcodeMiss);
                break;
            case "Others":
                s = this.getString(R.string.Others);
                break;
            default:
                s = this.getString(R.string.Others);
                break;
        }
        return s;
    }

    public class LoadPictureTask extends AsyncTask<String[], Integer, Integer> {

        ExceptionDetailActivity context;

        public LoadPictureTask(ExceptionDetailActivity context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String[]... params) {
            eventPhotos = new ArrayList<>();
            String[] urlArray = params[0];
            for(String s : urlArray){
                Bitmap bmp = imageLoader.loadImageSync(s, imageSize);
                eventPhotos.add(bmp);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            gv.setAdapter(new PhotoCheckAdapter(context, null));
            super.onPostExecute(integer);
        }
    }
}
