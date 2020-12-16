package com.example.remotemonitoringapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetworkStatus  {

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager cManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cManager.getActiveNetworkInfo();
        return activeInfo != null &&activeInfo.isConnected();
    }

}
