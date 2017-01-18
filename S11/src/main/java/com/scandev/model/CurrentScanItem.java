package com.scandev.model;

public class CurrentScanItem {
    private String barCode;
    private boolean currentCount;
    private boolean selected;
    private boolean damaged;
    private String boxCode;

    public CurrentScanItem(String barCode) {
        this.barCode = barCode;
        this.currentCount = true;
        this.selected = false;
        this.damaged = false;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public boolean isCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(boolean currentCount) {
        this.currentCount = currentCount;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

}
