package com.tas.beaconzz.Tip;
//Couerse 값(db)
public class Tip {
    String campusMap;
    String storeName;
    String storeNote;
    String storeContent;


    public String getCampusMap() {
        return campusMap;
    }

    public String getStoreNote() {
        return storeNote;
    }

    public String getStoreContent() {
        return storeContent;
    }

    public void setStoreContent(String storeContent) {
        this.storeContent = storeContent;
    }

    public void setStoreNote(String storeNote) {
        this.storeNote = storeNote;
    }

    public void setCampusMap(String campusMap) {
        this.campusMap = campusMap;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }


    public Tip(String campusMap, String storeName, String storeNote, String storeContent) {
        this.campusMap = campusMap;
        this.storeName = storeName;
        this.storeContent = storeContent;
        this.storeNote = storeNote;

    }
}