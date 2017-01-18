package com.scandev.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ExceptionItem extends JSONObject implements Parcelable{
    private String barCode;
    private String description;
    private String type;
    private String time;
    private String[] imgUris;

    public ExceptionItem(){

    }

    public ExceptionItem(JSONObject object) throws JSONException {
        this.barCode = object.getString("barCode");
        this.description = object.getString("description");
        this.type = object.getString("type");
        this.time = object.getString("time");
        JSONArray array = object.getJSONArray("imgUrls");
        imgUris = new String[array.length()];
        for(int i=0;i< array.length();i++){
            imgUris[i] = array.getString(i);
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        try {
            this.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
        try {
            this.put("scanId", barCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        try {
            this.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        try {
            this.put("exceptionType", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String[] getImgUris() {
        return imgUris;
    }

    public void setImgUris(String[] imgUris) {
        this.imgUris = imgUris;
    }

    public ExceptionItem(Parcel in) {
        barCode = in.readString();
        description = in.readString();
        time = in.readString();
        type = in.readString();
        imgUris = in.createStringArray();

        setTime(time);
        setBarCode(barCode);
        setType(type);
        setDescription(description);
        setImgUris(imgUris);
    }

    public static final Creator<ExceptionItem> CREATOR = new Creator<ExceptionItem>() {
        @Override
        public ExceptionItem createFromParcel(Parcel in) {
            return new ExceptionItem(in);
        }

        @Override
        public ExceptionItem[] newArray(int size) {
            return new ExceptionItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barCode);
        dest.writeString(description);
        dest.writeString(time);
        dest.writeString(type);
        dest.writeStringArray(imgUris);
    }
}
