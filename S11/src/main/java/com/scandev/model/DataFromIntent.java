package com.scandev.model;

public class DataFromIntent {

    private int arcId;
    private String source;
    private String destination;
    private String cargoType;
    private String sortCode;

    public int getArcId() {
        return arcId;
    }

    public void setArcId(int arcId) {
        this.arcId = arcId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
}
