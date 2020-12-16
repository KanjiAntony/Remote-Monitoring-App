package com.example.remotemonitoringapp;

import java.util.Date;

public class User {

    String UserID;
    Date sessionExpiryDate;

    public void setUserID(String user)
    {
        this.UserID = user;
    }

    public void setSessionExpiryDate(Date expiry)
    {
        this.sessionExpiryDate = expiry;
    }

    public String getUserID()
    {
        return UserID;
    }

    public Date getSessionExpiryDate()
    {
        return sessionExpiryDate;
    }

}

