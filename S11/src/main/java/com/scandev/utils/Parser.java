package com.scandev.utils;

import com.scandev.model.BoxCodeMap;
import com.scandev.model.Constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    static public Boolean validateBarCode(String barCode, String arcType, String sourceFC) {
        Pattern pattern;
        Matcher matcher;
        boolean returnValue = true;

        if (barCode == null || barCode.length() == 0) return false;

        switch (arcType) {
            case Constant.ARC_TYPE_TRANS://Transfer
                pattern = Pattern.compile(Constant.REGEX_TRANS);
                matcher = pattern.matcher(barCode);
                if (!matcher.matches()) {
                    return false;
                }
                //judge the sourceFC
                /*if(!sourceFC.equals(Constant.TRANS_FC_CODE.get(barCode.charAt(3)))) {
                    return false;
                }*/
                returnValue = true;
                break;
            case Constant.ARC_TYPE_VRETURN://VReturn
                pattern = Pattern.compile(Constant.REGEX_VRETURN);
                matcher = pattern.matcher(barCode);
                if (!matcher.matches()) {
                    return false;
                }
               /* if (barCode.length() >= 19) {
                    String caseNumber = barCode.substring(barCode.length() - 6, barCode.length());
                    int currentCase = Integer.parseInt(caseNumber.substring(0, 3));
                    int totalCase = Integer.parseInt(caseNumber.substring(3));
                    if (currentCase > totalCase) return false;
                }*/
                returnValue = true;
                break;
            case Constant.ARC_TYPE_MLPS:
                pattern = Pattern.compile(Constant.REGEX_MLPS);
                matcher = pattern.matcher(barCode);
                if (matcher.matches()) return true;
                else return false;
            default:
                return true;
        }
        return returnValue;
    }

    static public String validateBoxCode(String boxCode) {
        String boxName;
        if (boxCode.length() == 0 || boxCode == null)
            return null;
        if ((boxName = BoxCodeMap.getName(boxCode)) != null)
            return boxName;
        return null;
    }
}
