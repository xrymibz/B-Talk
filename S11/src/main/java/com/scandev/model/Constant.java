package com.scandev.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Constant {

    public static String folderName = "/DCIM/S11";

    public static final String PREARCIDENTIFY = "PREARCIDENTIFY";

    public static final String app_key_true = "S11_2016";
    public static final String app_secret_true = "S11_linehaul_2016";

    public static final String METHOD_LOGIN = "scanLogin";
    public static final String METHOD_UPLOAD = "scanPickup";
    public static final String METHOD_GETCOMPARELIST = "getCompareList";
    public static final String METHOD_HISTORY = "getScanHistory";
    public static final String METHOD_EXCEPTION_HISTORY = "getExceptionHistory";
    public static final String METHOD_UPLOAD_EXCEPTION = "uploadExceptionInfo";

    private static String laneConfigServer = "https://54.223.193.245:8443/laneconfig/";
    //private static String laneConfigServer = "http://9801a78c0ccf.ant.amazon.com:8080/";
    public static final String URL_GETLANE = laneConfigServer + "getLanesByCarrier";
    public static final String URL_GETARC = laneConfigServer + "getArcsByLane";
    public static final String URL_UPDATE = "https://s3.cn-north-1.amazonaws.com.cn/test-fengxion/S11-Update";
    public static final String URL_UPDATE_XIETIAN ="https://s3-ap-northeast-1.amazonaws.com/s11-upload";

    public static final int COMPLETED = 1;
    public static final int FAILED = 0;
    public static final int ERROR = -1;

    public static final String REGEX_VRETURN = "^(\\d{13}|\\d{20}|\\d{14}|\\*(\\d{20}|0{5}\\d{14})\\*)$";
    public static final String REGEX_TRANS = "^(csX[A-Za-z0-9]\\d[A-Za-z0-9]{6}R{0,1}|tsX\\d{8}R{0,1})$";
    public static final String REGEX_MLPS = "^(\\d{12}|\\d{13}|\\d{14}|PT\\d{6})$";

    public static final String ARC_TYPE_VRETURN = "VReturn";
    public static final String ARC_TYPE_MLPS = "MLPS";
    public static final String ARC_TYPE_TRANS = "Transfer";
    public static final String ARC_TYPE_Injection = "Injection";

    public static String formateDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date parse(String dateStr) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(dateStr);
    }

    public static final String STATICATION = "s11.broadcase";

    public static final ConcurrentHashMap<Character, String> TRANS_FC_CODE = new ConcurrentHashMap() {
        {
            put('p', "PEK3");
            put('m', "PEK5");
            put('t', "TSN2");
            put('h', "WUH2");
            put('k', "SHA2");
            put('g', "CAN1");
            put('c', "CTU1");
            put('f', "SHA3");
            put('e', "SHE1");
            put('a', "XIY2");
            put('x', "XMN2");
            put('n', "NNG1");
            put('O', "HRB1");
            put('q', "CAN4");
            put('J', "TNA1");
        }
    };
}
