package com.example.remotemonitoringapp;

public class aobData {
    String aob;
    String created_by;
    String received_by;
    String aob_id;

    aobData(String aob, String created_by, String received_by, String aob_id)
    {
        this.aob = aob;
        this.created_by = created_by;
        this.received_by = received_by;
        this.aob_id = aob_id;

    }
}