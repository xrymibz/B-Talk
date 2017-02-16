package com.scandev.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.scandev.R;
import com.scandev.model.ExceptionItem;
import com.scandev.model.ExceptionType;

import java.util.HashMap;
import java.util.Map;

public class DataLoad {
    public static void uploadData(Context context, SharedPreferences login_user, Map<String, String> uploadData) {
        uploadData.put("laneE", login_user.getString("laneE", "No LaneE"));
        uploadData.put("carrierabbr", login_user.getString("carrierAbbr", "No carrierAbbr"));
        uploadData.put("sourceFC", login_user.getString("sourceFC", null));
        uploadData.put("destinationFC", login_user.getString("destinationFC", null));
        uploadData.put("arctype", login_user.getString("arcType", null));
        uploadData.put("sortcode", login_user.getString("sortCode", null));
        uploadData.put("carriername", login_user.getString("carrierName", null));
        uploadData.put("lanename", login_user.getString("laneName", "No Lane"));
        uploadData.put("arcname", login_user.getString("arcName", "No Arc"));
        uploadData.put("carNumber",login_user.getString("carNumber", "No carNumber"));
        uploadData.put("carType",login_user.getString("carType", "No carType"));
    }

    public static void changeType(Context activity, ExceptionItem exception) {
        String newType = new String();
        switch (exception.getType()) {
            case ExceptionType.CARGO_DAMAGE:
                newType = activity.getString(R.string.CargoDamage);
                break;
            case ExceptionType.BARCODE_MISS:
                newType = activity.getString(R.string.BarcodeMiss);
                break;
            case ExceptionType.CARGO_EXCESS:
                newType = activity.getString(R.string.CargoExcess);
                break;
            case ExceptionType.OTHERS:
                newType = activity.getString(R.string.Others);
                break;
            default:
                newType = activity.getString(R.string.Others);
        }
        exception.setType(newType);
    }
}