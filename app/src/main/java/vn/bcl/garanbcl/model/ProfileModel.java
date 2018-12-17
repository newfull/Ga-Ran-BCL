package vn.bcl.garanbcl.model;

public class ProfileModel {
    Integer inbox,arrow;
    String txttrades,txthistory;

    public Integer getInbox() {
        return inbox;
    }

    public void setInbox(Integer inbox) {
        this.inbox = inbox;
    }

    public Integer getArrow() {
        return arrow;
    }

    public void setArrow(Integer arrow) {
        this.arrow = arrow;
    }

    public String getTxttrades() {
        return txttrades;
    }

    public void setTxttrades(String txttrades) {
        this.txttrades = txttrades;
    }

    public String getTxthistory() {
        return txthistory;
    }

    public void setTxthistory(String txthistory) {
        this.txthistory = txthistory;
    }

    public ProfileModel(Integer inbox, Integer arrow, String txttrades, String txthistory) {
        this.inbox = inbox;
        this.arrow = arrow;
        this.txttrades = txttrades;
        this.txthistory = txthistory;
    }
}
