package com.scandev.model;

import com.scandev.R;

/**
 * Created by renjicui on 16/8/13.
 */
public enum BoxCodeMap {
    BOX0("C0A00", "BOX0", R.id.Box0), BOX1("C0A01", "BOX1", R.id.Box1), BOX3("C0B03", "BOX3", R.id.Box3), BOX4("C0B04", "BOX4", R.id.Box4),
    BOX5("C0B05", "BOX5", R.id.Box5), BOX6("C0B06", "BOX6", R.id.Box6), BOX7("C0B07", "BOX7", R.id.Box7), BOX8("C0B08", "BOX8", R.id.Box8),
    BOX10("C0A10", "BOX10", R.id.Box10), BOX11("C0A11", "BOX11", R.id.Box11), BOX12("C0A12", "BOX12", R.id.Box12), BOX13("C0A13", "BOX13", R.id.Box13),
    BOX14("C0A14", "BOX14", R.id.Box14), BOX15("C0A15", "BOX15", R.id.Box15), BOX16("C0A16", "BOX16", R.id.Box16), BOX17("C0A17", "BOX17", R.id.Box17),
    BOX18("C0A18", "BOX18", R.id.Box18), BOX19("C0A19", "BOX19", R.id.Box19), BOX23("C0A23", "BOX23", R.id.Box23), BOX24("C0A24", "BOX24", R.id.Box24),
    BOX31("C0A31", "BOX31", R.id.Box31), BOX32("C0A32", "BOX32", R.id.Box32), BOX99("C0A99", "BOX99", R.id.Box99), DBBOX1("DBBOX1", "DBBOX1", R.id.DBBox1),
    DBBOX2("DBBOX2", "DBBOX2", R.id.DBBox2), DBBOX3("DBBOX3", "DBBOX3", R.id.DBBox3);

    private String boxCode;
    private String dipalyName;
    private int buttonId;

    BoxCodeMap(String code, String dipalyName, int buttonId) {
        this.boxCode = code;
        this.dipalyName = dipalyName;
        this.buttonId = buttonId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getDipalyName() {
        return dipalyName;
    }

    public void setDipalyName(String dipalyName) {
        this.dipalyName = dipalyName;
    }

    public int getButtonId() {
        return buttonId;
    }

    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }

    public static int getButtonId(String boxCode) {
        for (BoxCodeMap b : BoxCodeMap.values()) {
            if (b.getDipalyName().equals(boxCode)) {
                return b.getButtonId();
            }
        }
        return 0;
    }

    public static String getName(String code) {
        for (BoxCodeMap b : BoxCodeMap.values()) {
            if (b.getBoxCode().equals(code)) {
                return b.getDipalyName();
            }
        }
        return null;
    }
}
