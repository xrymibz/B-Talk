package com.scandev.model;

public enum Urls{
    URL_LOGIN("scan/scanLogin"),
    URL_GETCHECKLIST( "interface/getCompareList"),
    URL_GETCARTYPEBYCARRIER("scan/getcartypeBycarrier"),
    URL_GETCARNUMBERBYCARRIER("scan/getcarnumberBycarrier"),
   // URL_GETLANESByCARRIER("scan/getLanesByCarrier"),
    URL_UPLOAD("interface/scanPickup"),
    URL_HISTORY("scan/getScanHistory"),
    URL_EXCEPTION_HISTORY("scan/getExceptionHistory"),
    URL_UPLOADEXPT("interface/uploadExceptionInfo");

    private String route;

    Urls(String route) {
        this.route = route;
    }

    public String url(){
        return ServerAddr.test.getAddr() + this.route;
    }
}
