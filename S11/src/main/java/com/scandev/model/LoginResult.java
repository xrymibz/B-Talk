package com.scandev.model;

import org.json.JSONObject;

/**
 * Created by liuyifan on 2016/11/30.
 */

public class LoginResult {
    private String AITS;
    private JSONObject linker;

    private LoginResult() {}

    public LoginResult(JSONObject linker, String AITS) {
        this.linker = linker;
        this.AITS = AITS;
    }

    public String getAITS() {
        return AITS;
    }

    public JSONObject getLinker() {
        return linker;
    }
}
