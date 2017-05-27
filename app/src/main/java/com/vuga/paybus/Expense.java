package com.vuga.paybus;

/**
 * Created by DOUGLAS on 27/03/2017.
 */
public class Expense {

    private String particular;
    private String qty;
    private String unit;
    private String total;

    public String getSync() {
        return sync;
    }
    public void setSync(String sync) {
        this.sync = sync;
    }
    public String getParticular() {
        return particular;
    }
    public void setParticular(String particular) {
        this.particular = particular;
    }
    public String getQty() {
        return qty;
    }
    public void setQty(String qty) {
        this.qty = qty;
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    private String sync;
    private String sessionID;
    private String companyID;

    public Expense(){}

    public Expense( String sessionID,String particular, String qty, String unit, String total, String sync){

        this.particular= particular;
        this.qty = qty;
        this.unit = unit;
        this.total = total;
        this.sessionID = sessionID;
        this.sync = sync;
    }

}

