package com.athishWorks.savepasswords.pojoModels;

public class PasswordsData {

    private String mWebsite;
    private String mPassword;
    private String mEmail;
    private String mID;

    public PasswordsData() {
        this.mWebsite = "";
        this.mPassword = "";
        this.mEmail = "";
    }

    public PasswordsData(String mID, String mEmail, String mWebsite, String mPassword) {
        this.mWebsite = mWebsite;
        this.mPassword = mPassword;
        this.mEmail = mEmail;
        this.mID = mID;
    }

    public PasswordsData(String mEmail, String mWebsite, String mPassword) {
        this.mWebsite = mWebsite;
        this.mPassword = mPassword;
        this.mEmail = mEmail;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmWebsite() {
        return mWebsite;
    }

    public void setmWebsite(String mWebsite) {
        this.mWebsite = mWebsite;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
